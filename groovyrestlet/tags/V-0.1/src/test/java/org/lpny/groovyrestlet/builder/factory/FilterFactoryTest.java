/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.restlet.Filter;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author keke
 * 
 */
@Test(groups = { "unittest" })
public class FilterFactoryTest extends AbstractFactoryTest {
    private FilterFactory fixture;

    @BeforeTest(groups = { "unittest" })
    @Test(groups = { "unittest" })
    public void construct() {
        fixture = new FilterFactory();
        assert fixture.getName().equals("filter");
    }

    public void testNewInstance() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final Map attributes = new HashMap();
        final Filter filter = (Filter) fixture.newInstance(builder, "filter",
                null, attributes);
        assert filter != null;
    }

    public void testNewInstanceWithClosure() throws InstantiationException,
            IllegalAccessException {
        final FactoryBuilderSupport builder = createMockBuilder();
        final Map attributes = new HashMap();
        final AtomicBoolean beforeCalled = new AtomicBoolean(false);
        attributes.put("before", new Closure(this) {

            @Override
            public Object call(final Object[] args) {
                beforeCalled.set(true);
                return null;
            }

            @Override
            public Class[] getParameterTypes() {
                return new Class[] { Request.class, Response.class };
            }
        });
        final Filter filter = (Filter) fixture.newInstance(builder, "filter",
                null, attributes);
        assert filter != null;
        filter.handle(new Request());
        assert beforeCalled.get();
    }
}
