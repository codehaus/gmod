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

    JMS(connArg = null, sessArg = null, Closure c = null) {
        if (connArg && !(connArg instanceof ConnectionFactory || connArg instanceof Connection))
            throw new IllegalArgumentException("input arguments are not valid. check docs for correct usage")

        try {

            if (!connArg) {
                synchronized (SYSTEM_PROP_JMSPROVIDER) {
                    String className = System.getProperty(SYSTEM_PROP_JMSPROVIDER) ?: ActiveMQJMSProvider.class.name
                    provider = provider ?: Class.forName(className).newInstance();
                }
                factory = provider.connectionFactory;
                connection = JMSCoreCategory.establishConnection(factory);
            } else {
                if (connArg instanceof ConnectionFactory) {
                    factory = connArg;
                    connection = JMSCoreCategory.establishConnection(factory);
                } else if (connArg instanceof Connection) {
                    this.connection = connArg;
                    JMSCoreCategory.connection.set(connection)
                }
            }

            if (sessArg) { JMSCoreCategory.session.set(sessArg); session = sessArg }
            else {
                session = JMSCoreCategory.establishSession(connection);
            }
            if (c) {
                use(JMSCategory) {
                    //todo add try catch and close connection
                    c()
                    //connection.close();
                }
            }
        } catch (ClassNotFoundException cnfe) {
            System.err.println("cannot find the JMS Provider class: \"${System.getProperty(SYSTEM_PROP_JMSPROVIDER)}\", please ensure it is in the classpath")
        }
    }

    static void jms(Closure c) {
        new JMS(null, null, c)
    }

    void eachMessage(String queueName, Map cfg = null, Closure c) {
        use(JMSCoreCategory) {
            session.queue(queueName).receiveAll(cfg).each {m -> c(m)}
            cleanupThreadLocalVariables()
        }
    }

    def firstMessage(String queueName, Map cfg = null, Closure c) {
        use(JMSCoreCategory) {
            c(session.queue(queueName).receive(cfg))
            cleanupThreadLocalVariables()
        }

    }

    def receive(Map params) {
        // from: queueName
        // within: Integer as ms 
    }

    def send(Map params) {
        //validate
        if (!(params.containsKey('toQueue') || params.containsKey('toTopic'))) throw new IllegalArgumentException("either toQueue or toTopic must present")
        if (!params.containsKey('message')) throw new IllegalArgumentException("send message must have a \"message\"")
        //dest: toQueue, toTopic ; handle String or List<String>
        //replyTo: queueName or [destName: type];
        use(JMSCoreCategory) {
            def toQueue = params.'toQueue', toTopic = params.'toTopics'
            if (toQueue) {
                if (toQueue instanceof Collection) {
                    toQueue.each {q -> session.queue(q).send(params.'message')}
                } else {
                    session.queue(toQueue).send(params.'message')
                }
            }

            if (toTopic) {
                if (toTopic instanceof Collection) {
                    toTopic.each {t -> session.topic(t).send(params.'message')}
                } else {
                    session.topic(toTopic).send(params.'message')
                }
            }
            cleanupThreadLocalVariables()
        }
    }


    String toString() { return "JMS { session: $session, connection: $connection, factory: $factory, provider: $provider}"}

}