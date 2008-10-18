package groovy.jms

import groovy.jms.provider.ActiveMQPooledJMSProvider
import java.util.concurrent.Future
import org.apache.activemq.pool.PooledConnectionFactory
import org.apache.log4j.Logger
import static groovy.jms.JMS.jms

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
        pool.shutdown()
    }

    void testOnMessageThread() {
        def pool = new JMSPool()
        pool.onMessage(topic: 'testTopic', threads: 1) {m -> println m}
        sleep(500)
        assertEquals(1, pool.jobs.size())
        assertFalse(pool.jobs[0].isDone())
        pool.shutdown()
    }

    void testMultipleSenderSingleReceiverOnQueue() { // just to prove message could be sent
        def pool = new JMSPool(), result = [], counter = 0, count = 20, queueName = "testMultipleSenderSingleReceiverOnQueue"
        //pool.onMessage(topic: 'testQueueOnMessageWithMultipleThreads', threads: 5) {m -> result << m}
        sleep(500)
        count.times {
            pool.send(toQueue: queueName, message: 'message #' + it)
        }
        sleep(1000)

        jms(pool.connectionFactory) {
            result += queueName.receiveAll(within: 2000)
        }
        result?.eachWithIndex {it, i -> println "$i\t$it"}
        assertEquals(count, result.size())

        sleep(5000)
        pool.shutdown()

        //use stable non-Pool JMS to retreive and verify results


    }

    void testTopicOnMessage() {
        def jms = new JMSPool()
        def result = []
        jms.onMessage([topic: 'testTopic', threads: 1]) {m -> logger.debug("testStopRunningPool() - m: ${m}"); result << m}
        sleep(1000)
        jms.send(toTopic: 'testTopic', message: 'this is a test')
        assertTrue(jms.jobs?.size() > 0)
        jms.jobs.eachWithIndex {Future f, i ->
            //println "$i\tisCancelled? ${f?.isCancelled()}\tisDone? ${f?.isDone()}\t$f";
            assertFalse("test job(s) is cancelled", f.isCancelled());
            f.cancel(true)
        }
        sleep(500)
        //assertTrue jms.stop(true, 2000)
        sleep(500)
        jms.jobs.eachWithIndex {Future f, i ->
            //println "$i\tisCancelled? ${f?.isCancelled()}\tisDone? ${f?.isDone()}\t$f";
            assertTrue(f.isCancelled())
        }
        result.each { println it}
        assertEquals(1, result.size())
    }

    void testQueueSendMessage() {
        def jms = new JMSPool();
        def result = []
        jms.send(toQueue: 'testQueue', message: 'message 1')
        //jms.send(toQueue: 'testQueue', message: 'message 2')
        assertEquals 0, jms.jobs.size()
        jms.receive(fromQueue: 'testQueue', within: 0) {m -> result += m}
        assertNotNull(result)
        assertEquals(1, result?.size())
    }
}