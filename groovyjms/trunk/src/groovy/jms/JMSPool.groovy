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
class JMSPool extends JMS {
    ExecutorService pool;
    int poolSize = 10;

    JMSPool(Map cfg = null, Closure c = null) {
        super(null, null, c)
        this.provider = new ActiveMQPooledJMSProvider(cfg)
        this.factory = provider.getConnectionFactory()
        poolSize = cfg?.'poolSize' ?: poolSize
        pool = Executors.newFixedThreadPool(poolSize);
    }

    def init() {} // TODO refactor JMS and JMSPool to the same level using a new parent class

    List<Future> futures = []

    def onMessage(Map cfg = null, final Object target) {
        if (cfg && !cfg.containsKey('threads')) {
            return super.onMessage(dest, cfg, target)
        }

        int threads = cfg?.'threads' ?: 1
        final String reqQueue = cfg.'queue' //assume to be a single dest, no collection
        threads.times {
            final var = it;
            futures << pool.submit({
                def jms = new JMS();
                jms.setAutoClose false
                jms.onMessage(cfg, target);
                
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