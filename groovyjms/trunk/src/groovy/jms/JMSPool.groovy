package groovy.jms

import javax.jms.ConnectionFactory
import groovy.jms.provider.ActiveMQPooledJMSProvider
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.jms.Session
import javax.jms.MessageListener
import javax.jms.QueueReceiver
import javax.jms.Connection
import org.apache.log4j.Logger
import java.text.SimpleDateFormat

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
class JMSPool {
    static Logger logger = Logger.getLogger(JMSPool.class.name)
    ExecutorService pool;
    int poolSize = 10;
    ConnectionFactory factory;

    JMSPool() {this(getDefaultConnectionFactory(), null, null)}

    JMSPool(Map cfg) {this(getDefaultConnectionFactory(), cfg, null)}

    JMSPool(ConnectionFactory f) {this(f, null, null)}

    JMSPool(Map cfg, Closure exec) {this(getDefaultConnectionFactory(), cfg, exec)}

    JMSPool(ConnectionFactory f, Closure exec) {this(f, null, exec)}

    JMSPool(ConnectionFactory f, Map cfg) {this(f, cfg, null)}

    JMSPool(ConnectionFactory f, Map cfg, Closure exec) {
        factory = f; poolSize = cfg?.'poolSize' ?: poolSize
        pool = Executors.newFixedThreadPool(poolSize);
    }

    synchronized static ConnectionFactory getDefaultConnectionFactory(Map cfg = null) {
        return new ActiveMQPooledJMSProvider(cfg).getConnectionFactory()
    }

    List<Future> futures = []

    boolean started = false

    def start() {
        println "pool is being started"
        stopped = false;
        started = true;
    }

    boolean stopped = false;

    boolean stop(boolean mayInterruptIfRunning = true, int timeout = -1) {
        long startTime = System.currentTimeMillis()
        futures.each {Future f -> f.cancel(mayInterruptIfRunning)}

        while (!futures.every {Future f -> f.isDone()} && (timeout != -1 && (System.currentTimeMillis() - startTime < timeout))) {
            sleep(100)
        }

        def outstandings = futures.findAll {Future f -> !f.isDone()}

        if (outstandings.size() == 0) { return true; } else {
            logger.warn("${futures.size()} thread(s) fail to cancel within ${timeout}ms")
            return false;
        }

    }

    def onMessage(Map cfg = null, final Object target) {
        if (!started) start();
        int threads = cfg?.'threads' ?: 1
        final String reqQueue = cfg.'queue' //assume to be a single dest, no collection
        final ConnectionFactory f = this.factory;
        final JMSPool jmsPool = this;
        threads.times {
            final var = it;
            futures << pool.submit({
                try {
                    Connection connection = f.createConnection().with {it.clientID = JMS.getDefaultClientID() + ":" + Thread.currentThread().id; it};
                    Session session = c.createSession(false, Session.AUTO_ACKNOWLEDGE)
                    def jms = new JMS(connection, session);
                    println "autoClose: ${jms.autoClose}"
                    jms.onMessage(cfg, target);
                    while (jmsPool.stopped) {
                        println("running, time: ${new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')}")
                        sleep(1000);
                    }
                } catch (e) {
                    logger("thread failure [${Thread.currentThread().id}]", e)
                }
                /* Connection connection = factory.createConnection().with {it.clientID = JMSPool.class.name + Thread.currentThread().id; it};
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
                if (reqQueue) {
                    Queue queue = session.createQueue(reqQueue)
                    QueueReceiver receiver = session.createConsumer(queue)
                    receiver.setMessageListener((target instanceof MessageListener) ? target : target as MessageListener)
                    connection.start()
                }*/
            })
        }
    }

}