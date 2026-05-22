export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

export function assistantStreamUrl(message: string, customerId = 'CUST-1001', orderNo = '') {
  const params = new URLSearchParams({ message, customerId })
  if (orderNo) params.set('orderNo', orderNo)
  return `${API_BASE_URL}/assistant/stream?${params.toString()}`
}

export async function confirmOrderConfirmation(confirmationId: string, customerId: string, sessionId: string) {
  const response = await fetch(`${API_BASE_URL}/assistant/order-confirmations/${confirmationId}/confirm`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ customerId, sessionId })
  })
  if (!response.ok) throw new Error((await response.json()).message || '确认下单失败')
  return response.json()
}

export async function cancelOrderConfirmation(confirmationId: string, customerId: string, sessionId: string) {
  const response = await fetch(`${API_BASE_URL}/assistant/order-confirmations/${confirmationId}/cancel`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ customerId, sessionId })
  })
  if (!response.ok) throw new Error((await response.json()).message || '取消下单失败')
  return response.json()
}
