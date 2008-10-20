package groovy.jms

import groovy.jms.provider.ActiveMQJMSProvider
import groovy.jms.provider.JMSProvider
import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.Session
import org.apache.log4j.Logger
import javax.jms.MessageListener
import javax.jms.Topic
import javax.jms.Queue
import javax.jms.MessageConsumer
import javax.jms.TopicSubscriber
import javax.jms.QueueReceiver

/**
 * Connection and session is either provided to constructor, or be created in the construction. In either case, connection
 * will not be re-created.
 *
 * when connection and session are provided, autoClose is set to false, otherwise, it's true
 */
class JMS extends AbstractJMS{
    static Logger logger = Logger.getLogger(JMS.class.name)
    static final hostName;
    static {
        try {hostName = InetAddress.getLocalHost()?.hostName} catch (e) { logger.error("fail to get local hostname on JMS static init")}
    }
    public static String SYSTEM_PROP_JMSPROVIDER = "groovy.jms.provider"
    public static final int DEFAULT_SESSION_ACK = Session.AUTO_ACKNOWLEDGE; //TODO consider to add support for other ack mode
    boolean autoClose = true;
    boolean initialized = false;
    private Connection connection;//TODO add @delegate after upgraded to 1.6beta2
    private Session session; //TODO add @delegate after upgraded to 1.6beta2

    JMS() {this(getDefaultConnectionFactory(), null)}

    JMS(Closure exec) {this(getDefaultConnectionFactory(), exec)}

    JMS(ConnectionFactory f) {this(f, null)}

    JMS(Connection c) {this(c, c.createSession(false, DEFAULT_SESSION_ACK), false, null)}

    JMS(ConnectionFactory f, Closure exec) { this(getConnectionWithDefaultClientID(f), exec) }

    JMS(Connection c, Closure exec) {this(c, c.createSession(false, DEFAULT_SESSION_ACK), false, exec)}

    JMS(Connection c, Session s, boolean ac, Closure exec) {
        if (!c || !s) throw new IllegalArgumentException("ConnectionFactory and Execution closure must not be null")
        if (!c.clientID) c.clientID = getDefaultClientID(); org.apache.log4j.MDC.put("tid", Thread.currentThread().getId());
        connection = c; session = s; autoClose = ac; if (exec) run(exec);
    }

    synchronized static ConnectionFactory getDefaultConnectionFactory() {
        String className = System.getProperty(SYSTEM_PROP_JMSPROVIDER)
        JMSProvider provider = className ? Class.forName(className)?.newInstance() : new ActiveMQJMSProvider();
        return provider.connectionFactory;
    }

    static String getDefaultClientID() { return "$hostName:${System.currentTimeMillis()}" }

    static Connection getConnectionWithDefaultClientID(ConnectionFactory f) {
        Connection c = f.createConnection(); c.setClientID(getDefaultClientID())
        return c;
    }

    static void jms(Closure exec) { jms(getDefaultConnectionFactory(), exec)}

    static void jms(ConnectionFactory f, Closure exec) {
        if (!exec || !f) throw new IllegalArgumentException("ConnectionFactory and Execution closure must not be null")
        def jms = new JMS(f, exec)
    }

    static void jms(Connection c, Session s = null, Closure exec) {
        if (!c || !exec) throw new IllegalArgumentException("Connection and Execution closure must not be null")
        def jms = (s) ? new JMS(c, s, exec) : new JMS(c, exec)
    }

    boolean started = false;

    synchronized void start() {
        if (logger.isTraceEnabled()) logger.trace("start()")
        connection.start()
        JMSCoreCategory.connection.set(connection)
        JMSCoreCategory.session.set(session)
        started = true;
    }

    void connect() { // connection.start()
        connection.start()
    }

    void run(Closure c) {
        if (!started) start();
        use(JMSCategory) {
            //delegate.set(); //set JMS to Category ThreadLocal
            c(); //TODO consider to set sth to the closure
        }
        if (autoClose && !closed) close();
    }

    boolean closed = false;

    void close() {
        if (logger.isTraceEnabled()) logger.trace("stop()")
        JMSCoreCategory.cleanupThreadLocalVariables(null, true)
        //connection.close()
        closed = true;
    }

    void eachMessage(String queueName, Map cfg = null, Closure c) {
        if (!started) start();
        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")
            session.queue(queueName).receiveAll(cfg?.'within' ?: null).each {m -> c(m)}
        }
        if (autoClose && !closed) close();
    }

    def firstMessage(String queueName, Map cfg = null, Closure c) {
        if (!started) start();
        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")
            c(session.queue(queueName).receive(cfg?.'within' ?: null))
        }
        if (autoClose && !closed) close();
    }

    Map messageConsumers = Collections.synchronizedMap(new WeakHashMap());

    def onMessage(Map cfg, Object target) {
        if (!started) start();
        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")
            if (!cfg || !(cfg.containsKey('topic') || cfg.containsKey('queue'))) throw new IllegalArgumentException("first argument of onMessage must have a Map with 'queue' or 'topic' key")

            MessageListener listener = target instanceof MessageListener ? target : target as MessageListener
            if (cfg.containsKey('topic')) {
                def topicDest = cfg.get('topic')
                Topic topic
                if (topicDest instanceof String) {
                    topic = session.topic(topicDest);
                } else if (topicDest instanceof Topic) {
                    topic = topicDest;                  //TODO: refactor this to one line
                } else if (topicDest instanceof Collection) {
                    throw new UnsupportedOperationException("collection of topics is not implemented yet")
                } else {
                    throw new IllegalArgumentException("topic value is not supported. class: ${topicDest.getClass()}")
                }
                def subscriber = topic.subscribe(listener)
                messageConsumers.put(subscriber, topic);  //TODO no need to put value
            }

            if (cfg.containsKey('queue')) {
                def queueDest = cfg.get('queue')
                Queue queue;
                if (queueDest instanceof String) {
                    queue = session.queue(queueDest);
                } else {
                    throw new IllegalArgumentException("unimplemented")
                }
                QueueReceiver receiver = queue.listen(listener)
                messageConsumers.put(receiver, queue);//TODO no need to put value
            }
            if (logger.isTraceEnabled()) logger.trace("onMessage() - cfg: $cfg, messageConsumers.size: ${messageConsumers?.size()}, messageConsumers: $messageConsumers")
        }
        if (autoClose && !closed) close();
    }

    def stopMessage(Map dest) {
        if (!started) throw new IllegalStateException("jms is not started yet")
        if (!dest || !(dest.containsKey('topic') || dest.containsKey('queue'))) throw new IllegalArgumentException("first argument of onMessage must have a Map with 'queue' or 'topic' key")

        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")
            if (dest.containsKey('topic')) {
                def topicDest = dest.get('topic')
                if (topicDest instanceof String) {
                    def consumers = messageConsumers.keySet().findAll {MessageConsumer c -> c instanceof TopicSubscriber && c.topic.topicName == topicDest}
                    consumers.each {MessageConsumer c ->
                        Topic topic = c.topic;
                        c.setMessageListener(null)
                        c.close()
                        topic.unsubscribe();
                        if (logger.isTraceEnabled()) logger.trace("stopMessage() - dest: $dest, consumer: $c")
                        messageConsumers.remove(c)
                    }
                } else if (topicDest instanceof Topic) {
                    topic = topicDest;                  //TODO: refactor this to one line
                    throw new UnsupportedOperationException("not implemented yet")
                } else if (topicDest instanceof Collection) {
                    throw new UnsupportedOperationException("collection of topics is not implemented yet")
                } else {
                    throw new IllegalArgumentException("topic value is not supported. class: ${topicDest.getClass()}")
                }
            }
            if (dest.containsKey('queue')) {
                def queueDest = dest.get('queue')
                if (queueDest instanceof String) {
                    def consumers = messageConsumers.keySet().findAll {MessageConsumer c -> c instanceof QueueReceiver && c.queue.queueName == queueDest}
                    consumers.each {MessageConsumer c ->
                        Queue queue = c.queue;
                        c.setMessageListener(null)
                        c.close()
                        if (logger.isTraceEnabled()) logger.trace("stopMessage() - dest: $dest, consumer: $c")
                        messageConsumers.remove(c)
                    }
                } else if (queueDest instanceof Queue) {
                    queue = queueDest;                  //TODO: refactor this to one line
                    throw new UnsupportedOperationException("not implemented yet")
                } else if (queueDest instanceof Collection) {
                    throw new UnsupportedOperationException("collection of topics is not implemented yet")
                } else {
                    throw new IllegalArgumentException("topic value is not supported. class: ${topicDest.getClass()}")
                }
            }

        }
        if (autoClose && !closed) close();
    }

    /**
     * jms.receive(){ messages-> }* jms.receive(with:{messages->})
     * def result = jms.receive()
     *
     * a topic doesn't support synchronous retrieval, any specified topic will be subscribed instead.
     *
     * @return if 'with' is specified, there will be no return value; otherwise, return an arraylist of messages
     */
    def receive(Map params, Closure with = null) {
        if (!started) start();
        if (!(params.containsKey('fromQueue') || params.containsKey('fromTopic'))) throw new IllegalArgumentException("either toQueue or toTopic must present")
        //if (!with && !params.containsKey('with')) throw new IllegalArgumentException("receive message must provide a \"with\"")

        def result = [];
        def fromQueue = params.'fromQueue', fromTopic = params.'fromTopic'
        with = (with) ?: params.'with'

        use(JMSCoreCategory) {
            if (!session) throw new IllegalStateException("session was not available")

            if (fromQueue) {
                int timeout = (params.'within') ? Integer.valueOf(params.'within') : 0
                if (fromQueue instanceof Collection) {
                    result += fromQueue.collect {q -> session.queue(q).receiveAll(timeout)}
                } else {
                    result += session.queue(fromQueue).receiveAll(timeout)
                }
            }

            if (fromTopic) {
                if (fromTopic instanceof Collection) {
                    fromTopic.each {t -> session.topic(t).subscribe(with)}
                } else {
                    session.topic(fromTopic).subscribe(with)
                }
            }
        }
        if (autoClose && !closed) close();
        if (with) { with(result) } else { return result; }
        return result;
    }

    def send(Map params) {
        if (!started) start();
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
        }
        if (autoClose && !closed) close();
    }


    String toString() { return "JMS { ${super.toString()}, session: $session, connection: $connection, autoClose: $autoClose"}

}