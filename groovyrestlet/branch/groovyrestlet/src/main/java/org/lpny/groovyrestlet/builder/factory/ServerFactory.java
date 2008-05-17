/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.List;
import java.util.Map;

import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shortcut factory to create {@link Server}.<br/>
 * 
 * <b>Attributes:</b>
 * <ul>
 * <li>protocol</li>
 * <li>protocols</li>
 * <li>address</li>
 * <li>port</li>
 * </ul>
 * 
 * @author keke
 * @reversion $Revision$
 * @version
 */
public class ServerFactory extends RestletFactory {
    private static final Logger LOG = LoggerFactory
            .getLogger(ServerFactory.class);
    /**
     * <b>Attribute</b>: The server address
     */
    protected static final String ADDRESS = "address";
    /**
     * <b>Attribute</b>: The server port
     */
    protected static final String PORT = "port";

    /**
     * 
     */
    public ServerFactory() {
        super("server");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object newInstanceInner(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        final List<Protocol> protocols = FactoryUtils.extractProtocols(value,
                attributes);
        final String address = (String) attributes.remove(ADDRESS);
        final Integer port = (Integer) attributes.remove(PORT);
        return new Server(FactoryUtils.getParentRestletContext(builder),
                protocols, address, port, null);
    }

    @Override
    protected Object setChildInner(final FactoryBuilderSupport builder,
            final Object parent, final Object child) {
        if (child != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("To set child {}", child);
            }
            if (child instanceof Restlet) {
                ((Server) parent).setTarget((Restlet) child);
            }
        }
        return null;
    }

}
