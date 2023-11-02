package cz.cez.trading.algo.interview.server;

import javax.jms.Destination;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import com.fasterxml.jackson.databind.ObjectMapper;

@ComponentScan(basePackages = {"cz.cez.trading.algo.interview.server", "cz.cez.trading.algo.interview.shared"})
@SpringBootApplication
public class InterviewHomeworkServerApplication {
	private static final Logger LOG = LoggerFactory.getLogger(InterviewHomeworkServerApplication.class);

	public static void main(String[] args) {
		final ConfigurableApplicationContext context = SpringApplication.run(new Class<?>[] {
			InterviewHomeworkServerApplication.class}, args);
	}

	@Bean
	public Orders orders() {
		return new Orders();
	}

	@Bean
	public IntegrationFlow orderProducer(InterviewHomeworkMessageConverter converter) {
		final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616", "user", "secret");
		final Destination destination = new ActiveMQQueue("cez.trading.algo.interview");
		return IntegrationFlows
				.fromSupplier(() -> this.orders().doRandomChange(),
						e -> e.poller(p -> p.fixedDelay(100)))
				.handle(Jms
						.outboundAdapter(connectionFactory)
						.configureJmsTemplate(t -> t.jmsMessageConverter(converter))
						.destination(destination))
				.get();
	}

	@Bean
	public InterviewHomeworkMessageConverter converter(ObjectMapper objectMapper) {
		return new InterviewHomeworkMessageConverter(objectMapper);
	}

	/**
	 * Fixes bug in spring boot embedded artemis that leads to ignoring
	 * connection configuration for server
	 */
	@Bean
	public ArtemisConfigurationCustomizer customizer() {
		return c -> {
			try {
				c.addAcceptorConfiguration("netty", "tcp://localhost:61616");
			} catch (final Exception e) {
				throw new RuntimeException("Failed to add netty transport acceptor to artemis instance", e);
			}
		};
	}
}
