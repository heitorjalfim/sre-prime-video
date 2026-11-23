package com.fccpd.app;

public record ProcessResponse(
        String frameId,
        String mode,
        int framesProcessed,
        int transitions,
        int storagePuts,
        int storageGets,
        long durationMs,
        String summary
) {}