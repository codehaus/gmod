/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.restlet.Finder;
import org.restlet.Router;
import org.restlet.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Factory to create <i>"resource"</i> of Restlet.
 * 
 * Actually the instance created by this factory is a {@link SpringFinder}
 * instance rather than an instance of {@link Resource}. See implementation of
 * {@link Router#attach(String, Class)}.
 * 
 * <h4>Usage:</h4>
 * 
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 * @see SpringFinder
 * @see Finder
 * @see Router
 */
public class ResourceFactory extends AbstractFactory {
    private static final Logger   LOG       = LoggerFactory
                                                    .getLogger(ResourceFactory.class);
    protected static final String ACCEPT    = "accept";
    protected static final String HEAD      = "head";
    protected static final String INIT      = "init";
    protected static final String OPTIONS   = "options";
    protected static final String REMOVE    = "remove";
    protected static final String REPRESENT = "represent";
    protected static final String STORE     = "store";

    /**
     * Constructor.
     */
    public ResourceFactory() {
        super("resource");
        addFilter(REMOVE).addFilter(REPRESENT).addFilter(OPTIONS).addFilter(
                ACCEPT).addFilter(STORE).addFilter(HEAD).addFilter(INIT);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object newInstance(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        Validate.notNull(name);
        filterAttributes(builder.getContext(), attributes);
        return newInstanceInner(builder, name, value, attributes);
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
        return new SpringFinder(FactoryUtils.getParentRestletContext(builder),
                (ApplicationContext) builder.getVariable(SPRING_CONTEXT),
                new HashMap(builder.getContext()));
    }

    @Override
    protected Object setParentInner(final FactoryBuilderSupport builder,
            final Object parent, final Object child) {
        if (parent != null) {
            final String uri = (String) builder.getContext().remove(URI);
            if (parent instanceof Router) {
                final Router router = (Router) parent;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("To attach {} to parent with uri={}",
                            new Object[] { child, uri });
                }
                if (uri == null) {
                    return router.attachDefault((Finder) child);
                } else {
                    return router.attach(uri, (Finder) child);
                }
            }
        }
        return null;
    }
}
