package groovy.jms

import org.apache.activemq.pool.PooledConnectionFactory
import java.util.concurrent.Future


class JMSPoolTest extends GroovyTestCase {
    void testConstructor() {
        def pool = new JMSPool()
        assertNotNull pool?.factory
        println pool?.factory
        assertTrue(pool?.factory instanceof PooledConnectionFactory)
    }

    void testOnMessage() {
        def jms = new JMSPool()
        jms.onMessage(topic: 'testTopic', threads: 1) {m ->
            println m
        }
        sleep(5000)
        jms.futures.eachWithIndex {Future f, i ->
            println "$i\t${f?.isDone()}"
        }
          sleep(5000)
    }
}