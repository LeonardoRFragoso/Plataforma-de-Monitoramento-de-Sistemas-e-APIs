package com.apm.platform.domain.port.outgoing;

import com.apm.platform.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
