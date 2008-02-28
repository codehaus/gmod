/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.HashMap;
import java.util.Map;

import org.lpny.groovyrestlet.builder.factory.GuardFactory;
import org.restlet.Guard;
import org.restlet.data.ChallengeScheme;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author keke
 * @version
 * @since
 * @revision $Revision$
 */
@Test(groups = { "unittest" })
public class GuardFactoryTest extends AbstractFactoryTest {
    private GuardFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new GuardFactory();
    }

    public void testNewInstance() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final Map attributes = new HashMap();
        attributes.put("scheme", ChallengeScheme.HTTP_BASIC);
        final Guard guard = (Guard) fixture.newInstance(builder, "guard", null,
                attributes);
        assert guard != null;
        assert guard.getScheme().equals(ChallengeScheme.HTTP_BASIC);
    }

    public void testNewInstanceWithNoneSchema() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final Map attributes = new HashMap();
        final Guard guard = (Guard) fixture.newInstance(builder, "guard", null,
                attributes);
        assert guard != null;
        assert guard.getScheme().getName().equals("None");
    }
}
