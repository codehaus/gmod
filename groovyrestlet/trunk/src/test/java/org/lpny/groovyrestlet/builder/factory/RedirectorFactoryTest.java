/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.HashMap;
import java.util.Map;

import org.lpny.groovyrestlet.builder.factory.RedirectorFactory;
import org.restlet.Redirector;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author keke
 * @version
 * @since
 * @revision $Revision$
 */
@Test(groups = { "unittest" })
public class RedirectorFactoryTest extends AbstractFactoryTest {
    private RedirectorFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new RedirectorFactory();
    }

    public void testNewInstance() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final Map attributes = new HashMap();
        final Redirector redirector = (Redirector) fixture.newInstance(builder,
                "redirector", null, attributes);
        assert redirector != null;
    }
}
