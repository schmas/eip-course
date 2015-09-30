package br.com.argonavis.eipcourse.exercises.ch3.exercise;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * @author dceschmidt
 * @since 29/09/15
 */
public class ExerciseMessageConsumer {

    public static void main(String[] args) {
        try {
            Context ctx = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
            Queue queue = (Queue) ctx.lookup("inbound");

            Connection con = factory.createConnection();
            final Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageConsumer consumer = session.createConsumer(queue);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    final String type;
                    try {
                        type = message.getStringProperty("type");
                        if (type != null && type.equals("xml")) {

                            System.out.println(((TextMessage) message).getText());
                        }

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            con.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
