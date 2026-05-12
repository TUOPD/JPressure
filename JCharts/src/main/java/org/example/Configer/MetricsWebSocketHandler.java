package org.example.Configer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Data.MetricsResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.example.Service.PressureService;
import java.io.IOException;
import java.util.concurrent.*;

@Component
public class MetricsWebSocketHandler implements WebSocketHandler {

    private final PressureService pressureService;
    private final ObjectMapper mapper = new ObjectMapper();
    private ScheduledExecutorService scheduler;

    public MetricsWebSocketHandler(PressureService pressureService) {
        this.pressureService = pressureService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                MetricsResponse metrics = pressureService.getMetrics();
                String json = mapper.writeValueAsString(metrics);
                session.sendMessage(new TextMessage(json));
            } catch (IOException ignored) {}
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Override public void handleMessage(WebSocketSession s, WebSocketMessage<?> m) {}
    @Override public void handleTransportError(WebSocketSession s, Throwable e) {}
    @Override public boolean supportsPartialMessages() { return false; }
}