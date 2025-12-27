package com.apm.platform.domain.event;

import java.time.Instant;

public interface DomainEvent {
    String getEventId();
    Instant getOccurredAt();
    String getAggregateId();
}
