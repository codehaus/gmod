package groovy.jms.pool

import groovy.jms.JMS
import groovy.jms.JMSCoreCategory
import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.Session
import org.apache.log4j.Logger


class JMSThread extends Thread {
    public static Logger logger = Logger.getLogger(JMSThread.class);
    private ConnectionFactory connectionFactory;
    public static final ThreadLocal<JMS> jms = new ThreadLocal<JMS>();
    private Runnable runnable;
    private boolean setToShutdown = false;

    public JMSThread(ThreadGroup g, Runnable r, ConnectionFactory f) {
        super(g, r);
        this.runnable = r;
        this.connectionFactory = f;
    }

    public void run() {
        org.apache.log4j.MDC.put("tid", Thread.currentThread().getId());
        try {
            if (jms.get() == null) {
                Connection connection = connectionFactory.createConnection().with {it.clientID = JMS.getDefaultClientID() + ":" + Thread.currentThread().id; it};
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
                JMSThread.jms.set(new JMS([connection, session], [autoClose: false]));
                if (logger.isTraceEnabled()) logger.trace("run() - created jms - this: " + this.toString());
            }

            //use(JMSCoreCategory) {
            if (logger.isTraceEnabled()) logger.trace("run() - run runnable - this: " + this.toString()); // (inside JMSCategory)
            if (this.runnable) {
                this.runnable.run();
                while (!setToShutdown) {sleep(10)}
                if (logger.isTraceEnabled()) logger.trace("run() - runnable is being shutdown gracefully - this: " + this.toString());
            }
            //TODO handle Callable case without loop
            //}
        } catch (Exception e) {
            logger.error("run() - with exception", e);
        } finally {
            if (JMSThread.jms.get() != null) JMSThread.jms.get().close();
            JMSThread.jms.set(null);
            if (logger.isTraceEnabled()) logger.trace("run() - end thread, cleanned up resource - this: " + this.toString());
        }
    }

    public void interrupt() {
        if (logger.isTraceEnabled()) logger.trace("interrupt() - interrupting thread, set setToShutdown to true - this: " + this.toString());
        this.setToShutdown = true;
    }

    /*  public static void main(args) {
        org.apache.log4j.MDC.put("tid", Thread.currentThread().getId());
        ExecutorService pool = new ThreadPoolExecutor(2, 2, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), {r -> new JMSThread(r)} as ThreadFactory)
        Future f1 = pool.submit({
            sleep(1000)
            org.apache.log4j.MDC.put("tid", Thread.currentThread().getId());
            JMSThread.logger.trace("running");
            return "result"
        } as Callable)
        Future f2 = pool.submit({
            sleep(1000)
            org.apache.log4j.MDC.put("tid", Thread.currentThread().getId());
            JMSThread.logger.trace("running");

        })

        println "f1: ${f1.get()}"
        f1.cancel(true)
        println "f2: ${f2.get()}"
        sleep(10000);
        pool.shutdownNow();
    }*/
}