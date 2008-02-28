/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Guard;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public class RestletFactoryTest extends AbstractFactoryTest {
    private RestletFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new RestletFactory();
    }

    @Test(groups = { "unittest" })
    public void testNewInstance() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport mockBuilder = createMockBuilder();
        final Map attributes = new HashMap();
        final Restlet restlet = (Restlet) fixture.newInstance(mockBuilder,
                "restlet", null, attributes);
        assert restlet != null;
    }

    @Test(groups = { "unittest" })
    public void testNewInstanceOfClassInsGoodConsArg()
            throws InstantiationException, IllegalAccessException {
        final FactoryBuilderSupport mockBuilder = createMockBuilder();
        final Map attributes = new HashMap();
        attributes.put("ofClass", Guard.class);
        attributes.put("consArgs", new Object[] { new Context(),
                ChallengeScheme.HTTP_BASIC, "test" });
        final Restlet restlet = (Restlet) fixture.newInstance(mockBuilder,
                "restlet", null, attributes);
        assert restlet != null;
        assert restlet instanceof Guard;
    }

    @Test(groups = { "unittest" })
    public void testNewInstanceOfClassString() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport mockBuilder = createMockBuilder();
        final Map attributes = new HashMap();
        attributes.put("ofClass", Router.class.getName());
        final Restlet restlet = (Restlet) fixture.newInstance(mockBuilder,
                "restlet", null, attributes);
        assert restlet != null;
        assert restlet instanceof Router;
    }

    @Test(groups = { "unittest" }, expectedExceptions = { InstantiationException.class })
    public void testNewInstanceOfClassStringBadConsArg()
            throws InstantiationException, IllegalAccessException {
        final FactoryBuilderSupport mockBuilder = createMockBuilder();
        final Map attributes = new HashMap();
        attributes.put("ofClass", Guard.class.getName());
        final Restlet restlet = (Restlet) fixture.newInstance(mockBuilder,
                "restlet", null, attributes);
    }

    @Test(groups = { "unittest" })
    public void testNewInstanceOfClassStringGoodConsArg()
            throws InstantiationException, IllegalAccessException {
        final FactoryBuilderSupport mockBuilder = createMockBuilder();
        final Map attributes = new HashMap();
        attributes.put("ofClass", Guard.class.getName());
        attributes.put("consArgs", new Object[] { new Context(),
                ChallengeScheme.HTTP_BASIC, "test" });
        final Restlet restlet = (Restlet) fixture.newInstance(mockBuilder,
                "restlet", null, attributes);
        assert restlet != null;
        assert restlet instanceof Guard;
    }

    @Test(groups = { "unittest" })
    public void testNewInstanceWithHandler() throws InstantiationException,
            IllegalAccessException, IOException {
        final FactoryBuilderSupport mockBuilder = createMockBuilder();
        final Map attributes = new HashMap();
        attributes.put("handle", new Closure(this) {

            @Override
            public Object call(final Object[] args) {
                ((Response) args[1]).setEntity("helo", MediaType.TEXT_ALL);
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see groovy.lang.Closure#getParameterTypes()
             */
            @Override
            public Class[] getParameterTypes() {
                return new Class[] { Request.class, Response.class };
            }
        });
        final Restlet restlet = (Restlet) fixture.newInstance(mockBuilder,
                "restlet", null, attributes);
        assert restlet != null;
        final Request request = new Request();
        final Response response = new Response(request);
        restlet.handle(request, response);
        assert response.getEntity().getText() == "helo";
    }

    @Test(groups = { "unittest" }, expectedExceptions = { IllegalArgumentException.class })
    public void testNullName() throws InstantiationException,
            IllegalAccessException {
        fixture.newInstance(null, null, null, null);
    }

}
