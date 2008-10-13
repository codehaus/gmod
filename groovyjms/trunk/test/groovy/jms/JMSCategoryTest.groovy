package groovy.jms

import groovy.jms.provider.ActiveMQJMSProvider
import javax.jms.*

class JMSCategoryTest extends GroovyTestCase {
    ActiveMQJMSProvider provider;
    ConnectionFactory jms; //simulate injection

    void setUp() { provider = new ActiveMQJMSProvider(); jms = provider.getConnectionFactory() }

    void tearDown() { provider.broker?.stop() }

    void testDefaultConnFactory() {
        assertNotNull("default conn factory is not available", provider.getConnectionFactory())
    }

    void testTopic() {
        String messageToSend = "this is the message to send"
        String messageToCheck;
        use(JMSCategory) {
            jms.session().topic("testTopic0").subscribe({Message m -> messageToCheck = m.text} as MessageListener)
            jms.topic("testTopic0").send(messageToSend); jms.close();
        }
        sleep(1000)
        assertEquals("callback message doesn't match", messageToSend, messageToCheck)
    }

    void testQueueInTheSameSession() {
        String messageToSend = "this is the message to send", messageToCheck;
        List<Message> messages
        use(JMSCategory) {
            Session session = jms.session(); session.queue("testQueue0").send(messageToSend);
            sleep(100); messages = session.queue("testQueue0").receiveAll(); session.close();
        }
        assertEquals("message size incorrect", 1, messages?.size())
        assertEquals("callback message doesn't match", messageToSend, messages[0].text)
    }

    void testQueueInTheDiffConn() {
        String messageToSend = "this is the message to send", messageToCheck;
        List<Message> messages;
        use(JMSCategory) {Session session = jms.session(); session.queue("testQueue0").send(messageToSend); session.close()}
        use(JMSCategory) {messages = jms.queue("testQueue0").receiveAll(1000); jms.close()}
        assertEquals("message size incorrect", 1, messages?.size())
        assertEquals("callback message doesn't match", messageToSend, messages[0].text)
    }

    void testTempQueuSyncReply() {
        String messageToSend = "passphase", messageToCheck;
        Queue replyQueue;
        use(JMSCategory) {
            replyQueue = jms.session().createQueue("replyQueue")
            jms.session().queue("testQueue0").send(messageToSend, [JMSCorrelationID: 'unittest', JMSReplyTo: replyQueue])
            jms.close();
        }
        use(JMSCategory) {
            Message message = jms.connect().queue("testQueue0").receive(1000); jms.close();
            String replyText = message.text + "ABC";
            assertNotNull(message.JMSReplyTo)
            jms.connect(); message.JMSReplyTo.send(replyText, [JMSCorrelationID: 'unittest']); jms.close();
        }
        use(JMSCategory) {
            jms.connect(); messageToCheck = replyQueue.receive(1000).text; jms.close();
        }
        assertEquals("passphase hash doesn't match", messageToSend + "ABC", messageToCheck)
    }

    void testReplyWithReplyMethod() {
        String messageToSend = "passphase";
        Queue replyQueue;
        use(JMSCategory) {
            replyQueue = jms.session().createQueue("replyQueue")
            jms.session().queue("testQueue0").send(messageToSend, [JMSCorrelationID: 'unittest', JMSReplyTo: replyQueue])
            jms.close();
        }
        use(JMSCategory) {
            jms.queue("testQueue0").receive(1000).with {it.reply(it.text + "ABC")}; jms.close();
        }
        Message messageToCheck;
        use(JMSCategory) {
            jms.connect(); messageToCheck = replyQueue.receive(1000); jms.close();
        }
        assertEquals("passphase hash doesn't match", messageToSend + "ABC", messageToCheck.text)
        assertEquals("JMSCorrelationID doesn't match", 'unittest', messageToCheck.JMSCorrelationID)
    }

    void testJMS(){
        use(JMSCategory){
            jms.session()
            "queue".send("message")
            assertNotNull("queue".receive(waitTime:100))
        }
    }

}