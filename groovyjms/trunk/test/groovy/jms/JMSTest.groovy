package groovy.jms

import groovy.jms.provider.ActiveMQJMSProvider
import javax.jms.ConnectionFactory
import javax.jms.Connection;
import static groovy.jms.JMS.jms;

public class JMSTest extends GroovyTestCase {
    ActiveMQJMSProvider provider;
    ConnectionFactory factory; //simulate injection

    void setUp() { provider = new ActiveMQJMSProvider(); factory = provider.getConnectionFactory() }

    void tearDown() {
        //try{provider.broker?.stop()}catch(e){}
    }

    void testJMSProviderSystemProperty() {
        System.setProperty(JMS.SYSTEM_PROP_JMSPROVIDER, "groovy.jms.provider.ActiveMQJMSProviderNOTEXISTED")
        assertNull(new JMS() {}.provider)
    }

    void testGetConnectionSession() {
        Connection c = factory.createConnection()
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
        Map data = ['int': 100, 'double': 100000000d, 'string': 'string', 'character': 'd' as char];
        def results = []
        new JMS() {
            "testqueue".send(data)
            data.sendTo("testqueue")
            sleep(1000);
            results = "testqueue".receiveAll().collect {it.toMap()}
        }

        results.each {Map result ->
            assertEquals data.'int', result.'int'
            assertEquals data.'double', result.'double'
            assertEquals data.'string', result.'string'
            assertEquals data.'character', result.'character'
        }
    }

    void testStaticClosure() {
        jms {
            "testqueue".send("testdata")
            sleep(500)
            assertEquals("testdata", "testqueue".receive().text)
        }

        jms(factory) {
            "testqueue".send("testdata1")
            sleep(500)
            assertEquals("testdata1", "testqueue".receive().text)
        }
    }

    void testEachMessage() {
        def jms = new JMS()
        jms.setAutoClose(false)
        jms.send(toQueue: 'testQueue', 'message': 'hello')
        jms.send(toQueue: 'testQueue', 'message': 'hello2')
        jms.eachMessage("testQueue") {m ->
            assertTrue(m.text?.startsWith("hello"))
        }
    }


}
