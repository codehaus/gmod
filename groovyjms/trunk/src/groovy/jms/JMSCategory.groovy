package groovy.jms

import javax.jms.*

class JMSCategory extends JMSCoreCategory {

    static Map toMap(MapMessage mapMessage) {
        def result = [:]
        mapMessage.mapNames.each {result.put(it, mapMessage.getObject(it))}
        return result;
    }


    static receive(String dest, Map cfg = null) {
        int timeout = (cfg?.within) ? Integer.valueOf(cfg.within) : 0
        return JMSCoreCategory.session.get().queue(dest).receive(timeout);
    }

    static receiveAll(String dest, Map cfg = null) {
        int timeout = (cfg?.within && cfg?.within.isInteger()) ? cfg.within.toInteger() : 0
        return JMSCoreCategory.session.get().queue(dest).receiveAll(timeout);
    }

    static Queue send(String dest, String message) {
        return JMSCoreCategory.session.get().queue(dest).send(message);
    }

    static Queue sendTo(Map message, String dest) {
        return JMSCoreCategory.session.get().queue(dest).send(message);
    }

    static Queue send(String dest, Map message) {
        return JMSCoreCategory.session.get().queue(dest).send(message);
    }

    static Topic subscribe(String dest) {
        return JMSCoreCategory.session.get().topic(dest);
    }

    static Topic with(Topic topic, Closure listener) {
        with(topic, listener as MessageListener);
    }

    static Topic with(Topic topic, MessageListener listener) {
        topic.subscribe(listener)
    }

    static Topic subscribe(String dest, Closure listener) {
        return JMSCoreCategory.session.get().topic(dest).with(listener);
    }

    static Topic subscribe(String dest, MessageListener listener) {
        return JMSCoreCategory.session.get().topic(dest).with(listener);
    }

    static void publishTo(String textMessage, String dest, Map cfg = null) {
        if (!JMSCoreCategory.session.get()) throw new IllegalStateException("ThreadLocal session is not available")
        Topic topic = JMSCoreCategory.session.get().topic(dest);
        sendMessage(topic, textMessage, cfg);
    }
}