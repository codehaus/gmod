/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.Arrays;
import java.util.HashMap;

import org.lpny.groovyrestlet.builder.factory.ClientFactory;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author keke
 * @version
 * @since
 * @revision $Revision$
 */
@Test(groups = { "unittest" })
public class ClientFactoryTest extends AbstractFactoryTest {
    private ClientFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new ClientFactory();
    }

    public void testNewInstance() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final Client client = (Client) fixture.newInstance(builder, "client",
                null, new HashMap());
        assert client != null;
        assert client.getProtocols().isEmpty();
    }

    public void testNewInstanceWithProtocol() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final HashMap attributes = new HashMap();
        attributes.put("protocol", Protocol.HTTP);
        final Client client = (Client) fixture.newInstance(builder, "client",
                null, attributes);
        assert client != null;
        assert client.getProtocols().get(0) == Protocol.HTTP;
    }

    public void testNewInstanceWithProtocols() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final HashMap attributes = new HashMap();

        attributes.put("protocols", Arrays
                .asList(Protocol.HTTP, Protocol.HTTPS));
        final Client client = (Client) fixture.newInstance(builder, "client",
                null, attributes);
        assert client != null;
        assert client.getProtocols().size() == 2;
    }
}
