import { Brain, CheckCircle2, CircleDotDashed, Loader2, Route } from 'lucide-react'
import clsx from 'clsx'
import { useAssistantStore } from '../stores/assistantStore'

const icons = [Brain, Route, CircleDotDashed]

export function AgentWorkflowBoard() {
  const agents = useAssistantStore((state) => state.agents)
  return (
    <section className="glass rounded-[2rem] p-5">
      <div className="mb-4 flex items-center justify-between">
        <h3 className="font-semibold text-white">Agent Workflow</h3>
        <span className="rounded-full bg-emerald-300/10 px-3 py-1 text-xs text-emerald-200">Orchestrated</span>
      </div>
      <div className="space-y-3">
        {agents.map((agent, index) => {
          const Icon = icons[index] ?? Brain
          return (
            <div key={agent.name} className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
              <div className="flex items-center gap-3">
                <div className={clsx('flex h-10 w-10 items-center justify-center rounded-2xl', agent.status === 'done' ? 'bg-emerald-300/15 text-emerald-200' : 'bg-cyan-300/10 text-cyan-200')}>
                  {agent.status === 'running' ? <Loader2 className="animate-spin" size={18} /> : agent.status === 'done' ? <CheckCircle2 size={18} /> : <Icon size={18} />}
                </div>
                <div>
                  <p className="font-medium text-slate-100">{agent.name}</p>
                  <p className="text-xs text-slate-400">{agent.detail}</p>
                </div>
              </div>
            </div>
          )
        })}
      </div>
    </section>
  )
}
