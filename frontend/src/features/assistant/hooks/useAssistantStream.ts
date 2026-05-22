import { useRef } from 'react'
import { assistantStreamUrl } from '../api/client'
import { useAssistantStore } from '../stores/assistantStore'
import type { AssistantEvent } from '../types'

export function useAssistantStream() {
  const sourceRef = useRef<EventSource | null>(null)
  const { userMessage, start, stop, ingest } = useAssistantStore()

  const run = () => {
    sourceRef.current?.close()
    start()
    const source = new EventSource(assistantStreamUrl(userMessage))
    sourceRef.current = source

    const handle = (event: MessageEvent<string>) => {
      if (!event.data) return
      try {
        ingest(JSON.parse(event.data) as AssistantEvent)
      } catch (error) {
        console.error('Invalid SSE payload', error)
      }
    }

    const names = ['workflow_started', 'agent_started', 'agent_completed', 'tool_call', 'tool_result', 'knowledge_sources', 'ticket_created', 'ticket_updated', 'order_confirmation_required', 'order_created', 'trace', 'final', 'error']
    names.forEach((name) => source.addEventListener(name, handle as EventListener))
    source.onerror = () => {
      stop()
      source.close()
    }
  }

  const cancel = () => {
    sourceRef.current?.close()
    stop()
  }

  return { run, cancel }
}
