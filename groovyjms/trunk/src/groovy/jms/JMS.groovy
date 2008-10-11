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
        if (connArg && connArg instanceof ConnectionFactory) { factory = connArg; connection = factory.createConnection() }
        else if (connArg && connArg instanceof Connection) { connection = connArg;}
        else if (!connArg) {
            synchronized (ActiveMQJMSProvider) { provider = provider ?: new ActiveMQJMSProvider(); }
            factory = provider.connectionFactory; connection = factory.createConnection()
        } else { throw new IllegalArgumentException("input arguments are not valid. check docs for correct usage")}

        session = sessArg ?: session ?: connection.createSession(false, Session.AUTO_ACKNOWLEDGE); //TODO make args configurable

        use(JMSCategory) {c()}
    }

    String toString() { return "JMS { session: $session, connection: $connection, factory: $factory, provider: $provider}"}

}