package groovy.jms.pool;

import groovy.jms.JMS;

class JMSThread extends Thread {
    static ThreadLocal<JMS> jms = new ThreadLocal<JMS>();

    public JMSThread(Runnable r) {
        super(r);
    }

    public void start() {
        super.start();
    }

    public void interrupt() {
        if (jms.get()!=null) jms.get().close();
        super.interrupt();
    }
}