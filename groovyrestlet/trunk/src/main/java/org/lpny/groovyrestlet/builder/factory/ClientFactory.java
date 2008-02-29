/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.List;
import java.util.Map;

import org.restlet.Client;
import org.restlet.data.Protocol;

/**
 * Shortcut factory to create {@link Client}.<br/>
 * 
 * <b>Attributes</b>:
 * <ul>
 * <li>protocol</li>
 * <li>protocols</li>
 * </ul>
 * 
 * @author keke
 * @reversion $Revision$
 * @version 0.1
 * @since 0.1.0
 */
public class ClientFactory extends RestletFactory {
    /**
     * <b>Attribute</b>: {@link Protocol} instance used to create client.
     * 
     * @see Client#Client(org.restlet.Context, Protocol)
     */
    protected static final String PROTOCOL = "protocol";
    /**
     * <b>Attribute</b>: List of {@link Protocol} instances used to create
     * client.
     * 
     * @see Client#Client(org.restlet.Context, List))
     */
    protected static final String PROTOCOLS = "protocols";

    /**
     * 
     */
    public ClientFactory() {
        super("client");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object newInstanceInner(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        final List<Protocol> protocols = FactoryUtils.extractProtocols(value,
                attributes);
        return new Client(FactoryUtils.getParentRestletContext(builder),
                protocols);
    }

}
