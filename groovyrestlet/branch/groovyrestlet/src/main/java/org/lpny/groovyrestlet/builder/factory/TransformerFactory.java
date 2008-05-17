/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.restlet.Transformer;
import org.restlet.resource.Representation;

/**
 * @author keke
 * @reversion $Revision$
 * @version 0.1
 */
public class TransformerFactory extends FilterFactory {

    protected static final String MODE           = "mode";
    protected static final String REPRESENTATION = "representation";

    /*
     * (non-Javadoc)
     * 
     * @see org.lpny.groovyrestlet.builder.factory.FilterFactory#newInstanceInner(groovy.util.FactoryBuilderSupport,
     *      java.lang.Object, java.lang.Object, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object newInstanceInner(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        return new Transformer((Integer) attributes.remove(MODE),
                (Representation) attributes.remove(REPRESENTATION));
    }
}
