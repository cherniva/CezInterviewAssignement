package cz.cez.trading.algo.interview.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.qdox.model.expression.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync(proxyTargetClass = true)
public class OrderbookConfiguration {

    @Bean
    public JmsConsumer jmsConsumer() {
        return new JmsConsumer();
    }

    @Bean
    public OrderbookImpl orderbook() {
        return new OrderbookImpl();
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        executor.setMaxPoolSize(availableProcessors/2 > 0 ? availableProcessors : 1); // use half of cores

        return executor;
    }
}
