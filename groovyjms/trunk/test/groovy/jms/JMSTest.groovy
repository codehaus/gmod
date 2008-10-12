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
            println connection
            println session
        }

    }

    void testSimple() {
        String result, result2;
        new JMS() {
            "greetingroom".subscribe { result = it.text}
            "how are you?".publishTo "greetingroom"
        }
        sleep(1000)
        assertEquals("how are you?", result)
    }

    void testMapMessage() {
        Map data = ['int': 100, 'double': 100000000d, 'string': 'string', 'character': 'd' as char], result;
        new JMS() {
            "testqueue".send(data)
            sleep(1000);
              result = "testqueue".receive().toMap()
        }
        assertEquals data.'int', result.'int'
        assertEquals data.'double', result.'double'
        assertEquals data.'string', result.'string'
        assertEquals data.'character', result.'character'
    }
}
