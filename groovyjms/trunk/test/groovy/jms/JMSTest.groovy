package groovy.jms

import groovy.jms.provider.ActiveMQJMSProvider
import javax.jms.ConnectionFactory
import javax.jms.Connection;
import static groovy.jms.JMS.jms
import javax.jms.MapMessage
import javax.jms.MessageListener
import java.util.concurrent.CopyOnWriteArrayList;

public class JMSTest extends GroovyTestCase {
    ActiveMQJMSProvider provider;
    ConnectionFactory factory; //simulate injection

    void setUp() { provider = new ActiveMQJMSProvider(); factory = provider.getConnectionFactory() }

    void tearDown() {
        //try{provider.broker?.stop()}catch(e){}
    }

    void testJMSProviderSystemProperty() {
        System.setProperty(JMS.SYSTEM_PROP_JMSPROVIDER, "groovy.jms.provider.ActiveMQJMSProviderNOTEXISTED")
        JMS.provider = null
        assertNull(new JMS().provider)
        System.setProperty(JMS.SYSTEM_PROP_JMSPROVIDER, JMS.DEFAULT_JMSPROVIDER)
    }

    void testGetConnectionSession() {
        Connection c = factory.createConnection()
        new JMS(c) {  //read-only connection and session are automatically available in the JMS closure scope
            assertEquals c, connection
            assertNotNull session
            println connection
            println session
        }
        c.close()
    }

    void testSimple() {
        String result, result2;
        new JMS() {
            "greetingroom".subscribe { result = it.text}
            "how are you?".publishTo "greetingroom"
        }

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
            assertEquals("testdata", "testqueue".receive(within: 200).text)
        }

        jms(factory) {
            "testqueue".send("testdata1")
            assertEquals("testdata1", "testqueue".receive(within: 200).text)
        }
    }

    void testEachMessage() {
        def jms = new JMS()
        jms.setAutoClose(false)
        jms.send(toQueue: 'testQueue', 'message': 'hello')
        jms.send(toQueue: 'testQueue', 'message': 'hello2')
        int count = 0
        jms.eachMessage("testQueue") {m ->
            count++
            assertTrue(m.text?.startsWith("hello"))
        }
        assertEquals 2, count
    }

    void testSubscriptionEMC() {
        def jms = new JMS().with {it.setAutoClose(false); it}, topic = "testTopic", result = [] as CopyOnWriteArrayList
        def listener = {MapMessage m -> result << m} as MessageListener
        jms.onMessage("myTopic", listener)
        assertNotNull(listener.subscriptionName)
        jms.close()
    }

    void testOnMessageStopMessage() {
        def jms = new JMS().with {it.setAutoClose(false); it}, topic = "testTopic", result = [] as CopyOnWriteArrayList

        // 1 - listen with Closure
        def listener = {MapMessage m -> result << m.getString('key0')} as MessageListener
        jms.onMessage(topic, listener)
        println listener.subscriptionName
        jms.send toTopic: topic, message: [key0: 'value0']
        println listener.subscriptionName
        sleep(500)
        println listener.subscriptionName
        assertEquals 1, result.size()
        assertEquals 'value0', result[0]
        result.clear()
        println listener.subscriptionName
        jms.stopMessage(listener)

        // 2 -listen with anonymous closure
        jms.onMessage(topic) {MapMessage m -> result << m.getString('key0')}
        jms.send toTopic: topic, message: [key0: 'value1']
        sleep(500)
        assertEquals("fail to unsubscribe", 1, result.size())
        assertEquals 'value1', result[0]
        result.clear()

        jms.close()
    }

    void testReceive() {
        def jms = new JMS()
        jms.setAutoClose(false)
        jms.send(toQueue: 'testQueue', 'message': 'hello')
        jms.send(toQueue: 'testQueue', 'message': [key: 'value'])
        def result = []
        jms.receive(fromQueue: 'testQueue', within: 500, with: {result = it}) // only support receiveAll
        assertEquals 2, result.size()
        jms.receive(fromQueue: 'testQueue', within: 500, with: {result = it}) // no more message
        assertEquals 0, result.size()

        jms.send(toQueue: 'testQueue', 'message': 'hello2')
        result = []
        jms.receive(fromQueue: 'testQueue', within: 500) {result = it.text} // put closure at the end
        assertEquals 1, result.size()
        jms.close();
    }

    void testNewInstance() {
        def jms = JMS.newInstance()
        jms.setAutoClose(false)
        assertNotNull jms
        def result = []
        jms.send toQueue: 'queue0', message: 'newinstance'
        jms.receive fromQueue: 'queue0', within: 1000, with: {result = it}
        assertNotNull result
        assertEquals 1, result.size()
        jms.close();
    }
}
