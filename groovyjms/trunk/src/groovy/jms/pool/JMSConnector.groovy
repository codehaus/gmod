package groovy.jms.pool

import groovy.jms.JMS
import java.util.concurrent.RunnableFuture
import java.util.concurrent.TimeUnit
import javax.jms.MessageListener
import javax.jms.Destination
import javax.jms.MessageConsumer
import javax.jms.Session
import groovy.jms.JMSCoreCategory

/**
 * Design for message consuming first
 */
class JMSConnector implements RunnableFuture {
    int interval = 200;
    boolean running = true;
    JMS jms;
    Map cfg;
    MessageListener listener;

    JMSConnector(Map cfg, Closure closure) { this(cfg, closure as MessageListener)}

    JMSConnector(Map cfg, MessageListener listener) {
        assert (cfg.containsKey('topic') || cfg.containsKey('queue'))
        this.cfg = cfg; this.listener = listener;
    }

    public void run() {
        if (!jms) jms = new JMS();
        jms.autoClose = false
        jms.onMessage(cfg, listener)
        while (running) {
            sleep(interval)
        }
        //TODO double check if JMS stops here
    }//auto-close

    boolean cancelled = false;

    public boolean cancel(boolean mayInterruptIfRunning) {
        println("JMSConnector.cancel()")
        running = false; cancelled = true; return true;
    }

    public boolean isDone() { return !running; }

    public Object get() { return null;}

    public Object get(long timeout, TimeUnit unit) { return null; }

}