package groovy.jms

import groovy.jms.pool.JMSThread
import java.util.concurrent.Callable
import java.util.concurrent.Future
import javax.jms.Queue
import org.apache.log4j.Logger

/**
 * It provides features shared by JMS and JMSPool, as well as define mandatory methods
 */
abstract class AbstractJMS {
    static Logger logger = Logger.getLogger(AbstractJMS.class.name)
    static boolean enableAutoBroker = true;

    Queue createQueue(String queueName) {
        if (this instanceof JMS) {
            return session.createQueue("queue");
        } else {
            Future task = threadPool.submit({
                if (logger.isTraceEnabled()) logger.trace("createQueue() - executing submitted job - jms? ${JMSThread.jms.get() != null}")
                return JMSThread.jms.get().session.createQueue(queueName)
            } as Callable);
            return task.get();
        }
    }

    // abstract onMessage(Map cfg, Object target)
}