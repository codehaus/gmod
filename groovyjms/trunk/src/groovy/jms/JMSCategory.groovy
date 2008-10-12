package groovy.jms

import javax.jms.*
import org.apache.log4j.Logger
import java.lang.reflect.Method

/**
 * 1. JMS ConnectionFactory, Connection, and Session are top level objects that essentially can be used interchangable.
 *  - use(JMS){ jms.connect().queue()...} <br>
 *  - use(JMS){ jms.session().topic()...} <br>
 *  - use(JMS){ jms.queue() or jms.topic() } <br>
 *
 *  In the first time any of the above method is called, a connection and session are established in the thread context.
 * It supports only one connection factory, one connection and one session at a time. The use of connect() or session()
 * is for obtaining the session or connection for direct JMS usage in case where optimization is needed. The JMS category
 * always re-use the connection/session until close() is called.
 *
 *  after usage, the close() must be classed to clean up ThreadLocal instance. e.g. jms.close(), connection.close(), session.close().
 * Notice that closing a session automatically close the connection in JMS Category's context
 *
 * 2. a Queue or Topic must be obtained to send message
 *
 * 3. send(), receive(), receiveAll(), subscribe() are the actual methods to interact with JMS provider
 *
 *
 * TODO:
 * keep commit for connectionfactory, connection, rename session to close.
 * make return value consistent like
 *  - for createXXX, return XXX
 *  - otherwise, use the caller type as return type
 * remove those List<Message>.commit(), use session.close instead.
 * - remove the dependency to connectionFactory, user may supply connection and connectionFactory may be null
 */
class JMSCategory {
    static Logger logger = Logger.getLogger(JMSCategory.class)
    static final ThreadLocal<Connection> connection = new ThreadLocal<Connection>();
    static final ThreadLocal<Session> session = new ThreadLocal<Session>();
    static final clientIdPrefix;
    static {
        try {clientIdPrefix = InetAddress.getLocalHost()?.hostName} catch (e) { logger.error("fail to get local hostname on JMS static init")}
    }

    /*static Topic subscribe(String dest) {
        return session.get().topic(dest);
    }

    static Topic with(Topic topic, Closure listener) {
        with(topic, listener as MessageListener);
    }

    static Topic with(Topic topic, MessageListener listener) {
        topic.subscribe(listener)
    }
*/
    static Topic subscribe(String dest, Closure listener) {
        return session.get().topic(dest).with(listener);
    }

    static Topic subscribe(String dest, MessageListener listener) {
        return session.get().topic(dest).with(listener);
    }

    static void publishTo(String textMessage, String dest, Map cfg = null) {
        if (!session.get()) throw new IllegalStateException("ThreadLocal session is not available")
        Topic topic = session.get().topic(dest);
        sendMessage(topic, textMessage, cfg);
    }

    static Connection getConnection(subject) { return connection.get() }

    static Session getSession(subject) { return session.get() }


    /** *****************************************************************************************************************
     * TOP LEVEL PRIVATE METHOD for establish Connection and Session
     ***************************************************************************************************************** */

    //TODO consider to set a timeout to handle uncommitted JMS thread
    //TODO consider to add a parameter to enable/disable transaction
    //Remarks: it's hardcoded to reuse session per thread
    static Connection establishConnection(ConnectionFactory factory, String clientId = null) {
        if (!factory) throw new IllegalStateException("factory must not be null")
        if (JMSCategory.connection.get()) return JMSCategory.connection.get();
        org.apache.log4j.MDC.put("tid", Thread.currentThread().getId());
        Connection conn = factory.createConnection();
        conn.setClientID(clientId ?: clientIdPrefix + ':' + System.currentTimeMillis());
        conn.setExceptionListener({JMSException e -> logger.error("JMS Exception", e)} as ExceptionListener);
        JMSCategory.connection.set(conn);
        conn.start();
        return conn;
    }

    static Session establishSession(Connection conn) {
        if (JMSCategory.session.get()) return JMSCategory.session.get();
        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        JMSCategory.session.set(session);
        return session;
    }


    /** *****************************************************************************************************************
     * TOP LEVEL PUBLIC METHOD for call by users
     ***************************************************************************************************************** */
    //this method doesn't create session
    static Connection connect(ConnectionFactory factory, String clientId = null) {
        if (JMSCategory.connection.get()) return JMSCategory.connection.get();
        Connection connection = establishConnection(factory, clientId)
        if (logger.isTraceEnabled()) logger.trace("connect() - return connection: $connection, clientId: $clientId")
        return connection;
    }

    static Session session(ConnectionFactory factory, String clientId = null) {
        if (JMSCategory.session.get()) return JMSCategory.session.get();
        Connection conn = connect(factory, clientId)
        Session session = establishSession(conn);
        if (logger.isTraceEnabled()) logger.trace("session() - return session: $session")
        return session;
    }

    static Session session(Connection connection) {
        if (JMSCategory.session.get()) return JMSCategory.session.get();
        Session session = establishSession(connection);
        if (logger.isTraceEnabled()) logger.trace("session() - return session: $session")
        return session;
    }

    static start(ConnectionFactory target) {
        if (!JMSCategory.connection.get()) throw new IllegalStateException("not existing connection to start")
        start(JMSCategory.connection.get());
    }

    static final Method connectionStart = Connection.methods.find { it.name == 'start'};

    static start(Connection target) { connectionStart.invoke(JMSCategory.connection.get(), null); }

    static close(ConnectionFactory target) { cleanupThreadLocalVariables(target, true) }

    static close(Session target) { cleanupThreadLocalVariables(target, true)}

    static close(Connection target) { cleanupThreadLocalVariables(target, true) }

    static final Method connectionClose = Connection.methods.find { it.name == 'close'};

    static cleanupThreadLocalVariables(target, boolean close = false) {
        if (logger.isTraceEnabled()) logger.trace("cleanupThreadLocalVariables() - class: ${target.getClass()}")
        if (close) JMSCategory.connection.get().start();
        JMSCategory.session.set(null);
        if (close) connectionClose.invoke(JMSCategory.connection.get(), null)
        JMSCategory.connection.set(null);
    }

    /** *****************************************************************************************************************
     * DESTINATION CREATION METHODS
     ***************************************************************************************************************** */
    // create topic
    static Topic topic(ConnectionFactory factory, String dest) {
        Connection connection = connect(factory);
        Session session = session(connection);
        Topic topic = session.createTopic(dest);
        if (logger.isTraceEnabled()) logger.trace("topic() - return topic: $topic")
        return topic;
    }

    static Topic topic(Connection connection, String dest) {
        Session session = session(connection);
        Topic topic = session.createTopic(dest);
        if (logger.isTraceEnabled()) logger.trace("topic() - return topic: $topic")
        return topic;
    }

    static Topic topic(Session session, String dest) {
        Topic topic = session.createTopic(dest);
        if (logger.isTraceEnabled()) logger.trace("topic() - return topic: $topic")
        return topic;
    }

    static Queue queue(ConnectionFactory factory, String dest) {
        Connection connection = connect(factory);
        Session session = session(connection);
        Queue queue = session.createQueue(dest);
        if (logger.isTraceEnabled()) logger.trace("topic() - return queue: $queue")
        return queue;
    }

    static Queue queue(Connection connection, String dest) {
        Session session = session(connection);
        Queue queue = session.createQueue(dest);
        if (logger.isTraceEnabled()) logger.trace("topic() - return queue: $queue")
        return queue;
    }

    static Queue queue(Session session, String dest) {
        Queue queue = session.createQueue(dest);
        if (logger.isTraceEnabled()) logger.trace("topic() - return queue: $queue")
        return queue;
    }

    /** ****************************************************************************************************************
     * Messenging methods
     ***************************************************************************************************************** */
    static Topic send(Topic dest, message, Map msgCfg = null) {
        return sendMessage(dest, message, msgCfg);
    }

    static Queue send(Queue dest, message, Map msgCfg = null) {
        return sendMessage(dest, message, msgCfg);
    }

    static Destination reply(Message incoming, message, Map msgCfg = null) {
        if (!incoming.JMSReplyTo) throw new RuntimeException("the incoming message does not contain a reply address")
        if (incoming.JMSCorrelationID)
            msgCfg = (msgCfg) ? msgCfg.with {it.put('JMSCorrelationID', incoming.JMSCorrelationID); it} : ['JMSCorrelationID': incoming.JMSCorrelationID]
        return send(incoming.JMSReplyTo, message, msgCfg)
    }

    private static Object sendMessage(Destination dest, message, Map cfg = null) {
        if (!JMSCategory.connection.get()) throw new IllegalStateException("No connection. Call connect() or session() first.")
        if (!JMSCategory.session.get()) JMSCategory.session.set(session(JMSCategory.connection.get()))
        MessageProducer producer = session.get().createProducer(dest);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        Message jmsMessage;
        if (message instanceof Message) {
            jmsMessage = message;
        } else if (message instanceof String) {
            jmsMessage = session.get().createTextMessage((String) message);
        } else {
            throw new UnsupportedOperationException("only text message is implemented")
        }
        cfg?.each {k, v -> jmsMessage[k] = v}
        producer.send(jmsMessage);
        if (logger.isTraceEnabled()) logger.trace("send() - dest: $dest, message: $message, cfg: $cfg")
        return dest;
    }

    // subscribe to a topic
    static Topic subscribe(Topic topic, MessageListener listener, String subscriptionName = null) {
        if (!JMSCategory.connection.get()) throw new IllegalStateException("No connection. Call connect() or session() first.")
        if (!JMSCategory.session.get()) JMSCategory.session.set(session(JMSCategory.connection.get()))
        TopicSubscriber subscriber = session.get().createDurableSubscriber(topic, subscriptionName ?: JMSCategory.connection.get().clientID)
        subscriber.setMessageListener(listener);
        if (logger.isTraceEnabled()) logger.trace("subscribe() - topic: $topic, listener: $listener")
        return topic;
    }

    static Message receive(Queue dest, Integer waitTime = null) {
        if (!JMSCategory.connection.get()) throw new IllegalStateException("No connection. Call connect() or session() first.")
        if (!JMSCategory.session.get()) JMSCategory.session.set(session(JMSCategory.connection.get()))
        MessageConsumer consumer = session.get().createConsumer(dest);
        Message message = (waitTime) ? consumer.receive(waitTime) : consumer.receiveNoWait();
        consumer.close();
        if (logger.isTraceEnabled() && message) logger.trace("get() - from $dest - return $message");
        return message;
    }

    static List<Message> receiveAll(Queue dest, Integer waitTime = null) {
        if (!JMSCategory.connection.get()) throw new IllegalStateException("No connection. Call JMS.connect() first.")
        if (!JMSCategory.session.get()) JMSCategory.session.set(session(JMSCategory.connection.get()))
        MessageConsumer consumer = session.get().createConsumer(dest);
        List<Message> messages = [];
        boolean first = true;
        Message message;
        while (first || message) {
            message = (waitTime) ? consumer.receive(waitTime) : consumer.receiveNoWait();
            if (message) { messages << message;}
            first = false;
        }
        consumer.close();
        if (logger.isTraceEnabled() && messages?.size()) logger.trace("getAll() - from $dest - return size(): ${messages.size()}, messages: $messages");
        return messages;
    }

}