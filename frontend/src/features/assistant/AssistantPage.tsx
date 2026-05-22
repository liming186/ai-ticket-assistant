import { Activity, Bot, ShieldCheck } from 'lucide-react'
import { AgentWorkflowBoard } from './components/AgentWorkflowBoard'
import { Composer } from './components/Composer'
import { OrderConfirmationCard } from './components/OrderConfirmationCard'
import { ProductCatalogPanel } from './components/ProductCatalogPanel'
import { RagSourcesPanel } from './components/RagSourcesPanel'
import { StreamingAnswer } from './components/StreamingAnswer'
import { TicketStatusPanel } from './components/TicketStatusPanel'
import { ToolCallCard } from './components/ToolCallCard'
import { TraceTimeline } from './components/TraceTimeline'
import { useAssistantStore } from './stores/assistantStore'

export function AssistantPage() {
  const { connected, streaming } = useAssistantStore()
  return (
    <main className="relative min-h-screen px-4 py-6 md:px-8">
      <div className="orb pointer-events-none fixed left-10 top-10 h-72 w-72 rounded-full bg-cyan-400/10 blur-3xl" />
      <div className="orb pointer-events-none fixed bottom-10 right-10 h-80 w-80 rounded-full bg-violet-500/10 blur-3xl" />

      <header className="mx-auto mb-6 flex max-w-7xl items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="flex h-13 w-13 items-center justify-center rounded-3xl bg-gradient-to-br from-cyan-300 via-blue-300 to-violet-400 p-3 text-slate-950 shadow-glow">
            <Bot size={28} />
          </div>
          <div>
            <p className="text-sm text-cyan-200">Enterprise AI Native</p>
            <h1 className="text-2xl font-semibold text-white md:text-4xl">AI Ticket Assistant</h1>
          </div>
        </div>
        <div className="hidden items-center gap-3 rounded-full border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-300 md:flex">
          <Activity size={16} className={streaming ? 'animate-pulse text-emerald-200' : 'text-slate-500'} />
          {connected ? 'SSE Connected' : 'Ready'}
        </div>
      </header>

      <section className="mx-auto grid max-w-7xl gap-5 xl:grid-cols-[360px_1fr_360px]">
        <aside className="space-y-5">
          <div className="glass rounded-[2rem] p-5">
            <div className="mb-3 flex items-center gap-2 text-sm text-emerald-200">
              <ShieldCheck size={16} /> DDD + Tool Guard
            </div>
            <p className="text-sm leading-6 text-slate-400">AI智能客服</p>
          </div>
          <Composer />
          <ProductCatalogPanel />
          <AgentWorkflowBoard />
        </aside>

        <section className="space-y-5">
          <StreamingAnswer />
          <TraceTimeline />
        </section>

        <aside className="space-y-5">
          <OrderConfirmationCard />
          <ToolCallCard />
          <RagSourcesPanel />
          <TicketStatusPanel />
        </aside>
      </section>
    </main>
  )
}
