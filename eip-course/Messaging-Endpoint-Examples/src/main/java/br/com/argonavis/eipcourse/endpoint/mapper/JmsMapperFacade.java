package br.com.argonavis.eipcourse.endpoint.mapper;

import java.io.StringReader;
import java.io.StringWriter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JmsMapperFacade implements SimpleMapperFacade {
	Destination requestQueue;
	Destination replyQueue;
	Connection con;

	public JmsMapperFacade(String requestQueueName, String replyQueueName)
			throws MapperException {
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			requestQueue = (Destination) ctx.lookup(requestQueueName);
			replyQueue = (Destination) ctx.lookup(replyQueueName);
			con.start();
		} catch (JMSException | NamingException e) {
			throw new MapperException(e);
		}
	}
	
	@Override
	public void closeConnection() {
		try {
			con.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void persist(Product p) {
		try {
			StringWriter writer = new StringWriter();

			JAXBContext jctx = JAXBContext.newInstance(Product.class);
			Marshaller m = jctx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(p, writer);

			String payload = writer.toString();

			Session session = con
					.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer sender = session.createProducer(requestQueue);
			TextMessage message = session.createTextMessage(payload);
			message.setStringProperty("Command", "addProduct");
			
			System.out.println("Sending persist request for " + p);
			sender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Product select(Long pk) throws ProductNotFoundException {
		try {
			Session session = con
					.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer requestor = session.createProducer(requestQueue);

			Message getProdutoCmd = session.createMessage();
			getProdutoCmd.setStringProperty("Command", "getProduct");
			getProdutoCmd.setLongProperty("ProductID", pk);
			getProdutoCmd.setJMSReplyTo(replyQueue);
			
			System.out.println("Sending select request for " + pk);
			requestor.send(getProdutoCmd); 

			MessageConsumer receiver = session.createConsumer(replyQueue); 
			
			System.out.println("Waiting for reply.");
			TextMessage reply = (TextMessage) receiver.receive();
			
			System.out.println("Reply received: " + reply.getText());
			
			if(!reply.getJMSCorrelationID().equals(getProdutoCmd.getJMSMessageID())) {
				System.out.println("Wrong correlation for request/reply!");
			}
			
			Object exception = reply.getObjectProperty("Exception");
			if(exception != null) {
				throw (ProductNotFoundException)exception;
			} else {
				StringReader reader = new StringReader(reply.getText());

				JAXBContext jctx = JAXBContext.newInstance(Product.class);
				Unmarshaller m = jctx.createUnmarshaller();
				Product p = (Product) m.unmarshal(reader);

				return p;
			}

		} catch (Exception e) {
			throw new ProductNotFoundException(e);
		}
	}

}
