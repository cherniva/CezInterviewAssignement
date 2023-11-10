package cz.cez.trading.algo.interview.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.qdox.model.expression.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.jms.ConnectionFactory;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableJms
public class OrderbookConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(OrderbookConfiguration.class);

    private static final String[] PRODUCTS = new String[]{"ttf-jan-2023", "ttf-feb-2023", "ttf-mar-2023", "ttf-apr-2023"};

    @Autowired
    private OrderbookImpl orderbook;

    @Bean
    public JmsConsumer jmsConsumer() {
        return new JmsConsumer(orderbook);
    }

//    @Bean
//    public OrderbookImpl orderbook() {
//        return new OrderbookImpl();
//    }

    @Bean
    public JmsListenerContainerFactory<?> jmsTradingFactory(ConnectionFactory connectionFactory,
                                                            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all auto-configured defaults to this factory, including the message converter
        factory.setTaskExecutor(taskExecutor());
        configurer.configure(factory, connectionFactory);
        // You could still override some settings if necessary.
        return factory;
    }

    @Bean
    @Primary
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(availableProcessors);
        executor.setMaxPoolSize(16); // use half of cores
        executor.setThreadNamePrefix("AsyncPool-");
        executor.initialize();
        return executor;
    }

    @Bean
    public CommandLineRunner schedulingRunner() {
        return new CommandLineRunner() {
            public void run(String... args) throws Exception {
                Random random = new Random();
                while(true) {
                    try {
                        Thread.sleep(random.nextInt(100, 4000));
                    } catch (InterruptedException e) {
                        LOG.error("Interrupt Exception in CommandLineRunner");
                    }
//                    String product = PRODUCTS[random.nextInt(0, 4)];
//                    Side side = random.nextInt() % 2 == 0 ? Side.ASK : Side.BID;

//                    Stream<Order> orders = orderbook.getBestOrdersFor(product, side);
//                    LOG.info("Orders for {} {} have size: {}", product, side, Arrays.toString(orders.toArray()));

//                    CompletableFuture<Stream<Order>> orders = orderbook.getBestOrdersForAsync(product, side);
//                    CompletableFuture.allOf(orders).join();
//                    LOG.info("Orders for {} {} have size: {}", product, side, Arrays.toString(orders.get().toArray()));

                    int randomNum = random.nextInt(3,15);
                    CompletableFuture<Stream<Order>>[] orders = new CompletableFuture[randomNum];

                    for(int i = 0; i < randomNum; i++) {
                        try {
                            Thread.sleep(random.nextInt(10, 2000));
                        }
                        catch(InterruptedException e) {
                            LOG.error("Interrupt Exception in CommandLineRunner");
                        }

                        orders[i] = orderbook.getBestOrdersForAsync(
                                PRODUCTS[random.nextInt(0, 4)],
                                random.nextInt() % 2 == 0 ? Side.ASK : Side.BID
                        );
                    }
                    CompletableFuture.allOf(orders).join();

                    for(CompletableFuture<Stream<Order>> order : orders) {
                        LOG.info("--> " + Arrays.toString(order.get().toArray()));
                    }
                }
            }
        };
    }
}
