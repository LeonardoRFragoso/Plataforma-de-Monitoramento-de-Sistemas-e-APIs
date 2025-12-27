package com.apm.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.apm.platform.infrastructure",
    "com.apm.platform.interfaces"
})
public class ApmPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApmPlatformApplication.class, args);
    }
}
