package com.fccpd.app;

public record FrameRequest(
        String frameId,
        String payload,
        int batchSize
) {
    public FrameRequest {
        if (batchSize <= 0) {
            batchSize = 1;
        }
    }
}