package org.aiassistant.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Shared helpers for pushing Server-Sent Events to a client. Centralised here so every
 * streaming flow (code generation, requirement analysis, ...) emits events the same way.
 */
public final class SseUtil {

    private static final Logger log = LoggerFactory.getLogger(SseUtil.class);

    /** Common SSE event names the frontend can subscribe to. */
    public static final String EVENT_PROGRESS = "progress";
    public static final String EVENT_COMPLETE = "complete";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_ZIP = "zip";
    public static final String EVENT_S3 = "s3";
    public static final String EVENT_DELETE = "delete";

    /*
     * Monotonic, strictly-increasing event id sequence.
     *
     * The previous implementation used System.currentTimeMillis() as the id. Progress
     * events are emitted in tight loops, so several fire within the same millisecond and
     * end up sharing an id (and are not guaranteed to increase). SSE clients treat the id
     * as the Last-Event-ID for reconnection, so duplicate / non-monotonic ids cause events
     * to be de-duplicated or dropped on the client. A process-wide atomic counter keeps
     * every id unique and increasing.
     */
    private static final AtomicLong EVENT_ID = new AtomicLong();

    private SseUtil() {
    }

    /**
     * Sends a single SSE event to the client. Any failure (typically the client having
     * disconnected) is swallowed so it never interrupts the flow that is streaming.
     */
    public static void sendEvent(SseEmitter emitter, String eventName, String message) {
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(EVENT_ID.incrementAndGet()))
                    .name(eventName)
                    .data(message));
        } catch (Exception ex) {
            // Client likely disconnected; keep working without failing the run.
            log.debug("Failed to send SSE event '{}' (client likely disconnected)", eventName, ex);
        }
    }

    /**
     * Emits a human-readable progress line under the {@code progress} event.
     */
    public static void sendProgress(SseEmitter emitter, String message) {
        sendEvent(emitter, EVENT_PROGRESS, message);
    }
}
