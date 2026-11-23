package com.fccpd.app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MonitorController {

    private final MonitorConfig config;
    private final MonitorService service;

    public MonitorController(MonitorConfig config, MonitorService service) {
        this.config = config;
        this.service = service;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP:" + config.getRole());
    }

    @PostMapping("/frames")
    public ResponseEntity<ProcessResponse> processFrame(@RequestBody FrameRequest request) {
        if ("distributed".equalsIgnoreCase(config.getRole())) {
            return ResponseEntity.ok(service.processDistributed(request));
        }
        return ResponseEntity.ok(service.processMonolith(request));
    }

    @PutMapping("/storage/{key}")
    public ResponseEntity<Void> put(@PathVariable String key, @RequestBody String value) {
        service.putStorage(key, value);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/storage/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {
        return ResponseEntity.ok(service.getStorage(key));
    }

    @PostMapping("/detect/{detector}/{key}")
    public ResponseEntity<String> detect(@PathVariable String detector, @PathVariable String key) {
        return ResponseEntity.ok(service.runDetector(detector, key));
    }
}