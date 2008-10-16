package groovy.jms.provider

import javax.jms.ConnectionFactory
import org.apache.activemq.pool.PooledConnectionFactory

class ActiveMQPooledJMSProvider extends ActiveMQJMSProvider {
    /**
     * ActiveMQ Default
     * maxConnections = 1
     * maximumActive = 500    //  maximum number of active sessions per connection
     * idleTimeout = 30*1000
     */
    Map connectionFactoryConfig = [maxConnections: 500, maximumActive: 1, idleTimeout: 30 * 1000]

    ActiveMQPooledJMSProvider(Map cfg = null) {
        if (cfg) cfg.each {k, v -> connectionFactoryConfig.put(k, v)}
    }

    public ConnectionFactory getConnectionFactory() {
        try {
            startBroker();
            connectionFactory = (connectionFactory) ?: new PooledConnectionFactory(CONNECTOR_URL)
            connectionFactoryConfig.each {k, v -> connectionFactory.'$k' = v}
            return connectionFactory;
        } catch (NoClassDefFoundError e) {
            throw new ClassNotFoundException("Cannot find ActiveMQ Pool library, please put ActiveMQ jar in the classpath. e: ${e.message}");
        }
    }
}