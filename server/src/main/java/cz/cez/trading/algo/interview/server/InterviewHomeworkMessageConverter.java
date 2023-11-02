package cz.cez.trading.algo.interview.server;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.cez.trading.algo.interview.shared.Order;

public class InterviewHomeworkMessageConverter extends MappingJackson2MessageConverter {
	private final ObjectMapper objectMapper;
	public InterviewHomeworkMessageConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.setObjectMapper(objectMapper);
		this.setTargetType(MessageType.TEXT);
	}

	@Override
	protected JavaType getJavaTypeForMessage(Message message) throws JMSException {
		return this.objectMapper.constructType(Order.class);
	}

}
