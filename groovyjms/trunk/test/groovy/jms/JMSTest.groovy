package groovy.jms

import groovy.jms.provider.ActiveMQJMSProvider
import javax.jms.ConnectionFactory;

public class JMSTest extends GroovyTestCase {
    ActiveMQJMSProvider provider;
    ConnectionFactory jms; //simulate injection

    void setUp() { provider = new ActiveMQJMSProvider(); jms = provider.getConnectionFactory() }

    void tearDown() {
        //try{provider.broker?.stop()}catch(e){}
    }

    void testJMSProviderSystemProperty(){
       System.setProperty(JMS.SYSTEM_PROP_JMSPROVIDER, "groovy.jms.provider.ActiveMQJMSProviderNOTEXISTED")
        assertNull( new JMS(){}.provider)
    }

    void testSimple() {
        String result;
        new JMS() {
            subscribeTo:"greetingroom" with { result = it.text}
            "how are you?".publishTo "greetingroom"
        }
        sleep(1000)
        assertEquals("how are you?", result)
    }
}
