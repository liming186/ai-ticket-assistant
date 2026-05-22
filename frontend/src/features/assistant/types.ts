export type AssistantEvent = {
  type: string
  message: string
  payload: Record<string, unknown>
  timestamp: string
}

export type AgentCard = {
  name: string
  status: 'idle' | 'running' | 'done' | 'error'
  detail: string
}

export type ToolCard = {
  name: string
  status: 'requested' | 'success' | 'error'
  payload: unknown
}

export type OrderConfirmationPayload = {
  confirmationId: string
  sessionId: string
  customerId: string
  message: string
  productId: string
  productName: string
  quantity: number
  unitPrice: number
  totalAmount: number
  address: { id: string; display: string }
  paymentMethod: { id: string; display: string; methodType?: string }
  expiresAt: string
}

export type OrderCreationResult = {
  orderNo: string
  orderStatus: string
  paymentStatus: string
  amount: number
  product?: Record<string, unknown>
  message: string
}

export type SourceCard = {
  id?: string
  title?: string
  source?: string
  content?: string
  score?: number
  matchReason?: string
}
