CREATE TABLE metrics (
    id VARCHAR(36) PRIMARY KEY,
    system_id VARCHAR(36) NOT NULL,
    latency_ms BIGINT NOT NULL,
    status_code INTEGER NOT NULL,
    has_error BOOLEAN NOT NULL,
    cpu_usage_percent DOUBLE PRECISION NOT NULL,
    memory_usage_percent DOUBLE PRECISION NOT NULL,
    collected_at TIMESTAMP NOT NULL,
    FOREIGN KEY (system_id) REFERENCES monitored_systems(id) ON DELETE CASCADE
);

CREATE INDEX idx_metric_system_id ON metrics(system_id);
CREATE INDEX idx_metric_collected_at ON metrics(collected_at);
CREATE INDEX idx_metric_system_time ON metrics(system_id, collected_at);

CREATE TABLE metric_additional_data (
    metric_id VARCHAR(36) NOT NULL,
    data_key VARCHAR(255) NOT NULL,
    data_value TEXT,
    PRIMARY KEY (metric_id, data_key),
    FOREIGN KEY (metric_id) REFERENCES metrics(id) ON DELETE CASCADE
);
