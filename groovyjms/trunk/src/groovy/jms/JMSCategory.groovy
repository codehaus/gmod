package groovy.jms

import javax.jms.*

class JMSCategory extends JMSCoreCategory {

    static Map toMap(MapMessage mapMessage) {
        def result = [:]
        mapMessage.mapNames.each {result.put(it, mapMessage.getObject(it))}
        return result;
    }

    static void close(target) {
        JMSCoreCategory.cleanupThreadLocalVariables(null, true)
    }

    static receive(String dest, Map cfg = null) {
        int timeout = (cfg?.within) ? Integer.valueOf(cfg.within) : 0
        return JMSCoreCategory.session.get().queue(dest).receive(timeout);
    }

    static receiveAll(String dest, Map cfg = null) {
        int timeout = (cfg?.'within') ? Integer.valueOf(cfg.'within') : 0
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

    static Topic subscribe(String dest) { return JMSCoreCategory.session.get().topic(dest); } //TODO consider to remove this

    static TopicSubscriber with(Topic topic, Map cfg = null, Closure l) { with(topic, cfg, l as MessageListener); } //TODO consider to remove this

    static TopicSubscriber with(Topic topic, Map cfg = null, MessageListener l) { topic.subscribe(cfg, l) } //TODO consider to remove this

    static TopicSubscriber subscribe(String dest, Map cfg = null, Closure listener) {
        return JMSCoreCategory.session.get().topic(dest).with(cfg, listener);
    }

    static TopicSubscriber subscribe(String dest, Map cfg = null, MessageListener listener) {
        return JMSCoreCategory.session.get().topic(dest).with(cfg, listener);
    }

    static void publishTo(String textMessage, String dest, Map cfg = null) {
        if (!JMSCoreCategory.session.get()) throw new IllegalStateException("ThreadLocal session is not available")
        Topic topic = JMSCoreCategory.session.get().topic(dest);
        sendMessage(topic, textMessage, cfg);
    }


    static Queue listen(String dest) {
        return JMSCoreCategory.session.get().queue(dest);
    }


    static QueueReceiver with(Queue dest, Closure listener) {
        with(dest, listener as MessageListener);
    }

    static QueueReceiver with(Queue dest, MessageListener listener) {
        dest.listen(listener)
    }

    static QueueReceiver listen(String dest, Closure listener) {
        return JMSCoreCategory.session.get().queue(dest).with(listener);
    }

    static QueueReceiver listen(String dest, MessageListener listener) {
        return JMSCoreCategory.session.get().queue(dest).with(listener);
    }
}