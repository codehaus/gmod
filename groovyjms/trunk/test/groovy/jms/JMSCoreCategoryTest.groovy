package groovy.jms

import groovy.jms.provider.ActiveMQJMSProvider
import javax.jms.ConnectionFactory
import org.apache.activemq.command.ActiveMQMapMessage
import org.apache.activemq.command.ActiveMQTextMessage
import org.apache.activemq.command.ActiveMQObjectMessage

class JMSCoreCategoryTest extends GroovyTestCase {
    def provider = new ActiveMQJMSProvider(); // all test shares the same Broker for performance; shutdown hook is enabled by default
    ConnectionFactory jms; //simulate injection

    void setUp() { jms = provider.getConnectionFactory() }

    void tearDown() {JMS.close();}

    void testIsMessageClassEnhanced() { //make this the first method
        def jms = new JMS(), queue = 'testJMSEnhancement'
        jms.send(toQueue: queue, message: [test: 'data'])
        jms.send(toQueue: queue, message: "text message")
        jms.send(toQueue: queue, message: ["array list in object message"])
        def messages = jms.receive(fromQueue: queue, within: 500)
        def m0 = messages?.get(0), m1 = messages?.get(1), m2 = messages?.get(2)
        assertNotNull m0; assertEquals(ActiveMQMapMessage, m0.class);
        assertTrue(JMSUtils.isEnhanced(m0.class)); assertTrue(m0.isEnhanced()) //enhanced by ActiveMQJMSProvider
        assertNotNull m1; assertEquals(ActiveMQTextMessage, m1.class);
        assertTrue(JMSUtils.isEnhanced(m1.class)); assertTrue(m1.isEnhanced()) //enhanced by ActiveMQJMSProvider
        assertNotNull m2; assertEquals(ActiveMQObjectMessage, m2.class);
        assertTrue(JMSUtils.isEnhanced(m2.class)); assertTrue(m2.isEnhanced()) //enhanced by ActiveMQJMSProvider

    }
}