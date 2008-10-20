package groovy.jms

import groovy.jms.pool.JMSThread
import groovy.jms.pool.JMSThreadPoolExecutor
import groovy.jms.provider.ActiveMQPooledJMSProvider
import java.util.concurrent.*
import javax.jms.ConnectionFactory
import javax.jms.MessageListener
import org.apache.log4j.Logger

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
class JMSPool extends AbstractJMS {
    static Logger logger = Logger.getLogger(JMSPool.class.name)
    private static final defaultCorePoolSize = 10, defaultMaximumPoolSize = 10, defaultKeepAliveTime = 1000, defaultUnit = TimeUnit.MILLISECONDS
    static def connectionFactory, config;
    def ThreadPoolExecutor threadPool;

    JMSPool() {this(getDefaultConnectionFactory(), null, null)}

    JMSPool(Map cfg) {this(getDefaultConnectionFactory(), cfg, null)}

    JMSPool(ConnectionFactory f) {this(f, null, null)}

    JMSPool(Map cfg, Closure exec) {this(getDefaultConnectionFactory(), cfg, exec)}

    JMSPool(ConnectionFactory f, Closure exec) {this(f, null, exec)}

    JMSPool(ConnectionFactory f, Map cfg) {this(f, cfg, null)}

    JMSPool(ConnectionFactory f, Map cfg, Closure exec) {
        threadPool = new JMSThreadPoolExecutor(cfg?.'corePoolSize' ?: defaultCorePoolSize, cfg?.'maximumPoolSize' ?: defaultMaximumPoolSize,
                    cfg?.'keepAliveTime' ?: defaultKeepAliveTime, cfg?.'unit' ?: defaultUnit, new LinkedBlockingQueue(), getJMSThreadFactory());
        connectionFactory = f; config = cfg;
        org.apache.log4j.MDC.put("tid", Thread.currentThread().getId())
        if (logger.isTraceEnabled()) logger.trace("constructed JMSPool. this: ${this}")
    }

    static final ThreadGroup jmsThreads = new ThreadGroup(JMSPool.class.name)

    static final getJMSThreadFactory() { return {Runnable r -> new JMSThread(jmsThreads, r, connectionFactory) } as ThreadFactory }

    synchronized static ConnectionFactory getDefaultConnectionFactory(Map cfg = null) {
        return new ActiveMQPooledJMSProvider(cfg).getConnectionFactory()
    }

    void shutdown() {
        threadPool.shutdown();
    }

    List<Runnable> shutdownNow() {
        return threadPool.shutdownNow();
    }

    def jobs = [];

    def onMessage(Map cfg = null, final target) {
        if (threadPool.isShutdown()) throw new IllegalStateException("JMSPool has been shutdown already")
        if (logger.isTraceEnabled()) logger.trace("onMessage() - submitted job, jobs.size(): ${jobs.size()}, cfg: $cfg, target? ${target != null} (${target?.getClass()}")
        jobs << threadPool.submit({
            if (logger.isTraceEnabled()) logger.trace("onMessage() - executing submitted job - jms? ${JMSThread.jms.get() != null}")
            JMSThread.jms.get().onMessage(cfg, (target instanceof MessageListener) ? target : target as MessageListener);
            while (true) {}
        } as Runnable)

    }

    def send(Map spec) {
        if (threadPool.isShutdown()) throw new IllegalStateException("JMSPool has been shutdown already")
        if (logger.isTraceEnabled()) logger.trace("send() - spec: $spec")

        Future sendJob = threadPool.submit({
            if (logger.isTraceEnabled()) logger.trace("send() - executing submitted job - jms? ${JMSThread.jms.get() != null}")
            if (spec.'delay') sleep(spec.'delay')
            JMSThread.jms.get().send(spec)
            JMSThread.jms.get().connect()
        } as Runnable)
        //TODO review return value
        //TODO after sending one, check the queue to process the next message
    }

    def receive(Map spec, Closure with = null) {     // spec.'timeout'
        if (threadPool.isShutdown()) throw new IllegalStateException("JMSPool has been shutdown already")
        if (logger.isTraceEnabled()) logger.trace("receive() - spec: $spec, with? ${with != null}")
        Future receiveJob = threadPool.submit({
            if (logger.isTraceEnabled()) logger.trace("receive() - executing submitted job - jms? ${JMSThread.jms.get() != null}")
            //TODO break down every destination-selector to a thread
            //TODO handle the 'threads' parameter
            def result = JMSThread.jms.get().receive(spec, with);
            if (logger.isTraceEnabled()) logger.trace("receive() - executed job - result: $result, thread-jms: ${JMSThread.jms.get()}")
            return result;
        } as Callable);
        if (spec.containsKey('timeout')) {
            return receiveJob.get(spec.'timeout', TimeUnit.MILLISECONDS)
        } else {
            return receiveJob.get();
        }
    }

/*
    protected void beforeExecute(Thread t, Runnable r) {
        if (logger.isTraceEnabled()) logger.trace("beforeExecute() - tread: $r, runnable: $t")
        Connection connection = connectionFactory.createConnection().with {it.clientID = JMS.getDefaultClientID() + ":" + Thread.currentThread().id; it};
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        JMSThread.jms.set(new JMS(connection, session, false, null));
        threadPool.beforeExecute(t, r);
    }

    protected void afterExecute(Runnable r, Throwable t) {
        threadPool.afterExecute(r, t);
        JMSThread.jms.get().close();
        JMSThread.jms.set(null);  // thread will be cleaned in the interrpution event
        if (logger.isTraceEnabled()) logger.trace("afterExecute() - runnable: $r, throwable: $t")
    }
*/
}