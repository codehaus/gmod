package groovy.jms.pool;

import groovy.jms.JMS;
import org.apache.log4j.Logger;

class JMSThread extends Thread {
    private Logger logger = Logger.getLogger(JMSThread.class) ;
    static ThreadLocal<JMS> jms = new ThreadLocal<JMS>();
    //TODO add thread group
    public JMSThread(Runnable r) {
        super(r);
    }

    public void start() {
        super.start();
        if (logger.isTraceEnabled()) logger.trace("start() - this: " + this.toString());
    }

    public void interrupt() {
        if (logger.isTraceEnabled()) logger.trace("interrupt() - this: " + this.toString());
        if (jms.get() != null) jms.get().close();
        super.interrupt();
    }
}