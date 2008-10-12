package groovy.jms

import javax.jms.*
import org.apache.log4j.Logger
import java.lang.reflect.Method
import groovy.jms.provider.JMSProvider
import groovy.jms.provider.ActiveMQJMSProvider

class JMS {
    static Logger logger = Logger.getLogger(JMS.class)
    static final clientIdPrefix;
    static {
        try {clientIdPrefix = InetAddress.getLocalHost()?.hostName} catch (e) { logger.error("fail to get local hostname on JMS static init")}
    }
    private static JMSProvider provider; //no need to recreate
    private ConnectionFactory factory;
    private Connection connection;//TODO add @delegate after upgraded to 1.6beta2
    private Session session; //TODO add @delegate after upgraded to 1.6beta2

    JMS(connArg = null, sessArg = null, Closure c) {
        if (connArg && (connArg instanceof ConnectionFactory || connArg instanceof Connection))
            throw new IllegalArgumentException("input arguments are not valid. check docs for correct usage")

        use(JMSCategory) {
            if (!connArg) {
                synchronized (ActiveMQJMSProvider) { provider = provider ?: new ActiveMQJMSProvider(); }
                factory = provider.connectionFactory;
                connection = factory.connect();
                session = factory.session();
            }

            //todo add try catch and close connection
            c()
        }
    }

    String toString() { return "JMS { session: $session, connection: $connection, factory: $factory, provider: $provider}"}

}