package com.interviewpractice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        // ✅ 允许所有 WebSocket 消息（开发环境）
        messages
                .simpDestMatchers("/app/**").permitAll()
                .simpSubscribeDestMatchers("/topic/**", "/queue/**").permitAll()
                .anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // ✅ 禁用同源检查（允许跨域 WebSocket）
        return true;
    }
}
