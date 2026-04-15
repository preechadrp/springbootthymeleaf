package com.example.springbootthymeleaf;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BroadcastWebSocketHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private static final Map<WebSocketSession, Boolean> sending = new ConcurrentHashMap<>();

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);

    static {
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String msg = queue.take();

                    // cleanup dead sessions
                    sessions.removeIf(s -> {
                        if (!s.isOpen()) {
                            sending.remove(s);
                            return true;
                        }
                        return false;
                    });

                    for (WebSocketSession session : sessions) {

                        Boolean isSending = sending.get(session);
                        if (isSending == null || isSending) {
                            continue;
                        }

                        sending.put(session, true);

                        session.sendMessage(new TextMessage(msg));

                        // สำคัญ: Spring sendMessage เป็น blocking → reset flag เอง
                        sending.put(session, false);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Broadcast error: {}", e.getMessage(), e);
                }
            }

            log.info("WebSocket broadcast thread stopped.");
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        sending.put(session, false);
        log.info("WebSocket connected: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        sending.remove(session);
        log.info("WebSocket closed: {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        sessions.remove(session);
        sending.remove(session);
        log.error("WebSocket error: {}", exception.getMessage());
    }

    // broadcast method เหมือนเดิม
    public static void broadcast(String message) {
        if (!queue.offer(message)) {
            log.warn("Queue full, dropping message");
        }
    }
}