package com.example.aiticketassistant.application.assistant;

import com.example.aiticketassistant.application.agent.AiClientPort;
import com.example.aiticketassistant.application.agent.AiRequest;
import com.example.aiticketassistant.application.agent.AiResponse;
import com.example.aiticketassistant.application.agent.IntentAgent;
import com.example.aiticketassistant.application.agent.IntentResult;
import com.example.aiticketassistant.application.agent.KnowledgeAgent;
import com.example.aiticketassistant.application.agent.OrderAgent;
import com.example.aiticketassistant.application.assembler.AssistantResponseAssembler;
import com.example.aiticketassistant.application.tool.ToolDispatcher;
import com.example.aiticketassistant.application.tool.ToolMetadata;
import com.example.aiticketassistant.application.tool.ToolNames;
import com.example.aiticketassistant.application.tool.ToolRegistry;
import com.example.aiticketassistant.application.tool.ToolResult;
import com.example.aiticketassistant.application.workflow.WorkflowContext;
import com.example.aiticketassistant.application.workflow.WorkflowStep;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.stereotype.Service;

@Service
public class AssistantWorkflowService {
    private static final Pattern ORDER_NO_PATTERN = Pattern.compile("\\bORD-[A-Za-z0-9-]+\\b");

    private final IntentAgent intentAgent;
    private final OrderAgent orderAgent;
    private final KnowledgeAgent knowledgeAgent;
    private final ToolDispatcher toolDispatcher;
    private final ToolRegistry toolRegistry;
    private final AssistantResponseAssembler responseAssembler;
    private final AiClientPort aiClient;

    public AssistantWorkflowService(IntentAgent intentAgent, OrderAgent orderAgent, KnowledgeAgent knowledgeAgent,
                                    ToolDispatcher toolDispatcher, ToolRegistry toolRegistry,
                                    AssistantResponseAssembler responseAssembler, AiClientPort aiClient) {
        this.intentAgent = intentAgent;
        this.orderAgent = orderAgent;
        this.knowledgeAgent = knowledgeAgent;
        this.toolDispatcher = toolDispatcher;
        this.toolRegistry = toolRegistry;
        this.responseAssembler = responseAssembler;
        this.aiClient = aiClient;
    }

    public Flux<AssistantEvent> stream(AssistantRequest request) {
        return Flux.defer(() -> {
            WorkflowContext context = new WorkflowContext(request.sessionId(), request.customerId(), request.orderNo(), request.message());
            return runWorkflow(context)
                    .onErrorResume(ex -> Flux.just(AssistantEvent.of(AssistantEventType.ERROR, ex.getMessage(), Map.of("error", ex.getClass().getSimpleName()))));
        });
    }

    private Flux<AssistantEvent> runWorkflow(WorkflowContext context) {
        if (!shouldUseAgentWorkflow(context.userMessage())) {
            return runDirectQwen(context);
        }
        return Flux.concat(
                Flux.just(
                        AssistantEvent.of(AssistantEventType.WORKFLOW_STARTED, "Workflow started", Map.of(
                                "traceId", context.trace().traceId(),
                                "sessionId", context.sessionId())),
                        AssistantEvent.of(AssistantEventType.AGENT_STARTED, "Intent Agent 正在识别意图", Map.of("agent", "IntentAgent"))),
                runIntentAgent(context).flatMapMany(intent -> Flux.concat(
                        runTools(context, intent),
                        Flux.defer(() -> runContextAgentsAndFinalAnswer(context)))));
    }

    private Mono<IntentResult> runIntentAgent(WorkflowContext context) {
        return Mono.fromCallable(() -> {
                    WorkflowStep intentStep = context.trace().startStep("IntentAgent", "agent", Map.of("message", context.userMessage()));
                    IntentResult intent = intentAgent.analyze(context, toolMetadataJson());
                    context.intent(intent);
                    context.trace().complete(intentStep, Map.of("intent", intent.intent().name(), "confidence", intent.confidence()));
                    return intent;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Flux<AssistantEvent> runTools(WorkflowContext context, IntentResult intent) {
        List<AssistantEvent> events = new ArrayList<>();
        events.add(AssistantEvent.of(AssistantEventType.AGENT_COMPLETED, "Intent Agent 已完成", Map.of(
                "agent", "IntentAgent",
                "intent", intent.intent().name(),
                "title", intent.title(),
                "priority", intent.priority().name(),
                "confidence", intent.confidence())));
        if (intent.toolCalls().isEmpty()) {
            return Flux.fromIterable(events);
        }
        events.add(AssistantEvent.of(AssistantEventType.TOOL_CALL, "AI requested tool calls", Map.of("toolCalls", intent.toolCalls())));
        events.add(AssistantEvent.of(AssistantEventType.AGENT_STARTED, "ToolDispatcher 正在执行工具", Map.of("toolCount", intent.toolCalls().size())));
        return Flux.concat(
                Flux.fromIterable(events),
                Mono.fromCallable(() -> {
                            WorkflowStep toolStep = context.trace().startStep("ToolDispatcher", "tool", Map.of("count", intent.toolCalls().size()));
                            List<ToolResult> results = toolDispatcher.dispatchAll(intent.toolCalls(), context);
                            context.trace().complete(toolStep, Map.of("results", results));
                            return results;
                        })
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMapMany(results -> Flux.fromIterable(toolEvents(results))));
    }

    private Flux<AssistantEvent> runContextAgentsAndFinalAnswer(WorkflowContext context) {
        boolean needsOrderAnalysis = hasSuccessfulToolResult(context, ToolNames.QUERY_ORDER);
        boolean needsKnowledgeAnswer = hasSuccessfulToolResult(context, ToolNames.SEARCH_KNOWLEDGE);
        return Flux.concat(
                contextAgentStartedEvents(needsOrderAnalysis, needsKnowledgeAnswer),
                runOrderAgent(context, needsOrderAnalysis)
                        .flatMapMany(orderAnalysis -> runKnowledgeAgent(context, needsKnowledgeAnswer)
                                .flatMapMany(knowledgeAnswer -> Flux.concat(
                                        contextEvents(context, needsOrderAnalysis, needsKnowledgeAnswer, orderAnalysis, knowledgeAnswer),
                                        streamText(responseAssembler.assemble(context, orderAnalysis, knowledgeAnswer)),
                                        Flux.just(AssistantEvent.of(AssistantEventType.TRACE, "Workflow trace", Map.of(
                                                "traceId", context.trace().traceId(),
                                                "steps", context.trace().steps())))))));
    }

    private Flux<AssistantEvent> contextAgentStartedEvents(boolean needsOrderAnalysis, boolean needsKnowledgeAnswer) {
        List<AssistantEvent> events = new ArrayList<>();
        if (needsOrderAnalysis || needsKnowledgeAnswer) {
            List<String> agents = new ArrayList<>();
            if (needsOrderAnalysis) {
                agents.add("OrderAgent");
                events.add(AssistantEvent.of(AssistantEventType.AGENT_STARTED, "Order Agent 正在分析订单上下文", Map.of("agent", "OrderAgent")));
            }
            if (needsKnowledgeAnswer) {
                agents.add("KnowledgeAgent");
                events.add(AssistantEvent.of(AssistantEventType.AGENT_STARTED, "Knowledge Agent 正在检索并生成回答", Map.of("agent", "KnowledgeAgent")));
            }
            events.add(0, AssistantEvent.of(AssistantEventType.AGENT_STARTED, "上下文 Agent 正在分析", Map.of("agents", agents)));
        }
        return Flux.fromIterable(events);
    }

    private Flux<AssistantEvent> contextEvents(WorkflowContext context, boolean needsOrderAnalysis, boolean needsKnowledgeAnswer,
                                                String orderAnalysis, String knowledgeAnswer) {
        List<AssistantEvent> events = new ArrayList<>();
        if (needsOrderAnalysis || needsKnowledgeAnswer) {
            events.add(AssistantEvent.of(AssistantEventType.AGENT_COMPLETED, "上下文 Agent 已完成", Map.of(
                    "orderAnalysis", orderAnalysis == null ? "" : orderAnalysis,
                    "knowledgeAnswer", knowledgeAnswer == null ? "" : knowledgeAnswer)));
        }
        if (!context.knowledgeResults().isEmpty()) {
            events.add(AssistantEvent.of(AssistantEventType.KNOWLEDGE_SOURCES, "RAG sources retrieved", Map.of("sources", context.knowledgeResults())));
        }
        return Flux.fromIterable(events);
    }

    private Mono<String> runOrderAgent(WorkflowContext context, boolean enabled) {
        if (!enabled) {
            return Mono.just("");
        }
        return Mono.fromCallable(() -> {
                    WorkflowStep orderStep = context.trace().startStep("OrderAgent", "agent", Map.of("tool", ToolNames.QUERY_ORDER));
                    String orderAnalysis = orderAgent.analyze(context);
                    context.trace().complete(orderStep, Map.of("summary", orderAnalysis));
                    return orderAnalysis;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<String> runKnowledgeAgent(WorkflowContext context, boolean enabled) {
        if (!enabled) {
            return Mono.just("");
        }
        return Mono.fromCallable(() -> {
                    WorkflowStep knowledgeStep = context.trace().startStep("KnowledgeAgent", "agent", Map.of("tool", ToolNames.SEARCH_KNOWLEDGE));
                    String knowledgeAnswer = knowledgeAgent.synthesize(context);
                    context.trace().complete(knowledgeStep, Map.of("summary", knowledgeAnswer));
                    return knowledgeAnswer;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<AssistantEvent> toolEvents(List<ToolResult> results) {
        List<AssistantEvent> events = new ArrayList<>();
        events.add(AssistantEvent.of(AssistantEventType.TOOL_RESULT, "ToolDispatcher 已执行工具", Map.of("results", results)));
        results.stream()
                .filter(result -> result.success() && "CREATE_TICKET".equals(result.tool()))
                .findFirst()
                .ifPresent(result -> events.add(AssistantEvent.of(AssistantEventType.TICKET_CREATED, "Ticket created", Map.of("ticket", result.data()))));
        results.stream()
                .filter(result -> result.success() && "UPDATE_TICKET".equals(result.tool()))
                .findFirst()
                .ifPresent(result -> events.add(AssistantEvent.of(AssistantEventType.TICKET_UPDATED, "Ticket updated", Map.of("ticket", result.data()))));
        results.stream()
                .filter(result -> result.success() && ToolNames.CREATE_ORDER.equals(result.tool()))
                .findFirst()
                .ifPresent(result -> events.add(AssistantEvent.of(AssistantEventType.ORDER_CONFIRMATION_REQUIRED, "Order confirmation required", Map.of("confirmation", result.data()))));
        return events;
    }

    private boolean hasSuccessfulToolResult(WorkflowContext context, String toolName) {
        return context.toolResults().stream()
                .anyMatch(result -> result.success() && toolName.equals(result.tool()));
    }

    private Flux<AssistantEvent> runDirectQwen(WorkflowContext context) {
        return Mono.fromCallable(() -> aiClient.complete(new AiRequest(
                        "DirectQwen",
                        """
                                你是电商客服助手。直接回答普通问答、预算计算、简单商品推理和通用售后知识。
                                不要声称已经查询用户订单、账户、地址、支付方式，也不要声称已经下单、改购物车、退款、取消订单或创建工单。
                                如果用户要求真实业务动作或查询个人数据，请提示需要进入业务流程。
                                """,
                        "User message: %s".formatted(context.userMessage()),
                        Map.of("customerId", context.customerId(), "orderNo", context.orderNo() == null ? "" : context.orderNo()),
                        false))
                .text())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(this::streamText);
    }

    private boolean shouldUseAgentWorkflow(String message) {
        String value = message == null ? "" : message;
        return ORDER_NO_PATTERN.matcher(value).find()
                || isCatalogFactQuestion(value)
                || isPurchaseAction(value)
                || containsAny(value, "加入购物车", "加购物车", "放购物车", "移除购物车", "删除购物车", "清空购物车")
                || containsAny(value, "我的订单", "查我的订单", "查询我的订单", "帮我查订单", "帮我查一下我的订单", "我有几个订单", "全部订单", "所有订单")
                || containsAny(value, "我的账户", "我的地址", "收货地址", "支付方式", "余额", "积分", "个人信息")
                || containsAny(value, "取消订单", "确认收货", "申请退款", "帮我退款", "修改地址")
                || containsAny(value, "创建工单", "提交工单", "帮我开工单", "更新工单", "关闭工单", "升级工单", "我要投诉");
    }

    private boolean isCatalogFactQuestion(String value) {
        return containsAny(value,
                "商品目录", "服装目录", "商品编号", "编号", "价格", "多少钱", "多少元", "刚好",
                "便宜", "更便宜", "低于", "小于", "少于", "以内", "不超过", "预算", "预算有限", "哪几种", "哪些", "有什么")
                && containsAny(value, "衣服", "服装", "商品", "T恤", "衬衫", "牛仔裤", "卫衣", "连衣裙", "夹克", "半身裙", "Polo", "毛衣", "大衣");
    }

    private boolean isPurchaseAction(String value) {
        return !isBudgetQuestion(value)
                && containsAny(value,
                "买一件", "买一条", "买一个", "买一套", "买两件", "买2件", "买3件",
                "我要买", "我想买", "想买", "要买", "帮我买", "给我买",
                "购买", "立即购买", "下单", "帮我下单", "我要下单", "创建订单", "确认下单",
                "来一件", "来一条", "来一个", "来一套", "入手", "拿一件", "拿一条", "拿一个");
    }

    private boolean isBudgetQuestion(String value) {
        return containsAny(value, "最多", "几件", "多少件", "买几件", "能买几件", "可以买几件", "预算", "搭配", "推荐", "建议", "比较", "怎么选");
    }

    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private Flux<AssistantEvent> streamText(String text) {
        List<String> chunks = Arrays.stream(text.split("(?<=。|\n)"))
                .filter(chunk -> !chunk.isEmpty())
                .toList();
        if (chunks.isEmpty()) {
            return Flux.just(AssistantEvent.of(AssistantEventType.FINAL, text, Map.of("delta", text, "done", true)));
        }
        StringBuilder full = new StringBuilder();
        return Flux.fromIterable(chunks)
                .delayElements(Duration.ofMillis(90))
                .map(chunk -> {
                    full.append(chunk);
                    return AssistantEvent.of(AssistantEventType.FINAL, chunk, Map.of("delta", chunk, "done", false));
                })
                .concatWith(Mono.fromSupplier(() -> AssistantEvent.of(AssistantEventType.FINAL, full.toString(), Map.of("fullText", full.toString(), "done", true))));
    }

    private String toolMetadataJson() {
        return toolRegistry.metadata().stream()
                .map(this::formatTool)
                .collect(Collectors.joining("\n"));
    }

    private String formatTool(ToolMetadata metadata) {
        return "- %s: %s input=%s result=%s".formatted(metadata.name(), metadata.description(), metadata.inputSchema(), metadata.resultSchema());
    }
}
