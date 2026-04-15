package com.example.springbootthymeleaf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

import com.example.springbootthymeleaf.BroadcastWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new BroadcastWebSocketHandler(), "/websocket/broadcast")
                .setAllowedOrigins("*");
    }
}