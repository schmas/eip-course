package br.com.argonavis.eipcourse.exercises.ch3;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MockMessageProducer {

	private Connection con;
	private Session session;
	private MessageProducer producer;
	
	MockMessageProducer(Connection con, Destination destination) throws JMSException {
        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(destination);
	}
	
	public void send() throws JMSException {
		// EXERCICIO 2
	}

	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
			con = factory.createConnection();
			Destination queue = (Destination) ctx.lookup("inbound");
			
			MockMessageProducer producer = new MockMessageProducer(con, queue);
			System.out.println("Will send messages...");
			producer.send();
			System.out.println("Done.");

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
