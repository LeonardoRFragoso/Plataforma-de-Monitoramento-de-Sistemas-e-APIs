CREATE TABLE monitored_systems (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    base_url VARCHAR(500) NOT NULL,
    type VARCHAR(20) NOT NULL,
    environment VARCHAR(20) NOT NULL,
    collection_interval_seconds INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    current_status VARCHAR(20) NOT NULL,
    last_check_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_system_name ON monitored_systems(name);
CREATE INDEX idx_system_active ON monitored_systems(active);
CREATE INDEX idx_system_environment ON monitored_systems(environment);
