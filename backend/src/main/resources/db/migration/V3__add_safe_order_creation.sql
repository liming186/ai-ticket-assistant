CREATE TABLE products (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE product_inventory (
    product_id VARCHAR(64) PRIMARY KEY,
    stock_on_hand INT NOT NULL,
    reserved_stock INT NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_product_inventory_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE customer_addresses (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    recipient_name VARCHAR(128) NOT NULL,
    phone VARCHAR(64) NOT NULL,
    address_line VARCHAR(512) NOT NULL,
    is_default BOOLEAN NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_customer_addresses_customer (customer_id)
);

CREATE TABLE customer_payment_methods (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    method_type VARCHAR(64) NOT NULL,
    display_label VARCHAR(255) NOT NULL,
    is_default BOOLEAN NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_customer_payment_methods_customer (customer_id)
);

CREATE TABLE order_confirmations (
    id VARCHAR(64) PRIMARY KEY,
    trace_id VARCHAR(128) NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    customer_id VARCHAR(64) NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    total_amount DECIMAL(18,2) NOT NULL,
    address_id VARCHAR(64) NOT NULL,
    payment_method_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_order_confirmations_session (session_id),
    INDEX idx_order_confirmations_customer (customer_id),
    CONSTRAINT fk_order_confirmations_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_order_confirmations_address FOREIGN KEY (address_id) REFERENCES customer_addresses(id),
    CONSTRAINT fk_order_confirmations_payment FOREIGN KEY (payment_method_id) REFERENCES customer_payment_methods(id)
);

ALTER TABLE orders
    ADD COLUMN address_id VARCHAR(64),
    ADD COLUMN payment_method_id VARCHAR(64),
    ADD COLUMN source VARCHAR(64),
    ADD COLUMN confirmation_id VARCHAR(64);

ALTER TABLE order_items
    ADD COLUMN product_id VARCHAR(64);

INSERT INTO products (id, name, price, active, created_at, updated_at)
VALUES
  ('CLOTH-TEE-001', '基础纯棉白色T恤', 99.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-SHIRT-002', '蓝色牛津纺衬衫', 199.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-JEANS-003', '直筒水洗牛仔裤', 299.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-HOODIE-004', '黑色连帽卫衣', 259.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-DRESS-005', '碎花雪纺连衣裙', 329.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-JACKET-006', '轻薄防风夹克', 399.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-SKIRT-007', '高腰A字半身裙', 189.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-POLO-008', '商务休闲Polo衫', 169.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-COAT-009', '羊毛混纺大衣', 699.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('CLOTH-SWEATER-010', '米色针织毛衣', 229.00, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO product_inventory (product_id, stock_on_hand, reserved_stock, updated_at)
VALUES
  ('CLOTH-TEE-001', 80, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-SHIRT-002', 45, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-JEANS-003', 30, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-HOODIE-004', 36, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-DRESS-005', 24, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-JACKET-006', 18, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-SKIRT-007', 40, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-POLO-008', 52, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-COAT-009', 12, 0, CURRENT_TIMESTAMP(6)),
  ('CLOTH-SWEATER-010', 28, 0, CURRENT_TIMESTAMP(6));

INSERT INTO customer_addresses (id, customer_id, recipient_name, phone, address_line, is_default, active, created_at, updated_at)
VALUES
  ('ADDR-CUST-1001-DEFAULT', 'CUST-1001', '王同学', '138****1001', '上海市浦东新区世纪大道 100 号 18 楼', true, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('ADDR-CUST-2001-DEFAULT', 'CUST-2001', '李同学', '138****2001', '北京市朝阳区建国路 88 号 6 楼', true, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO customer_payment_methods (id, customer_id, method_type, display_label, is_default, active, created_at, updated_at)
VALUES
  ('PAY-CUST-1001-DEFAULT', 'CUST-1001', 'CARD', '招商银行信用卡 **** 1001（仅确认，不自动扣款）', true, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
  ('PAY-CUST-2001-DEFAULT', 'CUST-2001', 'ALIPAY', '支付宝账户 138****2001（仅确认，不自动扣款）', true, true, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
