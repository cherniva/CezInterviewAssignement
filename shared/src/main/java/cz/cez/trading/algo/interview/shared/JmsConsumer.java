package cz.cez.trading.algo.interview.shared;

import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JmsConsumer implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Orderbook orderbook;

    @Override
    @JmsListener(destination = "cez.trading.algo.interview")
    public void onMessage(Message message) {
        try{
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage)message;

            String json = activeMQTextMessage.getText();
            LOG.info("Thread #{}, Received Message: {}", Thread.currentThread().getId(), json);

            //Serialize to object
            Order order = objectMapper.readValue(json, Order.class);
            LOG.info(order.toString());

            orderbook.processOrder(order);

        } catch(Exception e) {
            LOG.error("Received Exception : " + e);
        }
    }

}
