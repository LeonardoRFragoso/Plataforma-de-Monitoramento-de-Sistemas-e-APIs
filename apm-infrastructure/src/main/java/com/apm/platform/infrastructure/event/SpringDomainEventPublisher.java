package com.apm.platform.infrastructure.event;

import com.apm.platform.domain.event.DomainEvent;
import com.apm.platform.domain.port.outgoing.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(SpringDomainEventPublisher.class);

    private final ApplicationEventPublisher springEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher springEventPublisher) {
        this.springEventPublisher = springEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        logger.debug("Publishing domain event: {} with ID: {}", 
            event.getClass().getSimpleName(), event.getEventId());
        
        springEventPublisher.publishEvent(event);
    }
}
