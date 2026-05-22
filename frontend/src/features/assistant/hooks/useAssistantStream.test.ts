import { describe, expect, it } from 'vitest'
import { assistantStreamUrl } from '../api/client'

describe('assistantStreamUrl', () => {
  it('builds encoded stream url', () => {
    expect(assistantStreamUrl('支付成功但订单没生成怎么办')).toContain('%E6%94%AF%E4%BB%98')
  })
})
