/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.restlet.Redirector;

/**
 * Shortcut for creating {@link Redirector}.
 * 
 * @author keke
 * @version 0.1.0
 * @since 0.1.0
 * @revision $Revision$
 */
public class RedirectorFactory extends RestletFactory {

    public RedirectorFactory() {
        super("redirector");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object newInstanceInner(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        return new Redirector(FactoryUtils.getParentRestletContext(builder),
                null);
    }

}
