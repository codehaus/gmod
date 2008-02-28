/**
 * 
 */
package org.lpny.groovyrestlet.builder;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author keke
 * @reversion $Revision$
 * @version
 */
public class Global extends GroovyObjectSupport {
    private static final Logger LOG    = LoggerFactory.getLogger(Global.class);
    private static final String STATUS = "status";

    public Object methodMissing(final String name, final Object args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("To check missing method: {}, args={}", new Object[] {
                    name, args });
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

}
