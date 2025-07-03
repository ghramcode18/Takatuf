package geekcode.takatuf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // هذا هو المسار يلي تربطي عليه
                .setAllowedOriginPatterns("*")// ضروري للسماح للـ frontend يتصل
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // الوجهات يلي بتم الاستقبال منها
        config.setApplicationDestinationPrefixes("/app"); // الوجهة يلي بترسلي عليها من الفرونت
    }
}
