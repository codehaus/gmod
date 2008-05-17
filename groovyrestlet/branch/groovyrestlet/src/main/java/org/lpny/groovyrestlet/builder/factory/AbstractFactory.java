/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Abstract Factory of Groovy Restlet.
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public abstract class AbstractFactory extends groovy.util.AbstractFactory {
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractFactory.class);

    protected static final String AUTO_ATTACH = "autoAttach";
    protected static final String CONS_ARG = "consArgs";
    protected static final String OF_BEAN = "ofBean";
    protected static final String OF_CLASS = "ofClass";
    protected static final String POST_ATTACH = "postAttach";
    protected static final String SPRING_CONTEXT = "springContext";
    protected static final String URI = "uri";

    private final List<String> filters = new ArrayList<String>();
    protected String name;

    public AbstractFactory() {
        this(null);
    }

    public AbstractFactory(final String name) {
        super();
        this.name = name;
        addFilter(OF_BEAN).addFilter(OF_CLASS).addFilter(URI).addFilter(
                CONS_ARG).addFilter(AUTO_ATTACH).addFilter(POST_ATTACH);
    }

    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see groovy.util.Factory#newInstance(groovy.util.FactoryBuilderSupport,
     *      java.lang.Object, java.lang.Object, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public Object newInstance(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        Validate.notNull(name);
        filterAttributes(builder.getContext(), attributes);
        Object result = FactoryUtils.createFromSpringContext(
                (ApplicationContext) builder.getVariable("springContext"),
                builder.getContext());
        if (result == null) {
            result = newInstanceInner(builder, name, value, attributes);
        }
        return postNewInstance(result, builder, name, value, attributes);
    }

    @Override
    public void onNodeCompleted(final FactoryBuilderSupport builder,
            final Object parent, final Object node) {
        super.onNodeCompleted(builder, parent, node);
        for (final String filter : filters) {
            builder.getContext().remove(filter);
        }
    }

    @Override
    public final void setChild(final FactoryBuilderSupport builder,
            final Object parent, final Object child) {
        if (FactoryUtils.isAutoAttachEnabled(builder.getContext())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("TO set Child {} on Parent {}", new Object[] { child,
                        parent });
            }
            doPostAttach(builder, setChildInner(builder, parent, child));
        }
    }

    @Override
    public final void setParent(final FactoryBuilderSupport builder,
            final Object parent, final Object child) {
        if (FactoryUtils.isAutoAttachEnabled(builder.getContext())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("TO set Parent {} on Child {}", new Object[] {
                        parent, child });
            }

            doPostAttach(builder, setParentInner(builder, parent, child));
        }
    }

    private void doPostAttach(final FactoryBuilderSupport builder,
            final Object result) {
        if (result == null) {
            return;
        }
        final Closure postAttach = (Closure) builder.getContext().remove(
                POST_ATTACH);
        if (postAttach == null) {
            return;
        }
        postAttach.call(result);
    }

    /**
     * 
     * @param keyName
     * @return this object, in order to build a method chain.
     */
    protected AbstractFactory addFilter(final String keyName) {
        assert keyName != null;
        filters.add(keyName);
        return this;
    }

    /**
     * Filter attributes: move all to-be-filtered attributes (which are defined
     * in list {@link AbstractFactory#filters}) from map
     * <code>attributes</code> to map <code>context</code>
     * 
     * @param context
     *            context map
     * @param attributes
     *            attribute map
     */
    @SuppressWarnings("unchecked")
    protected void filterAttributes(final Map context, final Map attributes) {
        assert context != null;
        assert attributes != null;
        for (final String key : filters) {
            if (attributes.containsKey(key)) {
                context.put(key, attributes.remove(key));
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected abstract Object newInstanceInner(
            final FactoryBuilderSupport builder, final Object name,
            final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException;

    @SuppressWarnings("unchecked")
    protected Object postNewInstance(final Object instance,
            final FactoryBuilderSupport builder, final Object name,
            final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        return instance;
    }

    protected Object setChildInner(final FactoryBuilderSupport builder,
            final Object parent, final Object child) {
        return null;

    }

    protected Object setParentInner(final FactoryBuilderSupport builder,
            final Object parent, final Object child) {
        return null;
    }

}
