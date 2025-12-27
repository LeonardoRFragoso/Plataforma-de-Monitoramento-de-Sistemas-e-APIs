CREATE TABLE alert_rules (
    id VARCHAR(36) PRIMARY KEY,
    system_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    threshold_value DOUBLE PRECISION NOT NULL,
    consecutive_violations INTEGER NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (system_id) REFERENCES monitored_systems(id) ON DELETE CASCADE
);

CREATE INDEX idx_alert_rule_system_id ON alert_rules(system_id);
CREATE INDEX idx_alert_rule_enabled ON alert_rules(enabled);
