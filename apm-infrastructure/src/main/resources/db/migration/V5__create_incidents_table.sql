CREATE TABLE incidents (
    id VARCHAR(36) PRIMARY KEY,
    system_id VARCHAR(36) NOT NULL,
    detected_status VARCHAR(20) NOT NULL,
    description VARCHAR(500) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at TIMESTAMP,
    downtime_seconds BIGINT,
    root_cause VARCHAR(1000),
    FOREIGN KEY (system_id) REFERENCES monitored_systems(id) ON DELETE CASCADE
);

CREATE INDEX idx_incident_system_id ON incidents(system_id);
CREATE INDEX idx_incident_resolved ON incidents(resolved);
CREATE INDEX idx_incident_started_at ON incidents(started_at);
