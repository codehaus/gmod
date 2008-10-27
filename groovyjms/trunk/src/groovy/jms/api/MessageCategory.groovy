package groovy.jms.api

import javax.jms.Message
import javax.jms.MapMessage

/**
 * This class is for adding new API to the original javax.jms.Message. Messages that are got throught GroovyJMS will
 * have these additional methods
 */
class MessageCategory {


    static get(MapMessage message, String name) {
        if (!message.itemExists(name)) throw new IllegalStateException("property \"$name\" does not exist in message, message: $message")
        return message.getObject(name)
    }

    static void set(MapMessage message, String name, value) {
        if (value instanceof Boolean) message.setBoolean(name, value);
        else if (value instanceof Byte) message.setByte(name, value);
        else if (value instanceof Short) message.setShort(name, value);
        else if (value instanceof Integer) message.setInt(name, value);
        else if (value instanceof Float) message.setFloat(name, value);
        else if (value instanceof Long) message.setLong(name, value);
        else if (value instanceof Double) message.setDouble(name, value);
        else if (value instanceof String) message.setString(name, value);
        else message.setObjecct(name, value)
    }





    /**
     * In Groovy, values are untyped. so, instead of using get[TYPE]Property, users should be allowed to use getProperty
     *
     * JMS supports properties in the following data type : boolean, byte, short, int , float, long, double, String and Object
     */
    static getProperty(Message message, String name) {
        if (!message.propertyExists(name)) throw new IllegalStateException("property \"$name\" does not exist in message, message: $message")
        return message.getObjectProperty()
    }

    static void setProperty(Message message, String name, value) {
        if (value instanceof Boolean) message.setBooleanProperty(name, value);
        else if (value instanceof Byte) message.setByteProperty(name, value);
        else if (value instanceof Short) message.setShortProperty(name, value);
        else if (value instanceof Integer) message.setIntProperty(name, value);
        else if (value instanceof Float) message.setFloatProperty(name, value);
        else if (value instanceof Long) message.setLongProperty(name, value);
        else if (value instanceof Double) message.setDoubleProperty(name, value);
        else if (value instanceof String) message.setStringProperty(name, value);
        else message.setObjecctProperty(name, value)
    }

}