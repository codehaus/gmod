package groovy.jms


class JMSPoolTest extends GroovyTestCase {
    void testConstructor() {
        assertNotNull new JMSPool()
    }
}