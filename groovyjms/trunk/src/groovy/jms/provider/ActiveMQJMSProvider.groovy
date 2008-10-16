package groovy.jms.provider

import javax.jms.ConnectionFactory
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.broker.BrokerRegistry
import org.apache.activemq.broker.BrokerService
import org.apache.activemq.pool.PooledConnectionFactory;

class ActiveMQJMSProvider implements JMSProvider {
    public static final String BROKER_NAME = "groovy.jms.provider.ActiveMQJMSProvider.broker"
    public static final String CONNECTOR_URL = "vm://localhost"
    BrokerService broker;
    ConnectionFactory connectionFactory;

    synchronized protected startBroker() {
        if (!broker) {
            BrokerRegistry registry = BrokerRegistry.getInstance();
            broker = registry.findFirst() ?: new BrokerService(brokerName: BROKER_NAME, useJmx: false,
                    persistent: false, useShutdownHook: false, transportConnectorURIs: [CONNECTOR_URL])
            if (!broker.transportConnectorURIs.find {it == CONNECTOR_URL}) broker.addConnector(CONNECTOR_URL)
        }
        if (!broker.isStarted()) broker.start()
    }

    public ConnectionFactory getConnectionFactory() {
        try {
            startBroker();
            connectionFactory = (connectionFactory) ?: new ActiveMQConnectionFactory(CONNECTOR_URL)
            return connectionFactory;
        } catch (NoClassDefFoundError e) {
            throw new ClassNotFoundException("Cannot find ActiveMQ library, please put ActiveMQ jar in the classpath. e: ${e.message}");
        }
    }

}