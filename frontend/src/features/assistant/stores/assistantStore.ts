import { create } from 'zustand'
import type { AgentCard, AssistantEvent, OrderConfirmationPayload, OrderCreationResult, SourceCard, ToolCard } from '../types'

type AssistantState = {
  connected: boolean
  streaming: boolean
  userMessage: string
  answer: string
  events: AssistantEvent[]
  agents: AgentCard[]
  tools: ToolCard[]
  sources: SourceCard[]
  ticket?: unknown
  pendingOrderConfirmation?: OrderConfirmationPayload
  orderCreationResult?: OrderCreationResult
  orderConfirmationError?: string
  setUserMessage: (value: string) => void
  start: () => void
  stop: () => void
  reset: () => void
  setOrderCreationResult: (result: OrderCreationResult) => void
  setOrderConfirmationError: (message?: string) => void
  clearOrderConfirmation: () => void
  ingest: (event: AssistantEvent) => void
}

const initialAgents: AgentCard[] = [
  { name: 'Intent Agent', status: 'idle', detail: '意图识别 / 参数抽取 / Tool Routing' },
  { name: 'Order Agent', status: 'idle', detail: '订单与工单业务决策' },
  { name: 'Knowledge Agent', status: 'idle', detail: 'RAG 混合检索与知识汇总' }
]

function toSourceCard(value: unknown): SourceCard {
  if (!value || typeof value !== 'object') return {}
  const source = value as Record<string, unknown>
  const chunk = source.chunk && typeof source.chunk === 'object' ? source.chunk as Record<string, unknown> : source
  return {
    id: typeof chunk.id === 'string' ? chunk.id : undefined,
    title: typeof chunk.title === 'string' ? chunk.title : undefined,
    source: typeof chunk.source === 'string' ? chunk.source : undefined,
    content: typeof chunk.content === 'string' ? chunk.content : undefined,
    score: typeof source.finalScore === 'number' ? source.finalScore : typeof source.score === 'number' ? source.score : undefined,
    matchReason: typeof source.matchReason === 'string' ? source.matchReason : undefined
  }
}

export const useAssistantStore = create<AssistantState>((set) => ({
  connected: false,
  streaming: false,
  userMessage: '支付成功但订单没生成怎么办',
  answer: '',
  events: [],
  agents: initialAgents,
  tools: [],
  sources: [],
  setUserMessage: (value) => set({ userMessage: value }),
  start: () => set({ connected: true, streaming: true, answer: '', events: [], tools: [], sources: [], pendingOrderConfirmation: undefined, orderCreationResult: undefined, orderConfirmationError: undefined, agents: initialAgents }),
  stop: () => set({ connected: false, streaming: false }),
  reset: () => set({ answer: '', events: [], tools: [], sources: [], ticket: undefined, pendingOrderConfirmation: undefined, orderCreationResult: undefined, orderConfirmationError: undefined, agents: initialAgents }),
  setOrderCreationResult: (result) => set({ orderCreationResult: result, pendingOrderConfirmation: undefined, orderConfirmationError: undefined }),
  setOrderConfirmationError: (message) => set({ orderConfirmationError: message }),
  clearOrderConfirmation: () => set({ pendingOrderConfirmation: undefined, orderConfirmationError: undefined }),
  ingest: (event) => set((state) => {
    const next = { ...state, events: [...state.events, event] }
    if (event.type === 'agent_started') {
      next.agents = state.agents.map((agent) => event.message.includes(agent.name.split(' ')[0]) || event.message.includes('并行') ? { ...agent, status: 'running' } : agent)
    }
    if (event.type === 'agent_completed') {
      next.agents = state.agents.map((agent) => ({ ...agent, status: agent.status === 'running' ? 'done' : agent.status }))
    }
    if (event.type === 'tool_call') {
      next.tools = [...state.tools, { name: 'ToolDispatcher', status: 'requested', payload: event.payload }]
    }
    if (event.type === 'tool_result') {
      next.tools = [...state.tools, { name: 'Tool Result', status: 'success', payload: event.payload }]
    }
    if (event.type === 'ticket_created' || event.type === 'ticket_updated') {
      next.ticket = event.payload.ticket
    }
    if (event.type === 'order_confirmation_required') {
      const confirmation = event.payload.confirmation
      if (confirmation && typeof confirmation === 'object') {
        next.pendingOrderConfirmation = confirmation as OrderConfirmationPayload
      }
    }
    if (event.type === 'knowledge_sources') {
      const raw = event.payload.sources
      next.sources = Array.isArray(raw) ? raw.map(toSourceCard) : []
    }
    if (event.type === 'final') {
      const delta = event.payload.delta
      const done = event.payload.done
      if (typeof delta === 'string') next.answer = state.answer + delta
      if (done === true) next.streaming = false
    }
    if (event.type === 'error') {
      next.streaming = false
      next.agents = state.agents.map((agent) => agent.status === 'running' ? { ...agent, status: 'error' } : agent)
    }
    return next
  })
}))
