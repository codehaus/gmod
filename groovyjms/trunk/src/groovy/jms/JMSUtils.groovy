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

            // asMap()
            if (MapMessage.isAssignableFrom(clazz)) clazz.metaClass.asMap << {->
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
