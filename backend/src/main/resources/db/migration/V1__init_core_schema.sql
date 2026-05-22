CREATE TABLE tickets (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(32) NOT NULL,
    priority VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    assigned_agent_id VARCHAR(64),
    resolution TEXT,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_ticket_customer (customer_id),
    INDEX idx_ticket_status_priority (status, priority),
    INDEX idx_ticket_updated (updated_at)
);

CREATE TABLE ticket_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id VARCHAR(64) NOT NULL,
    author VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_ticket_messages_ticket (ticket_id),
    CONSTRAINT fk_ticket_messages_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);

CREATE TABLE ticket_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    payload_json TEXT,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_ticket_events_ticket (ticket_id)
);

CREATE TABLE orders (
    id VARCHAR(64) PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    customer_id VARCHAR(64) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    order_status VARCHAR(32) NOT NULL,
    payment_status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_orders_customer_created (customer_id, created_at),
    INDEX idx_orders_status (order_status, payment_status)
);

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id VARCHAR(64) NOT NULL,
    sku VARCHAR(128) NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_order_items_order (order_id)
);

CREATE TABLE support_agents (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    role VARCHAR(64) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE agent_capabilities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    agent_id VARCHAR(64) NOT NULL,
    capability VARCHAR(64) NOT NULL,
    INDEX idx_agent_cap_agent (agent_id),
    UNIQUE KEY uk_agent_capability (agent_id, capability)
);

CREATE TABLE assistant_sessions (
    id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    summary TEXT,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_assistant_sessions_customer (customer_id)
);

CREATE TABLE assistant_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_assistant_messages_session (session_id)
);

CREATE TABLE workflow_traces (
    id VARCHAR(128) PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL,
    customer_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    started_at TIMESTAMP(6) NOT NULL,
    completed_at TIMESTAMP(6),
    INDEX idx_workflow_trace_session (session_id)
);

CREATE TABLE workflow_trace_steps (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trace_id VARCHAR(128) NOT NULL,
    step_name VARCHAR(128) NOT NULL,
    step_kind VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    input_json TEXT,
    output_json TEXT,
    started_at TIMESTAMP(6) NOT NULL,
    completed_at TIMESTAMP(6),
    duration_millis BIGINT,
    INDEX idx_trace_steps_trace (trace_id)
);

CREATE TABLE tool_invocations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trace_id VARCHAR(128) NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    tool_name VARCHAR(128) NOT NULL,
    arguments_json TEXT,
    success BOOLEAN NOT NULL,
    result_json TEXT,
    duration_millis BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_tool_invocations_trace (trace_id),
    INDEX idx_tool_invocations_tool (tool_name)
);

CREATE TABLE knowledge_documents (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(64) NOT NULL,
    source VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_knowledge_category (category)
);

CREATE TABLE knowledge_chunks (
    id VARCHAR(64) PRIMARY KEY,
    document_id VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(64) NOT NULL,
    source VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    INDEX idx_knowledge_chunks_doc (document_id),
    FULLTEXT INDEX ft_knowledge_chunks_content (title, content)
);
