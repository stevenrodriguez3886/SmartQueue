package smartqueue;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @brief Configuration class for WebSocket and STOMP messaging.
 * * Enables real-time, bi-directional communication between the server and the browser.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * @brief Configures the message broker routes.
     * @param config The registry to configure.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enables a simple memory-based message broker to carry messages back to the client
        // Topics prefixed with "/topic" will be broadcasted to subscribed clients
        config.enableSimpleBroker("/topic");
        
        // Defines the prefix for messages sent FROM the client to the server
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * @brief Registers the endpoint used by the frontend to connect to the WebSocket server.
     * @param registry The registry to configure endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers the "/ws" endpoint. 
        // withSockJS() provides fallback options for browsers that don't support raw WebSockets.
        registry.addEndpoint("/ws").withSockJS();
    }
}