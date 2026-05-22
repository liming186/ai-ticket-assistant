import { BookOpenText } from 'lucide-react'
import { useAssistantStore } from '../stores/assistantStore'

export function RagSourcesPanel() {
  const sources = useAssistantStore((state) => state.sources)
  return (
    <section className="glass rounded-[2rem] p-5">
      <div className="mb-4 flex items-center gap-2">
        <BookOpenText size={18} className="text-cyan-200" />
        <h3 className="font-semibold text-white">RAG Sources</h3>
      </div>
      <div className="space-y-3">
        {sources.length === 0 && <p className="text-sm text-slate-500">检索来源会在这里展示。</p>}
        {sources.length > 0 && <p className="text-xs text-slate-500">本次检索命中了 {sources.length} 条知识片段，不是重复检索。</p>}
        {sources.slice(0, 4).map((source, index) => (
          <div key={source.id ?? index} className="rounded-2xl border border-cyan-200/10 bg-cyan-300/5 p-3">
            <div className="flex items-start justify-between gap-3">
              <p className="text-sm font-medium text-cyan-100">{source.title ?? '未命名知识片段'}</p>
              {typeof source.score === 'number' && <span className="shrink-0 rounded-full bg-cyan-300/10 px-2 py-0.5 text-[10px] text-cyan-100">{source.score.toFixed(2)}</span>}
            </div>
            {source.source && <p className="mt-1 text-xs text-slate-400">{source.source}</p>}
            <p className="mt-2 line-clamp-3 text-sm text-slate-300">{source.content}</p>
          </div>
        ))}
      </div>
    </section>
  )
}
