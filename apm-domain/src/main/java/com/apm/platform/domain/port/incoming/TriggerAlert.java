package com.apm.platform.domain.port.incoming;

import com.apm.platform.domain.entity.Alert;

public interface TriggerAlert {
    Alert execute(String systemId, String ruleId, String message);
}
