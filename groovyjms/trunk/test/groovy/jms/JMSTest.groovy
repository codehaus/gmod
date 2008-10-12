package groovy.jms

import groovy.jms.provider.ActiveMQJMSProvider
import javax.jms.ConnectionFactory
import javax.jms.Connection;

public class JMSTest extends GroovyTestCase {
    ActiveMQJMSProvider provider;
    ConnectionFactory jms; //simulate injection

    void setUp() { provider = new ActiveMQJMSProvider(); jms = provider.getConnectionFactory() }

    void tearDown() {
        //try{provider.broker?.stop()}catch(e){}
    }

    void testJMSProviderSystemProperty() {
        System.setProperty(JMS.SYSTEM_PROP_JMSPROVIDER, "groovy.jms.provider.ActiveMQJMSProviderNOTEXISTED")
        assertNull(new JMS() {}.provider)
    }

    void testGetConnectionSession() {
        Connection c = jms.createConnection()
        new JMS(c) {  //read-only connection and session are automatically available in the JMS closure scope
            assertEquals c, connection
            assertNotNull session
        }

    }

    void testSimple() {
        String result, result2;
        new JMS() {
            subscribeTo("greetingroom").with { result = it.text} // subscribeTo("greetingroom") is the same as getConnection().topic("greetingroom")

            "how are you?".publishTo "greetingroom"
        }
        sleep(1000)
        assertEquals("how are you?", result)
    }
}
