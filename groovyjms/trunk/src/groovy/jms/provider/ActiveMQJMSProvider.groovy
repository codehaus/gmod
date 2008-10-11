package groovy.jms.provider

import javax.jms.ConnectionFactory
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.broker.BrokerRegistry
import org.apache.activemq.broker.BrokerService

class ActiveMQJMSProvider implements JMSProvider {
    public static final String BROKER_NAME = "groovy.jms.provider.ActiveMQJMSProvider"
    public static final String CONNECTOR_URL = "vm://localhost"
    BrokerService broker;
    ConnectionFactory connectionFactory;

    public ConnectionFactory getConnectionFactory() {
        try {
            if (!broker) {
                BrokerRegistry registry = BrokerRegistry.getInstance();
                broker = registry.findFirst() ?: new BrokerService(persistent: false, transportConnectorURIs: [CONNECTOR_URL])
                if (!broker.transportConnectorURIs.find {it == CONNECTOR_URL}) broker.addConnector(CONNECTOR_URL)
            }
            if (!broker.isStarted()) broker.start()
            connectionFactory = (connectionFactory) ?: new ActiveMQConnectionFactory(CONNECTOR_URL)
            return connectionFactory;
        } catch (NoClassDefFoundError e) {
            throw new ClassNotFoundException("Cannot find ActiveMQ library, please put ActiveMQ jar in the classpath. e: ${e.message}");
        }
    }

}