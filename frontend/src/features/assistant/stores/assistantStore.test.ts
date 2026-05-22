import { describe, expect, it } from 'vitest'
import { useAssistantStore } from './assistantStore'

describe('assistantStore', () => {
  it('appends final deltas', () => {
    useAssistantStore.getState().reset()
    useAssistantStore.getState().ingest({ type: 'final', message: 'hi', payload: { delta: 'hi', done: false }, timestamp: new Date().toISOString() })
    expect(useAssistantStore.getState().answer).toBe('hi')
  })
})
