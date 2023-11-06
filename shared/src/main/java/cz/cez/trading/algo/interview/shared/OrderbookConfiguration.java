package cz.cez.trading.algo.interview.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderbookConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public JmsConsumer jmsConsumer() {
        return new JmsConsumer(objectMapper);
    }
}
