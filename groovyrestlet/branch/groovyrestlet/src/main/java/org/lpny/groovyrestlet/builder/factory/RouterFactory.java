/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.restlet.Router;

/**
 * Shortcut for creating {@link Router}
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public class RouterFactory extends RestletFactory {

    public RouterFactory() {
        super("router");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object newInstanceInner(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        return new Router(FactoryUtils.getParentRestletContext(builder));
    }

}
