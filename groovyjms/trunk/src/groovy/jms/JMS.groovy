package groovy.jms

import groovy.jms.provider.ActiveMQJMSProvider
import groovy.jms.provider.JMSProvider
import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.Session
import org.apache.log4j.Logger
import javax.jms.MessageListener
import javax.jms.Topic

class JMS {
    static Logger logger = Logger.getLogger(JMS.class.name)
    static final clientIdPrefix;
    static {
        try {clientIdPrefix = InetAddress.getLocalHost()?.hostName} catch (e) { logger.error("fail to get local hostname on JMS static init")}
    }
    public static String SYSTEM_PROP_JMSPROVIDER = "groovy.jms.provider"
    public static String DEFAULT_JMSPROVIDER = ActiveMQJMSProvider.class.name
    static JMSProvider provider; //no need to recreate
    private ConnectionFactory factory;
    private Connection connection;//TODO add @delegate after upgraded to 1.6beta2
    private Session session; //TODO add @delegate after upgraded to 1.6beta2
    boolean autoClose = true; //TODO implements a nested handling mechanism and change the autoClose to a ThreadScope tx strategy

    JMS(connArg = null, Closure c) {
        this(connArg, null, c);
    }

    JMS(connArg = null, sessArg = null, Closure c = null) {
        if (connArg && !(connArg instanceof ConnectionFactory || connArg instanceof Connection))
            throw new IllegalArgumentException("input arguments are not valid. check docs for correct usage")

        try {

            if (!connArg) {
                synchronized (SYSTEM_PROP_JMSPROVIDER) {
                    String className = System.getProperty(SYSTEM_PROP_JMSPROVIDER) ?: DEFAULT_JMSPROVIDER
                    provider = provider ?: Class.forName(className).newInstance();
                }
                factory = provider.connectionFactory;
                connection = JMSCoreCategory.establishConnection(factory, true);
            } else {
                if (connArg instanceof ConnectionFactory) {
                    factory = connArg;
                    connection = JMSCoreCategory.establishConnection(factory, true);
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
                    if (!session) throw new IllegalStateException("session was not available")
                    c()
                }
                if (autoClose) this.close()
            }
        } catch (ClassNotFoundException cnfe) {
            System.err.println("cannot find the JMS Provider class: \"${System.getProperty(SYSTEM_PROP_JMSPROVIDER)}\", please ensure it is in the classpath")
        }
    }

    static void jms(connArgs = null, sessionArg = null, Closure c) {
        new JMS(connArgs, sessionArg, c)
    }

    void eachMessage(String queueName, Map cfg = null, Closure c) {
        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")
            session.queue(queueName).receiveAll(cfg).each {m -> c(m)}
            if (autoClose) this.close()
        }
    }

    def firstMessage(String queueName, Map cfg = null, Closure c) {
        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")
            c(session.queue(queueName).receive(cfg))
            if (autoClose) this.close()
        }
    }

    def onMessage(String dest, Map cfg = null, Object target) {
        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")
            if (target instanceof Closure)
                session.topic(dest).subscribe(target as MessageListener)
            else if (target instanceof MessageListener)
                session.topic(dest).subscribe(target)
            else {
                throw new UnsupportedOperationException("to hande this case")
            }
            if (autoClose) this.close()
        }
    }

    def stopMessage(listener) {
        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")
            println    listener
            println listener.subscriptionName
            session.unsubscribe(listener.subscriptionName)
            if (autoClose) this.close()
        }
    }

    def receive(Map params, Closure with = null) {
        if (!(params.containsKey('fromQueue') || params.containsKey('fromTopic'))) throw new IllegalArgumentException("either toQueue or toTopic must present")
        if (!with && !params.containsKey('with')) throw new IllegalArgumentException("receive message must provide a \"with\"")

        def fromQueue = params.'fromQueue', fromTopic = params.'fromTopic'
        with = (with) ?: params.'with'

        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")

            if (fromQueue) {
                int timeout = (params.'within') ? Integer.valueOf(params.'within') : 0
                if (fromQueue instanceof Collection) {
                    with(fromQueue.collect {q -> session.queue(q).receiveAll(timeout)})
                } else {
                    with(session.queue(fromQueue).receiveAll(timeout))
                }
            }

            if (fromTopic) {
                if (fromTopic instanceof Collection) {
                    fromTopic.each {t -> session.topic(t).subscribe(with)}
                } else {
                    session.topic(fromTopic).subscribe(with)
                }
            }
            if (autoClose) cleanupThreadLocalVariables()
        }
    }

    def send(Map params) {
        if (!(params.containsKey('toQueue') || params.containsKey('toTopic'))) throw new IllegalArgumentException("either toQueue or toTopic must present")
        if (!params.containsKey('message')) throw new IllegalArgumentException("send message must have a \"message\"")
        //dest: toQueue, toTopic ; handle String or List<String>
        //replyTo: queueName or [destName: type];
        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")
            def toQueue = params.'toQueue', toTopic = params.'toTopic'
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
            if (autoClose) cleanupThreadLocalVariables()
        }
    }

    def static close() {
        JMSCoreCategory.cleanupThreadLocalVariables(null, true)
    }


    String toString() { return "JMS { session: $session, connection: $connection, factory: $factory, provider: $provider}"}

}