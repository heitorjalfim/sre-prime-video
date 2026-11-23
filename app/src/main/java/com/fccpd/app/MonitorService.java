package com.fccpd.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MonitorService {

    private static final Logger log = LoggerFactory.getLogger(MonitorService.class);

    private final MonitorConfig config;
    private final RestClient restClient;
    private final Map<String, String> localStorage = new ConcurrentHashMap<>();

    public MonitorService(MonitorConfig config, RestClient restClient) {
        this.config = config;
        this.restClient = restClient;
    }

    public void putStorage(String key, String value) {
        localStorage.put(key, value);
    }

    public String getStorage(String key) {
        String value = localStorage.get(key);
        if (value == null) {
            throw new IllegalArgumentException("frame nao encontrado no storage: " + key);
        }
        return value;
    }

    public String runDetector(String detectorName, String frameId) {
        String payload;
        if ("monolith".equalsIgnoreCase(config.getRole())) {
            payload = localStorage.get(frameId);
        } else {
            payload = restClient.get()
                    .uri(config.getStorageUrl() + "/api/storage/" + frameId)
                    .retrieve()
                    .body(String.class);
        }
        return heavyCpuCheck(detectorName, payload);
    }

    public ProcessResponse processMonolith(FrameRequest request) {
        long start = System.currentTimeMillis();
        String payload = request.payload() != null ? request.payload() : "";

        String resA = heavyCpuCheck("detector-a", payload);
        String resB = heavyCpuCheck("detector-b", payload);

        long elapsed = System.currentTimeMillis() - start;
        String summary = "ok:" + resA.substring(0, 8) + ":" + resB.substring(0, 8);

        log.info("[METRIC] mode=monolith frameId={} batchSize={} transitions=0 storagePuts=0 storageGets=0 durationMs={}",
                request.frameId(), request.batchSize(), elapsed);

        return new ProcessResponse(
                request.frameId(),
                "monolith",
                request.batchSize(),
                0,
                0,
                0,
                elapsed,
                summary
        );
    }

    public ProcessResponse processDistributed(FrameRequest request) {
        long start = System.currentTimeMillis();
        String payload = request.payload() != null ? request.payload() : "";
        int batch = request.batchSize();

        String batchKey = request.frameId() + "-batch-" + batch;
        restClient.put()
                .uri(config.getStorageUrl() + "/api/storage/" + batchKey)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        int puts = 1;
        int gets = 0;
        int transitions = 1; 

        for (int i = 0; i < batch; i++) {
            restClient.post()
                    .uri(config.getDetectorAUrl() + "/api/detect/a/" + batchKey)
                    .retrieve()
                    .body(String.class);
            gets++;
            transitions++;

            restClient.post()
                    .uri(config.getDetectorBUrl() + "/api/detect/b/" + batchKey)
                    .retrieve()
                    .body(String.class);
            gets++;
            transitions++;
        }
        transitions++; 

        long elapsed = System.currentTimeMillis() - start;
        log.info("[METRIC] mode=distributed frameId={} batchSize={} transitions={} storagePuts={} storageGets={} durationMs={}",
                request.frameId(), batch, transitions, puts, gets, elapsed);

        return new ProcessResponse(
                request.frameId(),
                "distributed",
                batch,
                transitions,
                puts,
                gets,
                elapsed,
                "processed-batch-" + batch
        );
    }

    private String heavyCpuCheck(String detector, String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] data = payload.getBytes(StandardCharsets.UTF_8);
            byte[] hash = digest.digest(data);
            for (int i = 0; i < 25; i++) {
                hash = digest.digest(hash);
            }
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return detector + ":" + hex;
        } catch (Exception e) {
            throw new RuntimeException("erro no processamento " + detector, e);
        }
    }
}