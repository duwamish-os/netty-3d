package com.netty.microservice.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;

//@AllArgsConstructor
//@Data
public class HeartbeatResponse {
    private String applicationName;
    private String applicationVersion;

    public HeartbeatResponse(String applicationName, String applicationVersion) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }
}
