package br.com.argonavis.eipcourse.exercises.ch3.exercise;

import br.com.argonavis.eipcourse.exercises.ch3.MockData;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;

import javax.jms.ConnectionFactory;

/**
 * @author dceschmidt
 * @since 29/09/15
 */
public class ExerciseWithCamel {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Connexao com o ActiveMQ
        ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

            }
        });
        context.start();

        ProducerTemplate template = context.createProducerTemplate();
        for (String xml : MockData.getMockData()) {
            template.sendBodyAndHeader("jms:queue:inbound", xml, "type", "xml");

        }




        System.out.println("O servidor está no ar. "
                + "O servidor ficará no ar por 20 segundos.");
        Thread.sleep(20000);
        context.stop();

//        CamelContext context = new DefaultCamelContext();
//        ProducerTemplate template = context.createProducerTemplate();
//        template.sendBody("jms:queue:inbound-channel");
//
//
//
//        Exchange exchange = new DefaultExchange(context);
//        Message message = exchange.getIn();
//        message.setBody("Hello World!");
//        message.setHeader("category", "greeting");
//        message.setHeader("content-type", "text/plain");
    }

}
