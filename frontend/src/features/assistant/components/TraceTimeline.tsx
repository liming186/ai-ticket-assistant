import { useEffect, useMemo, useRef } from 'react'
import { GitBranch } from 'lucide-react'
import { useAssistantStore } from '../stores/assistantStore'

const labels: Record<string, string> = {
  workflow_started: '工作流启动',
  agent_started: 'Agent 开始',
  agent_completed: 'Agent 完成',
  tool_call: '工具请求',
  tool_result: '工具结果',
  knowledge_sources: 'RAG 来源',
  order_confirmation_required: '订单确认',
  final: '答案流',
  trace: 'Trace 汇总',
  error: '错误'
}

export function TraceTimeline() {
  const allEvents = useAssistantStore((state) => state.events)
  const events = useMemo(() => allEvents.filter((event) => event.type !== 'final'), [allEvents])
  const listRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    listRef.current?.scrollTo({ top: listRef.current.scrollHeight, behavior: 'smooth' })
  }, [events.length])

  return (
    <section className="glass rounded-[2rem] p-5">
      <div className="mb-4 flex items-center justify-between gap-2">
        <div className="flex items-center gap-2">
          <GitBranch size={18} className="text-slate-300" />
          <h3 className="font-semibold text-white">Workflow Trace</h3>
        </div>
        {events.length > 0 && <span className="rounded-full bg-white/5 px-2 py-1 text-xs text-slate-400">{events.length} events</span>}
      </div>
      <div ref={listRef} className="max-h-80 space-y-3 overflow-auto pr-1">
        {events.length === 0 && <p className="text-sm text-slate-500">SSE 事件会按到达顺序流式追加。</p>}
        {events.map((event, index) => (
          <div key={`${event.type}-${event.timestamp}-${index}`} className="animate-[traceIn_260ms_ease-out] border-l border-white/10 pl-3">
            <div className="flex items-center justify-between gap-3">
              <p className="text-xs uppercase tracking-wider text-cyan-200">{labels[event.type] ?? event.type}</p>
              <span className="text-[10px] text-slate-500">#{index + 1}</span>
            </div>
            <p className="text-sm text-slate-300">{event.message}</p>
          </div>
        ))}
      </div>
    </section>
  )
}
