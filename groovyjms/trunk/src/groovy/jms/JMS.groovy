package groovy.jms

import javax.jms.*
import org.apache.log4j.Logger
import java.lang.reflect.Method
import groovy.jms.provider.JMSProvider
import groovy.jms.provider.ActiveMQJMSProvider

class JMS {
    static Logger logger = Logger.getLogger(JMS.class.name)
    static final clientIdPrefix;
    static {
        try {clientIdPrefix = InetAddress.getLocalHost()?.hostName} catch (e) { logger.error("fail to get local hostname on JMS static init")}
    }
    public static String SYSTEM_PROP_JMSPROVIDER = "groovy.jms.provider"
    private static JMSProvider provider; //no need to recreate
    private ConnectionFactory factory;
    private Connection connection;//TODO add @delegate after upgraded to 1.6beta2
    private Session session; //TODO add @delegate after upgraded to 1.6beta2

    JMS(connArg = null, sessArg = null, Closure c) {
        if (connArg && !(connArg instanceof ConnectionFactory || connArg instanceof Connection))
            throw new IllegalArgumentException("input arguments are not valid. check docs for correct usage")

        try {

            if (!connArg) {
                synchronized (SYSTEM_PROP_JMSPROVIDER) {
                    String className = System.getProperty(SYSTEM_PROP_JMSPROVIDER) ?: ActiveMQJMSProvider.class.name
                    provider = provider ?: Class.forName(className).newInstance();
                }
                factory = provider.connectionFactory;
                connection = JMSCategory.establishConnection(factory);
            } else {
                if (connArg instanceof ConnectionFactory) {
                    factory = connArg;
                    connection = JMSCategory.establishConnection(factory);
                } else if (connArg instanceof Connection) {
                    this.connection = connArg;
                    JMSCategory.connection.set(connection)
                }
            }

            if (sessArg) { JMSCategory.session.set(sessArg); session = sessArg }
            else {
                session = JMSCategory.establishSession(connection);
            }
            use(JMSCategory) {
                //todo add try catch and close connection
                c()
            }
        } catch (ClassNotFoundException cnfe) {
            System.err.println("cannot find the JMS Provider class: \"${System.getProperty(SYSTEM_PROP_JMSPROVIDER)}\", please ensure it is in the classpath")
        }
    }

    String toString() { return "JMS { session: $session, connection: $connection, factory: $factory, provider: $provider}"}

}