package groovy.jms;

import javax.jms.MapMessage;
import javax.jms.JMSException;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

public class JMSUtils {
    
    public static Map toMap(MapMessage mm) throws JMSException {
        if (mm == null) return null;
        Map m = new HashMap();
        Enumeration mmFields = mm.getMapNames();
        while (mmFields.hasMoreElements()) {
            String k = (String) mmFields.nextElement();
            m.put(k, mm.getObject(k));
        }
        return m;
    }
}
