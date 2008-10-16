package groovy.jms

import org.apache.activemq.pool.PooledConnectionFactory


class JMSPoolTest extends GroovyTestCase {
    void testConstructor() {
        def pool = new JMSPool()
        assertNotNull pool?.factory
        println pool?.factory 
        assertTrue (pool?.factory instanceof PooledConnectionFactory)
    }
}