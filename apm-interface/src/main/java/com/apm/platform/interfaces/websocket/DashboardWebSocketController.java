package com.apm.platform.interfaces.websocket;

import com.apm.platform.application.dto.response.AlertResponse;
import com.apm.platform.application.dto.response.MetricResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DashboardWebSocketController {

    @MessageMapping("/dashboard/subscribe")
    @SendTo("/topic/dashboard")
    public String subscribe() {
        return "Connected to APM Dashboard real-time updates";
    }
}
