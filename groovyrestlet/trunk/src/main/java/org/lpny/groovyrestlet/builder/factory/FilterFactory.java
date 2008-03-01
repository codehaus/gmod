/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.restlet.Filter;
import org.restlet.Restlet;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * @author keke <keke@codehaus.org>
 * 
 * @reversion $Revision$
 * @version 0.1
 * @since 0.1
 */
public class FilterFactory extends RestletFactory {
	protected static final String	AFTER	= "after";
	protected static final String	BEFORE	= "before";

	protected static final String	HANDLE	= "handle";

	public FilterFactory() {
		super("filter");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object newInstanceInner(final FactoryBuilderSupport builder,
	        final Object name, final Object value, final Map attributes)
	        throws InstantiationException, IllegalAccessException {
		final Closure before = (Closure) attributes.remove(BEFORE);
		final Closure after = (Closure) attributes.remove(AFTER);
		final Closure handle = (Closure) attributes.remove(HANDLE);
		return new Filter(FactoryUtils.getParentRestletContext(builder)) {

			@Override
			protected void afterHandle(final Request request,
			        final Response response) {
				if (after != null) {
					after.call(FactoryUtils.packArgs(this, after, request,
					        response));
				} else {
					super.afterHandle(request, response);
				}
			}

			@Override
			protected int beforeHandle(final Request request,
			        final Response response) {
				if (before != null) {
					return (Integer) before.call(FactoryUtils.packArgs(this,
					        before, request, response));
				} else {
					return super.beforeHandle(request, response);
				}
			}

			@Override
			protected int doHandle(final Request request,
			        final Response response) {
				if (handle != null) {
					return (Integer) handle.call(FactoryUtils.packArgs(this,
					        handle, request, response));
				} else {
					return super.doHandle(request, response);
				}
			}

		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lpny.groovyrestlet.builder.factory.AbstractFactory#setChildInner(groovy.util.FactoryBuilderSupport,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Object setChildInner(final FactoryBuilderSupport builder,
	        final Object parent, final Object child) {
		if (child != null) {
			if (child instanceof Restlet) {
				((Filter) parent).setNext((Restlet) child);
			}
		}
		return null;
	}
}
