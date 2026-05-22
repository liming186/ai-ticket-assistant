import { Sparkles } from 'lucide-react'
import { useAssistantStore } from '../stores/assistantStore'

type InlineSegment = {
  text: string
  strong: boolean
}

function inlineMarkdown(text: string): InlineSegment[] {
  return text.split(/(\*\*[^*]+\*\*)/g).filter(Boolean).map((part) => {
    const strong = part.startsWith('**') && part.endsWith('**')
    return { text: strong ? part.slice(2, -2) : part, strong }
  })
}

function MarkdownLine({ line }: { line: string }) {
  const trimmed = line.trim()
  const listMatch = /^[-✅]\s*(.*)$/.exec(trimmed)
  const headingMatch = /^\*\*(.+?)\*\*：?\s*(.*)$/.exec(trimmed)
  const text = listMatch?.[1] ?? headingMatch?.[2] ?? line
  const segments = inlineMarkdown(text)

  if (headingMatch) {
    return (
      <div className="mt-4 first:mt-0">
        <p className="font-semibold text-cyan-100">{headingMatch[1]}</p>
        {headingMatch[2] && <p className="mt-1 text-slate-100">{segments.map((segment, index) => segment.strong ? <strong key={index}>{segment.text}</strong> : <span key={index}>{segment.text}</span>)}</p>}
      </div>
    )
  }

  if (listMatch) {
    return (
      <li className="ml-5 list-disc text-slate-200">
        {segments.map((segment, index) => segment.strong ? <strong key={index}>{segment.text}</strong> : <span key={index}>{segment.text}</span>)}
      </li>
    )
  }

  return (
    <p className="text-slate-100">
      {segments.map((segment, index) => segment.strong ? <strong key={index}>{segment.text}</strong> : <span key={index}>{segment.text}</span>)}
    </p>
  )
}

function MarkdownAnswer({ text }: { text: string }) {
  return (
    <div className="space-y-2 leading-8">
      {text.split('\n').map((line, index) => line.trim() ? <MarkdownLine key={index} line={line} /> : <div key={index} className="h-2" />)}
    </div>
  )
}

export function StreamingAnswer() {
  const { answer, streaming } = useAssistantStore()
  return (
    <section className="glass min-h-[420px] rounded-[2rem] p-6">
      <div className="mb-5 flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-cyan-300/15 text-cyan-200">
          <Sparkles size={20} />
        </div>
        <div>
          <h2 className="text-lg font-semibold text-white">AI Ticket Assistant</h2>
          <p className="text-sm text-slate-400">多 Agent 协同响应</p>
        </div>
      </div>
      <div className={streaming ? 'typing-caret' : ''}>
        {answer ? <MarkdownAnswer text={answer} /> : <p className="leading-8 text-slate-400">你好，我是客服助手，等待你的问题。系统会展示 Intent Agent、Order Agent、Knowledge Agent、Tool Calling 与 RAG 检索过程。</p>}
      </div>
    </section>
  )
}
