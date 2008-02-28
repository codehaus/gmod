/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.lang.Closure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Finder;
import org.restlet.Handler;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author keke
 * @reversion $Revision$
 * @version
 */
public class SpringFinder extends Finder {

    private static class InnerResource extends Resource {
        private final Map<String, Closure> methodHandlers;

        public InnerResource(final Map<String, Closure> methodHandlers) {
            super();
            this.methodHandlers = methodHandlers;
        }

        @Override
        public void acceptRepresentation(final Representation entity)
                throws ResourceException {
            final Closure closure = methodHandlers.get(ResourceFactory.ACCEPT);
            if (closure == null) {
                super.acceptRepresentation(entity);
            } else {
                closure.call(FactoryUtils.packArgs(this, closure, entity));
            }
        }

        @Override
        public void handleHead() {
            final Closure closure = methodHandlers.get(ResourceFactory.HEAD);
            if (closure == null) {
                super.handleHead();
            } else {
                closure.call(FactoryUtils.packArgs(this, closure));
            }
        }

        @Override
        public void handleOptions() {
            final Closure closure = methodHandlers.get(ResourceFactory.OPTIONS);
            if (closure == null) {
                super.handleOptions();
            } else {
                closure.call(FactoryUtils.packArgs(this, closure));
            }
        }

        @Override
        public void init(final Context context, final Request request,
                final Response response) {
            final Closure closure = methodHandlers.get(ResourceFactory.INIT);
            super.init(context, request, response);
            if (closure != null) {
                closure
                        .call(FactoryUtils.packArgs(this, closure, context, request,
                                response));
            }
        }

        @Override
        public void removeRepresentations() throws ResourceException {
            final Closure closure = methodHandlers.get(ResourceFactory.REMOVE);
            if (closure == null) {
                super.removeRepresentations();
            } else {
                closure.call(FactoryUtils.packArgs(this, closure));
            }
        }

        @Override
        public Representation represent() throws ResourceException {
            final Closure closure = methodHandlers
                    .get(ResourceFactory.REPRESENT);
            if (closure != null) {
                final Class<?>[] paramTypes = closure.getParameterTypes();
                if (paramTypes.length == 1 && paramTypes[0] == Variant.class
                        || paramTypes.length == 0) {
                    return (Representation) closure
                            .call(FactoryUtils.packArgs(this, closure));
                }
            }
            return super.represent();
        }

        @Override
        public Representation represent(final Variant variant)
                throws ResourceException {
            final Closure closure = methodHandlers
                    .get(ResourceFactory.REPRESENT);
            if (closure != null) {
                final Class<?>[] paramTypes = closure.getParameterTypes();
                if (paramTypes.length == 1 && paramTypes[0] == Variant.class
                        || paramTypes.length == 2) {
                    return (Representation) closure.call(FactoryUtils.packArgs(this,
                            closure, variant));
                }
            }
            return super.represent(variant);

        }

        @Override
        public void storeRepresentation(final Representation entity)
                throws ResourceException {
            final Closure closure = methodHandlers.get(ResourceFactory.STORE);
            if (closure == null) {
                super.storeRepresentation(entity);
            } else {
                closure.call(FactoryUtils.packArgs(this, closure, entity));
            }
        }
    }

    private static final List<String> METHODS = Arrays.asList(new String[] {
            ResourceFactory.REMOVE, ResourceFactory.STORE,
            ResourceFactory.HEAD, ResourceFactory.ACCEPT,
            ResourceFactory.OPTIONS, ResourceFactory.REPRESENT,
            ResourceFactory.INIT             });

    static final Logger               LOG     = LoggerFactory
                                                      .getLogger(SpringFinder.class);

    @SuppressWarnings("unchecked")
    private final Map                 context;

    private final ApplicationContext  springContext;

    @SuppressWarnings("unchecked")
    public SpringFinder(final Context restletContext,
            final ApplicationContext springContext, final Map context) {
        super(restletContext);
        this.springContext = springContext;
        this.context = context;
    }

    private Handler createWithClosures() {
        final Map<String, Closure> methodHandlers = new HashMap<String, Closure>();
        for (final String method : METHODS) {
            final Closure methodHandler = (Closure) context.get(method);
            if (methodHandler != null) {
                String methodName = method;
                if (method.equals(ResourceFactory.REMOVE)
                        || method.equals(ResourceFactory.ACCEPT)
                        || method.equals(ResourceFactory.STORE)) {
                    methodName = method + "Representation";
                }
                if (method.equals(ResourceFactory.HEAD)
                        || method.equals(ResourceFactory.OPTIONS)) {
                    methodName = "handle" + method;
                }
                methodHandlers.put(methodName, methodHandler);
            }
        }
        if (methodHandlers.isEmpty()) {
            return new Resource();
        } else {
            return new InnerResource(methodHandlers);
        }
    }

    @Override
    protected Handler createTarget(final Request request,
            final Response response) {
        try {
            Handler handler = (Handler) FactoryUtils.createFromSpringContext(
                    springContext, context);
            if (handler == null) {
                handler = createWithClosures();
            }
            handler.init(getContext(), request, response);
            return handler;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
