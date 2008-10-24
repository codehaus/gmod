package groovy.jms

import groovy.jms.provider.ActiveMQPooledJMSProvider
import java.util.concurrent.Future
import org.apache.activemq.pool.PooledConnectionFactory
import org.apache.log4j.Logger
import javax.jms.Queue

import static groovy.jms.JMS.jms
import java.util.concurrent.TimeUnit

class JMSPoolTest extends GroovyTestCase {
    static Logger logger = Logger.getLogger(JMSPoolTest.class.name)
    ActiveMQPooledJMSProvider provider;

    void setUp() {
        //provider = new ActiveMQPooledJMSProvider()
        //provider.getConnectionFactory(); //trigger init
    }

    void testConstructor() {
        def pool = new JMSPool()
        assertNotNull pool?.connectionFactory
        assertTrue(pool?.connectionFactory instanceof PooledConnectionFactory)
        println pool
        pool.shutdown()
    }

    void testOnMessageThread() {
        def pool = new JMSPool()
        pool.onMessage(topic: 'testTopic', threads: 1) {m -> println m}
        sleep(500)
        pool.shutdown()
    }

    void testAbstractGetQueue() {
        fail()
        def pool = new JMSPool(corePoolSize: 1, maximumPoolSize: 1, keepAliveTime: 1, unit: TimeUnit.SECONDS)
        Queue q = pool.createQueue("testAbstractGetQueue")
        assertNotNull(q)
        assertEquals("testAbstractGetQueue", q.getQueueName())
    }

    void testMultipleSenderSingleReceiverOnQueue() { // just to prove message could be sent
        def pool = new JMSPool(maximumPoolSize:10), result = [], counter = 0, count = 20, queueName = "testMultipleSenderSingleReceiverOnQueue"
        sleep(100)
        count.times { pool.send(toQueue: queueName, message: 'message #' + it) }
        sleep(500)

        jms(pool.connectionFactory) {
            result += queueName.receiveAll(within: 2000)
        }
        result?.eachWithIndex {it, i -> println "$i\t$it"}
        assertEquals(count, result.size())

        sleep(5000)
        pool.shutdown()
        sleep(10000)
        //use stable non-Pool JMS to retreive and verify results
    }

    void testTopicOnMessage() {
        def pool = new JMSPool()
        def result = []
        pool.onMessage([topic: 'testTopic', threads: 1]) {m -> logger.debug("testTopicOnMessage() - received message m: ${m}"); result << m}
        sleep(1000)
        pool.send(toTopic: 'testTopic', message: 'this is a test')
        sleep(1000)
        result.eachWithIndex {it, i -> println "$i\t$it"}
        assertEquals(1, result.size())
    }

    void testQueueSendMessage() {
        fail()
        def pool = new JMSPool();
        def result = []
        pool.receive(fromQueue: 'testQueue') {m -> result += m}
        assertNotNull(result)
        assertEquals("there are outstanding message in the previous test case", 0, result?.size())
        result.clear()
        pool.send(toQueue: 'testQueue', message: 'message 1')
        //jms.send(toQueue: 'testQueue', message: 'message 2')
        pool.receive(fromQueue: 'testQueue', within: 2000) {m -> result += m}
        assertNotNull(result)
        result.eachWithIndex {it, i -> println "$i\t$it"}
        assertEquals(1, result?.size())
    }
}