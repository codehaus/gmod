package groovy.jms.pool;

import org.apache.log4j.Logger;

import java.util.concurrent.*;

public class JMSThreadPoolExecutor extends ThreadPoolExecutor {
    static Logger logger = Logger.getLogger(JMSThreadPoolExecutor.class.getName());

    public JMSThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public JMSThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public JMSThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public JMSThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);

    }

    protected void afterExecute(Runnable r, Throwable t) {
        if (JMSThread.jms.get() != null) JMSThread.jms.get().connect(); //flush message, TODO: review this 
        super.afterExecute(r, t);
    }

}
