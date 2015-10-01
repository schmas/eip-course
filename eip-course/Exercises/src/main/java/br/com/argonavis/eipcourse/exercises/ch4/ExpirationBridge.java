package br.com.argonavis.eipcourse.exercises.ch4;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import br.com.argonavis.eipcourse.exercises.utils.JMSUtils;

public class ExpirationBridge implements MessageListener {
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;

	public ExpirationBridge(Connection con, Destination in, Destination out)
			throws JMSException {
		System.out.println("Bridge from " + in + " to " + out);
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(out);
		consumer = session.createConsumer(in);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message incomingMessage) {
		try {
			// Exercicio 5 - Troque o DEFAULT_TIME_TO_LIVE por um valor em milissegundos
			producer.send(incomingMessage, Message.DEFAULT_DELIVERY_MODE, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE); 
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			
			Destination from = (Destination) ctx.lookup("inbound");
			Destination to   = (Destination) ctx.lookup("outbound");

			new ExpirationBridge(con, from, to);

			System.out.println("Receiving messages for 60 seconds...");
			Thread.sleep(60000);
			System.out.println("Done.");
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (JMSException e) {
				}
			}
		}
	}
}
