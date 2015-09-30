package br.com.argonavis.eipcourse.exercises.ch3.exercise;

import br.com.argonavis.eipcourse.exercises.ch3.MockData;

import javax.jms.*;
import javax.naming.InitialContext;

/**
 * @author dceschmidt
 * @since 29/09/15
 */
public class ExerciseMessageProducer {

    public static void main(String[] args) {
        final InitialContext context;
        try {
            context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
            Destination destination = (Destination) context.lookup("inbound");
            try (Connection con = connectionFactory.createConnection()) {
                con.start();
                final Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final MessageProducer producer = session.createProducer(destination);

                for (String xml : MockData.getMockData()) {
                    final Message message = session.createTextMessage(xml);
                    message.setStringProperty("type", "xml");
                    producer.send(message);
                }

                for (String xml : MockData.getMockData()) {
                    final Message message = session.createTextMessage(xml);
                    message.setStringProperty("type", "xxx");
                    producer.send(message);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
