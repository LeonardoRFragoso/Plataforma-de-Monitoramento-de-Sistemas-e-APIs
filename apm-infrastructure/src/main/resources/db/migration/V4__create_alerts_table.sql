CREATE TABLE alerts (
    id VARCHAR(36) PRIMARY KEY,
    system_id VARCHAR(36) NOT NULL,
    rule_id VARCHAR(36),
    severity VARCHAR(20) NOT NULL,
    message VARCHAR(500) NOT NULL,
    triggered_at TIMESTAMP NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at TIMESTAMP,
    resolution_notes VARCHAR(500),
    FOREIGN KEY (system_id) REFERENCES monitored_systems(id) ON DELETE CASCADE,
    FOREIGN KEY (rule_id) REFERENCES alert_rules(id) ON DELETE SET NULL
);

CREATE INDEX idx_alert_system_id ON alerts(system_id);
CREATE INDEX idx_alert_resolved ON alerts(resolved);
CREATE INDEX idx_alert_severity ON alerts(severity);
CREATE INDEX idx_alert_triggered_at ON alerts(triggered_at);
