import { TicketCheck } from 'lucide-react'
import { useAssistantStore } from '../stores/assistantStore'

export function TicketStatusPanel() {
  const ticket = useAssistantStore((state) => state.ticket) as Record<string, unknown> | undefined
  return (
    <section className="glass rounded-[2rem] p-5">
      <div className="mb-4 flex items-center gap-2">
        <TicketCheck size={18} className="text-emerald-200" />
        <h3 className="font-semibold text-white">Ticket Status</h3>
      </div>
      {ticket ? (
        <div className="space-y-3 text-sm">
          <div className="rounded-2xl bg-emerald-300/10 p-4 text-emerald-100">{String(ticket.ticketId ?? 'New Ticket')}</div>
          <div className="grid grid-cols-2 gap-3 text-slate-300">
            <span>状态：{String(ticket.status ?? '-')}</span>
            <span>优先级：{String(ticket.priority ?? '-')}</span>
            <span className="col-span-2">标题：{String(ticket.title ?? '-')}</span>
          </div>
        </div>
      ) : (
        <p className="text-sm text-slate-500">工单创建或更新后会实时显示。</p>
      )}
    </section>
  )
}
