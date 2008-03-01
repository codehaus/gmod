/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.HashMap;

import org.restlet.Application;
import org.restlet.Restlet;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author keke
 * @reversion $Revision$
 * @version
 */
@Test(groups = "unittest")
public class ApplicationFactoryTest extends AbstractFactoryTest {
    private ApplicationFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new ApplicationFactory();
        assert fixture.getName().equals("application");
    }

    @Test(groups = { "unittest" })
    public void testNewInstance() throws InstantiationException,
            IllegalAccessException {
        final Application app = (Application) fixture.newInstance(
                createMockBuilder(), "application", null, new HashMap());
        assert null != app;
    }

    @Test(groups = { "unittest" }, dependsOnMethods = { "testNewInstance" })
    public void testSetChildNull() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final Application app = (Application) fixture.newInstance(builder,
                "application", null, new HashMap());
        fixture.setChild(builder, app, null);
        assert app.getRoot() == null;
    }

    @Test(groups = { "unittest" }, dependsOnMethods = { "testNewInstance" })
    public void testSetChildRestlet() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final Application app = (Application) fixture.newInstance(builder,
                "application", null, new HashMap());
        final Restlet child = new Restlet();
        fixture.setChild(builder, app, child);
        assert app.getRoot() == child;
    }
}
