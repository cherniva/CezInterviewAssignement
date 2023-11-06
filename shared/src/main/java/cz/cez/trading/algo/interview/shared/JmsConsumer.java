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

import java.util.Map;

//@Component
public class JmsConsumer implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConsumer.class);

    private final ObjectMapper objectMapper;

    public JmsConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @JmsListener(destination = "cez.trading.algo.interview")
    public void onMessage(Message message) {
        try{
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage)message;

            String json = activeMQTextMessage.getText();
            LOG.info("Received Message: " + json);

            //Serialize to object
            Order order = objectMapper.readValue(json, Order.class);
            LOG.info(order.toString());

        } catch(Exception e) {
            LOG.error("Received Exception : " + e);
        }
    }

//    @JmsListener(destination = "cez.trading.algo.interview", containerFactory = "activeMQConnectionFactory")
//    public void receiveMessage(Order order) {
//        System.out.println("Received <" + order + ">");
//    }
}
