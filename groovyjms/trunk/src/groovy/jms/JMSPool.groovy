package groovy.jms

import groovy.jms.pool.JMSThread
import groovy.jms.provider.ActiveMQPooledJMSProvider
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.Session
import org.apache.log4j.Logger
import javax.jms.MessageListener

/**
 * JMSPool extends JMS and provided configurable JMS Pooling. Unlike the "raw" JMS, you don't need to provide a connection
 * or session to it. It aims at providing something similar to Spring DefaultMessageListenerContainer 
 *
 * "A Session object is a single-threaded context for producing and consuming messages", in order to enjoy better
 * throughput in both incoming and outgoing messaging, you are recommended to use the JMSPool over JMS. JMSPool could
 * be configured to establish multiple session in mutiple thread to consume or produce messages.
 *
 * The JMSPool utilize ActiveMQ SessionPool for sending message.
 *
 * Read the following before using JMSPool
 * 1. for message consumption, order of messages are not guaranteed after using the pool
 * 2. there is no support for Transaction and there is no recovery mechanism (yet), use Spring JMS or Jencks if you need those features
 * 3. JMSPool is implemented as one thread per connection-session
 */
class JMSPool extends ThreadPoolExecutor {
    static Logger logger = Logger.getLogger(JMSPool.class.name)
    private static final defaultCorePoolSize = 20, defaultMaximumPoolSize = 20, defaultKeepAliveTime = 1000, defaultUnit = TimeUnit.MILLISECONDS
    def connectionFactory, config;

    JMSPool() {this(getDefaultConnectionFactory(), null, null)}

    JMSPool(Map cfg) {this(getDefaultConnectionFactory(), cfg, null)}

    JMSPool(ConnectionFactory f) {this(f, null, null)}

    JMSPool(Map cfg, Closure exec) {this(getDefaultConnectionFactory(), cfg, exec)}

    JMSPool(ConnectionFactory f, Closure exec) {this(f, null, exec)}

    JMSPool(ConnectionFactory f, Map cfg) {this(f, cfg, null)}

    JMSPool(ConnectionFactory f, Map cfg, Closure exec) {
        super(cfg?.'corePoolSize' ?: defaultCorePoolSize, cfg?.'maximumPoolSize' ?: defaultMaximumPoolSize,
                    cfg?.'keepAliveTime' ?: defaultKeepAliveTime, cfg?.'unit' ?: defaultUnit, new LinkedBlockingQueue(),
                getJMSThreadFactory())
        connectionFactory = f; config = cfg;
        org.apache.log4j.MDC.put("tid", Thread.currentThread().getId())
        if (logger.isTraceEnabled()) logger.trace("constructed JMSPool. this: ${this}")
    }


    synchronized static ConnectionFactory getDefaultConnectionFactory(Map cfg = null) {
        return new ActiveMQPooledJMSProvider(cfg).getConnectionFactory()
    }

    void shutdown() {
        super.shutdown();
    }

    List<Runnable> shutdownNow() {
        return super.shutdownNow();
    }

    void finalize() {
        super.finalize();
    }


    static final getJMSThreadFactory() {
        return {Runnable r -> return new JMSThread(r)} as ThreadFactory
    }

    protected void beforeExecute(Thread t, Runnable r) {
        if (logger.isTraceEnabled()) logger.trace("beforeExecute() - tread: $r, runnable: $t")
        Connection connection = connectionFactory.createConnection().with {it.clientID = JMS.getDefaultClientID() + ":" + Thread.currentThread().id; it};
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        JMSThread.jms.set(new JMS(connection, session, false, null));
        super.beforeExecute(t, r);
    }

    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
//        JMSThread.jms.set(null);  // thread will be cleaned in the interrpution event
        if (logger.isTraceEnabled()) logger.trace("afterExecute() - runnable: $r, throwable: $t")
    }

    def jobs = [];

    def onMessage(Map cfg = null, final target) {
        if (isShutdown()) throw new IllegalStateException("JMSPool has been shutdown already")
        if (logger.isTraceEnabled()) logger.trace("onMessage() - submitted job, jobs.size(): ${jobs.size()}, cfg: $cfg, target: $target (${target?.getClass()}")
        jobs << submit({
            org.apache.log4j.MDC.put("tid", Thread.currentThread().getId())
            if (logger.isTraceEnabled()) logger.trace("onMessage() - executing submitted job - threadlocal jms: ${JMSThread.jms.get()}")
            JMSThread.jms.get().onMessage(cfg, (target instanceof MessageListener)?target:target as MessageListener);
            while (true) {}
        } as Runnable)

    }

    def send(Map spec) {
        if (!spec.containsKey('threads')) {
            new JMS(connectionFactory).send(spec)
        }
    }

}