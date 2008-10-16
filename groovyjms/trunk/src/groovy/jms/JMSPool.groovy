package groovy.jms

import javax.jms.ConnectionFactory
import groovy.jms.provider.ActiveMQPooledJMSProvider

/**
 * JMSPool extends JMS and provided configurable JMS Pooling. Unlike the "raw" JMS, you don't need to provide a connection
 * or session to it. It aims at providing something similar to Spring DefaultMessageListenerContainer 
 *
 * "A Session object is a single-threaded context for producing and consuming messages", in order to enjoy better
 * throughput in both incoming and outgoing messaging, you are recommended to use the JMSPool over JMS. JMSPool could
 * be configured to establish multiple session in mutiple thread to consume or produce messages.
 *
 * The JMSPool utilize ActiveMQ SessionPool for sending message.
 *
 *
 *
 */
class JMSPool extends JMS {
    private static final defaultCfg = [:]

    JMSPool(Map cfg = null, Closure c) {
        super(null, null, c)
        this.provider = new ActiveMQPooledJMSProvider(cfg);
        this.factory = provider.getConnectionFactory()
    }

    def init() {}

}