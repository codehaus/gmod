/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import java.lang.reflect.Method;
import java.util.Map;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import org.restlet.Component;
import org.restlet.Guard;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory to create an instance of {@link Restlet} or any of its derived types.
 * 
 * <p>
 * If user does not specify {@link AbstractFactory#OF_BEAN} or
 * {@link AbstractFactory#OF_CLASS} attributes, this factory will create an
 * instance of {@link Restlet}. If {@link AbstractFactory#OF_CLASS} was
 * specified but not Spring Context was defined, this factory will create a new
 * {@link Restlet} instance using {@code Class.newInstance()}. For example:
 * 
 * <pre>
 * <code>
 *   builder.restlet(ofClass:&quot;org.restlet.Guard&quot;)
 *   //instance of class is also accepted
 *   builder.restlet(ofClass:Guard.class)
 * </code> 
 * </pre>
 * 
 * will create an instance of {@link Guard}.
 * </p>
 * <p>
 * Otherwise this factory will consult spring context to create a new instance.<br/>
 * 
 * If both <code>ofClass</code> and <code>ofBean</code> are specified,
 * <code>ofBean</code> will be treated first.
 * </p>
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public class RestletFactory extends AbstractFactory {

    private static final Logger   LOG    = LoggerFactory
                                                 .getLogger(RestletFactory.class);

    protected static final String HANDLE = "handle";

    public RestletFactory() {
        super();
        addFilter(HANDLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lpny.gr.builder.factory.AbstractFactory#newInstanceInner(groovy.util.FactoryBuilderSupport,
     *      java.lang.Object, java.lang.Object, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object newInstanceInner(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        return new Restlet(FactoryUtils.getParentRestletContext(builder));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object postNewInstance(final Object instance,
            final FactoryBuilderSupport builder, final Object name,
            final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        final Closure handler = (Closure) builder.getContext().get(HANDLE);
        if (handler == null) {
            return instance;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("To enhance {}", instance);
        }
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Restlet.class);
        enhancer.setCallbackFilter(new CallbackFilter() {
            public int accept(final Method method) {
                try {
                    if (method.equals(Restlet.class.getMethod("handle",
                            Request.class, Response.class))) {
                        return 1;
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }
        });
        enhancer.setCallbacks(new Callback[] { new Dispatcher() {

            public Object loadObject() throws Exception {
                return instance;
            }
        }, new InvocationHandler() {
            public Object invoke(final Object proxy, final Method method,
                    final Object[] args) throws Throwable {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Invoke Method {} on Proxy {}", new Object[] {
                            method, proxy });
                }
                return handler.call(args);
            }
        } });

        final Object newIns = enhancer.create();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Enhanced: {}", newIns);
        }
        return newIns;
    }

    @Override
    protected Object setParentInner(final FactoryBuilderSupport builder,
            final Object parent, final Object child) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("SetParent: parent={} child={}", new Object[] { parent,
                    child });
        }

        if (parent == null) {
            return null;
        }
        final String uri = (String) builder.getContext().get(URI);
        if (uri == null) {
            return null;
        }
        if (parent instanceof Component) {
            return ((Component) parent).getDefaultHost().attach(uri,
                    (Restlet) child);
        } else if (parent instanceof Router) {
            return ((Router) parent).attach(uri, (Restlet) child);
        }
        return null;
    }

}
