import { CheckCircle2, ShieldAlert, XCircle } from 'lucide-react'
import { useState } from 'react'
import { cancelOrderConfirmation, confirmOrderConfirmation } from '../api/client'
import { useAssistantStore } from '../stores/assistantStore'

export function OrderConfirmationCard() {
  const pending = useAssistantStore((state) => state.pendingOrderConfirmation)
  const result = useAssistantStore((state) => state.orderCreationResult)
  const error = useAssistantStore((state) => state.orderConfirmationError)
  const setResult = useAssistantStore((state) => state.setOrderCreationResult)
  const setError = useAssistantStore((state) => state.setOrderConfirmationError)
  const clear = useAssistantStore((state) => state.clearOrderConfirmation)
  const [busy, setBusy] = useState(false)

  if (!pending && !result) return null

  const onConfirm = async () => {
    if (!pending) return
    setBusy(true)
    setError(undefined)
    try {
      const response = await confirmOrderConfirmation(pending.confirmationId, pending.customerId, pending.sessionId)
      setResult(response)
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '确认下单失败')
    } finally {
      setBusy(false)
    }
  }

  const onCancel = async () => {
    if (!pending) return
    setBusy(true)
    setError(undefined)
    try {
      await cancelOrderConfirmation(pending.confirmationId, pending.customerId, pending.sessionId)
      clear()
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : '取消下单失败')
    } finally {
      setBusy(false)
    }
  }

  if (result) {
    return (
      <section className="glass rounded-[2rem] border border-emerald-300/20 p-5">
        <div className="mb-4 flex items-center gap-2 text-emerald-100">
          <CheckCircle2 size={18} />
          <h3 className="font-semibold">订单已创建</h3>
        </div>
        <div className="space-y-2 text-sm text-slate-300">
          <p>订单号：{result.orderNo}</p>
          <p>订单状态：{result.orderStatus}</p>
          <p>支付状态：{result.paymentStatus}</p>
          <p>金额：¥{Number(result.amount).toFixed(2)}</p>
          <p className="rounded-2xl bg-emerald-300/10 p-3 text-emerald-100">{result.message}</p>
        </div>
      </section>
    )
  }

  return (
    <section className="glass rounded-[2rem] border border-amber-300/20 p-5">
      <div className="mb-4 flex items-center gap-2 text-amber-100">
        <ShieldAlert size={18} />
        <h3 className="font-semibold">高风险操作确认</h3>
      </div>
      <div className="space-y-3 text-sm text-slate-300">
        <p className="rounded-2xl bg-amber-300/10 p-3 text-amber-100">{pending?.message}</p>
        <div className="grid gap-2 rounded-2xl bg-white/5 p-3">
          <span>商品：{pending?.productName}</span>
          <span>商品 ID：{pending?.productId}</span>
          <span>数量：{pending?.quantity}</span>
          <span>单价：¥{Number(pending?.unitPrice ?? 0).toFixed(2)}</span>
          <span>总价：¥{Number(pending?.totalAmount ?? 0).toFixed(2)}</span>
          <span>收货：{pending?.address.display}</span>
          <span>支付方式：{pending?.paymentMethod.display}</span>
        </div>
        <p className="text-xs text-slate-500">确认只会创建未支付订单，不会自动扣款。</p>
        {error && <p className="rounded-2xl bg-rose-400/10 p-3 text-rose-100">{error}</p>}
        <div className="flex gap-3">
          <button
            onClick={onConfirm}
            disabled={busy}
            className="flex-1 rounded-2xl bg-emerald-300 px-4 py-2 font-semibold text-slate-950 disabled:opacity-50"
          >
            确认下单
          </button>
          <button
            onClick={onCancel}
            disabled={busy}
            className="flex flex-1 items-center justify-center gap-2 rounded-2xl bg-white/10 px-4 py-2 text-slate-100 disabled:opacity-50"
          >
            <XCircle size={16} />取消
          </button>
        </div>
      </div>
    </section>
  )
}
