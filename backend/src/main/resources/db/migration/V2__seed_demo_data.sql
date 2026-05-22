INSERT INTO orders (id, order_no, customer_id, amount, order_status, payment_status, created_at, updated_at)
VALUES
  ('ORD-ID-1001', 'ORD-1001', 'CUST-1001', 299.00, 'FAILED', 'PAID', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('ORD-ID-1002', 'ORD-1002', 'CUST-1001', 89.00, 'SHIPPED', 'PAID', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('ORD-ID-2001', 'ORD-2001', 'CUST-2001', 1299.00, 'FAILED', 'FAILED', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO support_agents (id, name, role, active, created_at, updated_at)
VALUES
  ('AGT-PAY-01', 'Payment Human Support', 'HUMAN_SUPPORT', true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('AGT-ORD-01', 'Order Human Support', 'HUMAN_SUPPORT', true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO agent_capabilities (agent_id, capability)
VALUES
  ('AGT-PAY-01', 'PAYMENT'),
  ('AGT-PAY-01', 'REFUND'),
  ('AGT-ORD-01', 'ORDER'),
  ('AGT-ORD-01', 'LOGISTICS');

INSERT INTO knowledge_documents (id, title, category, source, content, updated_at)
VALUES
  ('DOC-PAY-001', '支付成功但订单未生成处理规范', 'PAYMENT', 'internal://kb/payment-order-missing', '当用户支付成功但订单未生成时，系统应先查询订单与支付流水。如果订单状态为 FAILED 且支付状态为 PAID，需要创建高优先级支付工单，保留支付凭证，并由支付支持团队补偿处理。', CURRENT_TIMESTAMP(6)),
  ('DOC-PAY-002', '支付失败工单创建 FAQ', 'PAYMENT', 'internal://faq/payment-failed-ticket', '支付失败场景应收集用户 ID、订单号、支付渠道、失败截图。高价值订单或重复失败应标记 HIGH 优先级。', CURRENT_TIMESTAMP(6)),
  ('DOC-ORDER-001', '订单状态说明', 'ORDER', 'internal://kb/order-status', '订单状态包括 CREATED、PAID、FULFILLING、SHIPPED、DELIVERED、FAILED、CANCELLED。FAILED 表示需要客服介入核验。', CURRENT_TIMESTAMP(6));

INSERT INTO knowledge_chunks (id, document_id, title, category, source, content)
VALUES
  ('CHK-PAY-001-1', 'DOC-PAY-001', '支付成功但订单未生成处理规范', 'PAYMENT', 'internal://kb/payment-order-missing#1', '支付成功但订单没生成：先查询订单与支付流水；如果支付状态 PAID 且订单状态 FAILED，创建高优先级支付工单并进入补偿流程。'),
  ('CHK-PAY-002-1', 'DOC-PAY-002', '支付失败工单创建 FAQ', 'PAYMENT', 'internal://faq/payment-failed-ticket#1', '创建支付失败工单需要标题、客户 ID、订单号、支付渠道和失败截图；优先级通常为 HIGH。'),
  ('CHK-ORDER-001-1', 'DOC-ORDER-001', '订单状态说明', 'ORDER', 'internal://kb/order-status#1', '订单 FAILED 表示业务处理失败；支付 PAID + 订单 FAILED 是典型补单或退款核验场景。');
