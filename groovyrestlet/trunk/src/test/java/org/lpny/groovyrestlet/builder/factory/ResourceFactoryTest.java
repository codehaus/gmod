/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import java.util.HashMap;
import java.util.Map;

import org.lpny.groovyrestlet.builder.factory.ResourceFactory;
import org.lpny.groovyrestlet.builder.factory.SpringFinder;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public class ResourceFactoryTest extends AbstractFactoryTest {
    private ResourceFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new ResourceFactory();
    }

    @Test(groups = { "unittest" })
    public void testNewInstance() throws InstantiationException,
            IllegalAccessException, ResourceException {
        final SpringFinder finder = (SpringFinder) fixture.newInstance(
                createMockBuilder(), "resource", null, new HashMap());
        assert null != finder;
        final Request request = new Request();
        final Response response = new Response(request);
        final Resource resource = (Resource) finder.createTarget(request,
                response);
        final Representation representation = resource.represent(new Variant());
        assert null == representation;
    }

    @Test(groups = { "unittest" })
    public void testNewInstanceWithGet() throws InstantiationException,
            IllegalAccessException, ResourceException {
        final FactoryBuilderSupport mockBuilder = createMockBuilder();
        final Map attributes = new HashMap();
        attributes.put("represent", new Closure(this) {

            @Override
            public Object call(final Object[] args) {
                return new StringRepresentation("TEST");
            }

            @Override
            public Class[] getParameterTypes() {
                return new Class[] { Variant.class };
            }
        });
        final SpringFinder finder = (SpringFinder) fixture.newInstance(
                mockBuilder, "resource", null, attributes);
        assert null != finder;
        final Request request = new Request();
        final Response response = new Response(request);
        final Resource resource = (Resource) finder.createTarget(request,
                response);
        final Representation representation = resource.represent(new Variant());
        assert null != representation;
    }

}
