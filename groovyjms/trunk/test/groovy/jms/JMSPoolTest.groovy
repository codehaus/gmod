package groovy.jms

import groovy.jms.provider.ActiveMQPooledJMSProvider
import java.util.concurrent.Future
import org.apache.activemq.pool.PooledConnectionFactory
import org.apache.log4j.Logger
import javax.jms.Queue

import static groovy.jms.JMS.jms
import java.util.concurrent.TimeUnit
import org.apache.log4j.MDC

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
        def pool = new JMSPool(corePoolSize: 1, maximumPoolSize: 1, keepAliveTime: 1, unit: TimeUnit.SECONDS)
        Queue q = pool.createQueue("testAbstractGetQueue")
        assertNotNull(q)
        assertEquals("testAbstractGetQueue", q.getQueueName())
    }

    void testMultipleSenderSingleReceiverOnQueue() { // just to prove message could be sent
        def pool = new JMSPool(maximumPoolSize: 10), result = [], counter = 0, count = 20, queueName = "testMultipleSenderSingleReceiverOnQueue"
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

    void testQueueOnMessageWithTwoPools() {
        def results = [], incomingPool = new JMSPool(), outgoingPool = new JMSPool(), queue = "testQueueOnMessageWithTwoPools"
        new Thread() {
            org.apache.log4j.MDC.put("tid", Thread.currentThread().getId());
            incomingPool.onMessage([queue: queue, threads: 1]) {m -> logger.debug("testQueueOnMessage() - received message m: ${m}"); result << m}
        }.start()
        new Thread() {
            org.apache.log4j.MDC.put("tid", Thread.currentThread().getId());
            outgoingPool.send(toQueue: queue, message: 'this is a test')
        }.start()
        sleep(500)
        assertEquals "fail to receive message", 1, results.size()
    }


    void testQueueSendMessage() {
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