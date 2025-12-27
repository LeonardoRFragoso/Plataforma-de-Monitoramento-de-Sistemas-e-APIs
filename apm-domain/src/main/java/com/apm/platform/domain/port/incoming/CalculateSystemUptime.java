package com.apm.platform.domain.port.incoming;

import com.apm.platform.domain.valueobject.UptimePercentage;

import java.time.Instant;

public interface CalculateSystemUptime {
    UptimePercentage execute(String systemId, Instant startTime, Instant endTime);
}
