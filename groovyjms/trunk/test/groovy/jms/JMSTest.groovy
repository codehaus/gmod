package groovy.jms;

import groovy.util.GroovyTestCase;
import groovy.jms.provider.ActiveMQJMSProvider;

import javax.jms.ConnectionFactory
import javax.jms.MessageListener
import javax.jms.Message;

public class JMSTest extends GroovyTestCase {
    ActiveMQJMSProvider provider;
    ConnectionFactory jms; //simulate injection

    void setUp() { provider = new ActiveMQJMSProvider(); jms = provider.getConnectionFactory() }

    void tearDown() { provider.broker?.stop() }

    void testSimple() {
        String messageToSend = "this is the message to send"
        String messageToCheck;
        new JMS() {
            jms.session().topic("testTopic0").subscribe({Message m -> messageToCheck = m.text} as MessageListener)
            jms.topic("testTopic0").send(messageToSend); jms.close();
        }
        sleep(1000)
        assertEquals("callback message doesn't match", messageToSend, messageToCheck)
    }
}
