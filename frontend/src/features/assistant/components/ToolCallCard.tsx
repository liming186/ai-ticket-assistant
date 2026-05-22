import { Wrench } from 'lucide-react'
import { useAssistantStore } from '../stores/assistantStore'

export function ToolCallCard() {
  const tools = useAssistantStore((state) => state.tools)
  return (
    <section className="glass rounded-[2rem] p-5">
      <div className="mb-4 flex items-center gap-2">
        <Wrench size={18} className="text-violet-200" />
        <h3 className="font-semibold text-white">Tool Calling</h3>
      </div>
      <div className="space-y-3">
        {tools.length === 0 && <p className="text-sm text-slate-500">等待 AI 返回 tool_calls。</p>}
        {tools.map((tool, index) => (
          <div key={`${tool.name}-${index}`} className="rounded-2xl border border-violet-200/10 bg-violet-300/5 p-3">
            <div className="mb-2 flex items-center justify-between">
              <span className="text-sm font-medium text-violet-100">{tool.name}</span>
              <span className="rounded-full bg-white/5 px-2 py-1 text-[10px] uppercase text-slate-300">{tool.status}</span>
            </div>
            <pre className="max-h-32 overflow-auto text-xs text-slate-400">{JSON.stringify(tool.payload, null, 2)}</pre>
          </div>
        ))}
      </div>
    </section>
  )
}
