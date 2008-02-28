/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import java.util.HashMap;
import java.util.Map;

import org.lpny.groovyrestlet.builder.factory.ServerFactory;
import org.restlet.Server;
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
public class ServerFactoryTest extends AbstractFactoryTest {
    private ServerFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new ServerFactory();
    }

    @Test(groups = { "unittest" })
    public void testNewInstance() throws InstantiationException,
            IllegalAccessException {
        final Map attributes = new HashMap();
        attributes.put("protocol", Protocol.HTTP);
        attributes.put("port", 8080);
        final Server server = (Server) fixture.newInstance(createMockBuilder(),
                "server", null, attributes);
        assert server != null;
    }

}
