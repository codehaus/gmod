package groovy.jms;

import javax.jms.MapMessage;
import javax.jms.JMSException;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

public class JMSUtils {


    static boolean isEnhanced(Class clazz) { return clazz.metaClass.hasMetaMethod('isEnhanced', null)}

    static final void enhance(Class clazz) {
        if (!isEnhanced(clazz)) {
            // isEnhanced()
            clazz.metaClass.isEnhanced << {-> return true }

            // get/setProperty() - property
            clazz.metaClass.getProperty = {String name ->
                return (delegate.propertyExists(name)) ? delegate.getObjectProperty(name) : null
            }
            clazz.metaClass.setProperty = {String name, value ->
                if (value instanceof Boolean) delegate.setBooleanProperty(name, value);
                else if (value instanceof Byte) delegate.setByteProperty(name, value);
                else if (value instanceof Short) delegate.setShortProperty(name, value);
                else if (value instanceof Integer) delegate.setIntProperty(name, value);
                else if (value instanceof Float) delegate.setFloatProperty(name, value);
                else if (value instanceof Long) delegate.setLongProperty(name, value);
                else if (value instanceof Double) delegate.setDoubleProperty(name, value);
                else if (value instanceof String) delegate.setStringProperty(name, value);
                else delegate.setObjecctProperty(name, value)
            }

            // for MapMessage
            if (MapMessage.isAssignableFrom(clazz)) {
                // asMap()
                clazz.metaClass.asType << {Class asType ->
                    if (asType == Map || asType == HashMap) {
                        Map m = new HashMap();
                        Enumeration mmFields = delegate.getMapNames();
                        while (mmFields.hasMoreElements()) {
                            String k = (String) mmFields.nextElement();
                            m.put(k, delegate.getObject(k));
                        }
                        return m;
                    }
                }

                clazz.metaClass.getData = {String name ->
                    return (delegate.itemExists(name)) ? delegate.getObject(name) : null
                }

                //Remarks, for multiple access, it is not optimized
                clazz.metaClass.getMap << {->
                    Map m = new HashMap();
                    Enumeration mmFields = delegate.getMapNames();
                    while (mmFields.hasMoreElements()) {
                        String k = (String) mmFields.nextElement();
                        m.put(k, delegate.getObject(k));
                    }
                    return m;
                }
            }


        }
    }


}