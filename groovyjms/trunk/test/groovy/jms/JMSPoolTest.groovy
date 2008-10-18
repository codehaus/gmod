package groovy.jms

import groovy.jms.provider.ActiveMQPooledJMSProvider
import java.util.concurrent.Future
import org.apache.activemq.pool.PooledConnectionFactory

class JMSPoolTest extends GroovyTestCase {
    ActiveMQPooledJMSProvider provider;

    void setUp() {
        //provider = new ActiveMQPooledJMSProvider()
        //provider.getConnectionFactory(); //trigger init
    }

    void testConstructor() {
        def pool = new JMSPool()
        assertNotNull pool?.factory
        println pool?.factory
        assertTrue(pool?.factory instanceof PooledConnectionFactory)
    }

    void testStopRunningPool() {
        def jms = new JMSPool()
        jms.onMessage(topic: 'testTopic', threads: 1) {m ->
            println m
        }
        sleep(500)
        assertTrue(jms.futures?.size() > 0)
        jms.futures.eachWithIndex {Future f, i ->
            //println "$i\t${f?.isDone()}\t$f"
            assertFalse("test job(s) is/are not running", f.isDone())
        }
        sleep(5000)
        jms.stop(true, 1000)
        sleep(5000)
        jms.futures.eachWithIndex {Future f, i ->
            //println "$i\t${f?.isDone()}\t$f"
            assertTrue(f.isDone())
        }
    }
}