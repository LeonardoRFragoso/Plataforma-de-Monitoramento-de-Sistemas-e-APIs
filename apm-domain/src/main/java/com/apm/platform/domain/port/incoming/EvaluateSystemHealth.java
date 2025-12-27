package com.apm.platform.domain.port.incoming;

import com.apm.platform.domain.valueobject.SystemStatus;

public interface EvaluateSystemHealth {
    SystemStatus execute(String systemId);
}
