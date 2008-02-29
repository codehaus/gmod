/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.restlet.Application;
import org.restlet.Restlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory to create an instance of {@link Application}.
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public class ApplicationFactory extends RestletFactory {
    private static final Logger LOG = LoggerFactory
            .getLogger(ApplicationFactory.class);

    public ApplicationFactory() {
        super("application");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lpny.gr.builder.factory.AbstractFactory#newInstanceInner(groovy.util.FactoryBuilderSupport,
     *      java.lang.Object, java.lang.Object, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object newInstanceInner(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        return new Application(FactoryUtils.getParentRestletContext(builder));
    }

    @Override
    protected Object setChildInner(final FactoryBuilderSupport builder,
            final Object parent, final Object child) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("SetChild: child={} on parent={}", new Object[] { child,
                    parent });
        }
        if (child == null) {
            return null;
        }
        if (child instanceof Restlet) {
            final Application application = (Application) parent;
            application.setRoot((Restlet) child);
        }
        return null;
    }

}
