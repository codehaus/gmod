/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Router;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author keke
 * @version
 * @since
 * @revision $Revision$
 */
@Test(groups = { "unittest" })
public class RouterFactoryTest extends AbstractFactoryTest {
    private RouterFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new RouterFactory();
        assert fixture.getName().equals("router");
    }

    public void testNewInstance() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final Map attributes = new HashMap();
        final Router router = (Router) fixture.newInstance(builder, "router",
                null, attributes);
        assert router != null;
    }
}
