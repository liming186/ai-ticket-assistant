import { ArrowUp, Square } from 'lucide-react'
import { useAssistantStream } from '../hooks/useAssistantStream'
import { useAssistantStore } from '../stores/assistantStore'

export function Composer() {
  const { run, cancel } = useAssistantStream()
  const { userMessage, setUserMessage, streaming } = useAssistantStore()
  return (
    <div className="glass rounded-3xl p-4">
      <textarea
        value={userMessage}
        onChange={(event) => setUserMessage(event.target.value)}
        className="h-24 w-full resize-none bg-transparent text-base text-slate-100 outline-none placeholder:text-slate-500"
        placeholder="输入工单问题，例如：购买 CLOTH-JEANS-003 下单"
      />
      <div className="flex items-center justify-between border-t border-white/10 pt-3">
        <div className="flex gap-2 text-xs text-slate-400">
          <span className="rounded-full bg-white/5 px-3 py-1">SSE 流式</span>
          <span className="rounded-full bg-white/5 px-3 py-1">Tool Calling</span>
          <span className="rounded-full bg-white/5 px-3 py-1">RAG</span>
        </div>
        <button
          onClick={streaming ? cancel : run}
          className="flex h-11 w-11 items-center justify-center rounded-2xl bg-gradient-to-br from-cyan-300 to-violet-400 text-slate-950 shadow-glow transition hover:scale-105"
        >
          {streaming ? <Square size={18} /> : <ArrowUp size={20} />}
        </button>
      </div>
    </div>
  )
}
