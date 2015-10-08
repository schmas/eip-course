package br.com.argonavis.cursocamel.si.twitter;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.social.twitter.api.Tweet;

public class Application {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("integration.xml");

		CamelContext context = (CamelContext) ctx.getBean("camelContext");

		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));
		context.addRoutes(new RouteBuilder() {
			
			public void configure() {
				from("jms:queue:spring-tweets")
                   .process(new Processor() {
                	   public void process(Exchange e) {
                		   System.out.println("Message: " + e.getIn().getBody());
                		   
                		    Tweet tweet = (Tweet)e.getIn().getBody();
                			System.out.println("Sender: " + tweet.getFromUser());
                			System.out.println("Text: " + tweet.getText());
                	   }
                   });
			}
		});
		context.start();

		while (true) {
		}
	}

}
