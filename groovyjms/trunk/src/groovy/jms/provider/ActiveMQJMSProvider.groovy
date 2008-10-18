package groovy.jms.provider

import javax.jms.ConnectionFactory
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.broker.BrokerRegistry
import org.apache.activemq.broker.BrokerService
import org.apache.activemq.pool.PooledConnectionFactory
import org.apache.log4j.Logger;

class ActiveMQJMSProvider implements JMSProvider {
    static Logger logger = Logger.getLogger(ActiveMQJMSProvider.class.name)
    public static final String BROKER_NAME = "groovy.jms.provider.ActiveMQJMSProvider.broker"
    public static final String CONNECTOR_URL = "vm://localhost"//?broker.persistent=false
    static BrokerService broker;
    ConnectionFactory factory;

    synchronized static protected startBroker() {
        if (!broker) {
            BrokerRegistry registry = BrokerRegistry.getInstance();
            broker = registry.findFirst() ?: new BrokerService(brokerName: BROKER_NAME, useJmx: false,
                    persistent: false, useShutdownHook: true, transportConnectorURIs: [CONNECTOR_URL])
            if (!broker.transportConnectorURIs.find {it == CONNECTOR_URL}) broker.addConnector(CONNECTOR_URL)
            if (logger.isInfoEnabled()) logger.info("startBroker() - create broker - broker: $broker")
        }
        if (!broker.isStarted()) broker.start()
    }

    public ConnectionFactory getConnectionFactory() {
        try {
            if (logger.isInfoEnabled()) logger.info("getConnectionFactory() - broker: $broker, broker.isStarted? ${broker?.isStarted()}")
            if (!broker?.isStarted()) startBroker();
            factory = factory ?: new ActiveMQConnectionFactory(CONNECTOR_URL);
            return factory;
        } catch (NoClassDefFoundError e) {
            throw new ClassNotFoundException("Cannot find ActiveMQ library, please put ActiveMQ jar in the classpath. e: ${e.message}");
        }
    }

}