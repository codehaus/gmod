/**
 * 
 */
package org.lpny.groovyrestlet.builder;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author keke
 * @reversion $Revision$
 * @version 0.1
 */
public class Global extends GroovyObjectSupport {
    private static final Logger LOG = LoggerFactory.getLogger(Global.class);
    private static final String STATUS = "status";

    public Object methodMissing(final String name, final Object args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("To check missing method: {}, args={}", new Object[] {
                    name, args });
        }
        if (STATUS.equals(name)) {
            return makeStatus(InvokerHelper.asArray(args));
        }
        throw new MissingMethodException(name, RestletBuilder.class,
                InvokerHelper.asArray(args));
    }

    public Object propertyMissing(final String name) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("To check missing property: {}", name);
        }
        throw new MissingPropertyException(name, RestletBuilder.class);
    }

    private Object makeStatus(final Object[] args) {
        if (args.length == 1 && args[0] instanceof Integer) {
            return Status.valueOf((Integer) args[0]);
        }
        throw new MissingMethodException("status", RestletBuilder.class, args);
    }

}
