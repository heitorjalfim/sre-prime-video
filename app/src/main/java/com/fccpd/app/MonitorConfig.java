package com.fccpd.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConfigurationProperties(prefix = "app")
public class MonitorConfig {

    private String role = "monolith";
    private String storageUrl = "http://storage:8080";
    private String detectorAUrl = "http://detector-a:8080";
    private String detectorBUrl = "http://detector-b:8080";

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public String getDetectorAUrl() {
        return detectorAUrl;
    }

    public void setDetectorAUrl(String detectorAUrl) {
        this.detectorAUrl = detectorAUrl;
    }

    public String getDetectorBUrl() {
        return detectorBUrl;
    }

    public void setDetectorBUrl(String detectorBUrl) {
        this.detectorBUrl = detectorBUrl;
    }
}