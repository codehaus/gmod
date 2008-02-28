/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.restlet.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ComponentFactory to create {@link Component}. <br/>
 * 
 * 
 * 
 * 
 * 
 * 
 * @author keke
 * @reversion $Revision$
 * @version
 */
public class ComponentFactory extends RestletFactory {
    static final Logger LOG = LoggerFactory.getLogger(ComponentFactory.class);

    public ComponentFactory() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object newInstanceInner(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        if (LOG.isDebugEnabled()) {
            LOG
                    .debug("To create an instance, value={}",
                            new Object[] { value });
        }
        return new Component();
    }
}
