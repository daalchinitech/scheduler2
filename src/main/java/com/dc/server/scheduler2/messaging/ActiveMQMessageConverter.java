package com.dc.server.scheduler2.messaging;


import com.dc.server.data.transportable.Jsonable;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.data.util.Pair;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.*;


@Component
public class ActiveMQMessageConverter implements MessageConverter {

    @Override
    public Message toMessage (Object object, Session session) throws JMSException, MessageConversionException {
        final TextMessage textMessage = session.createTextMessage();
        if (object instanceof Jsonable) {
            textMessage.setText(((Jsonable) object).json());
            return textMessage;
        }

        textMessage.setText(object.toString());
        return textMessage;
    }

    @Override
    public Object fromMessage (Message message) throws JMSException, MessageConversionException {
        final String jsonStr;
        String physicalName = "";
        if (message instanceof ActiveMQTextMessage) {
            final ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
            jsonStr = textMessage.getText();
            physicalName = textMessage.getDestination().getPhysicalName();
        } else if (message instanceof ActiveMQBytesMessage) {
            final ActiveMQBytesMessage bytesMessage = (ActiveMQBytesMessage) message;
            jsonStr = new String(bytesMessage.getContent().getData());
            physicalName = bytesMessage.getDestination().getPhysicalName();
        } else {
            jsonStr = message.getBody(String.class);
            final Destination destination = message.getJMSDestination();
            if (destination instanceof Topic) physicalName = ((Topic) destination).getTopicName();
            else if (destination instanceof Queue) physicalName = ((Queue) destination).getQueueName();
        }

        return Pair.of(physicalName, jsonStr);
    }

}
