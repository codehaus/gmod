/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import java.util.HashMap;

import org.lpny.groovyrestlet.builder.factory.ComponentFactory;
import org.restlet.Component;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public class ComponentFactoryTest extends AbstractFactoryTest {
    private ComponentFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new ComponentFactory();
    }

    @Test(groups = { "unittest" })
    public void newInstance() throws InstantiationException,
            IllegalAccessException {
        final Component component = (Component) fixture.newInstance(
                createMockBuilder(), "component", null, new HashMap());
        assert component != null;
    }
}
