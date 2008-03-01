/**
 * 
 */
package org.lpny.groovyrestlet;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.lpny.groovyrestlet.builder.Global;
import org.lpny.groovyrestlet.builder.RestletBuilder;
import org.restlet.Redirector;
import org.restlet.Router;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The entry point of GroovyRestlet.
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public class GroovyRestlet implements ApplicationContextAware {
    private static final Logger LOG = LoggerFactory
            .getLogger(GroovyRestlet.class);
    private RestletBuilder builder;
    private final GroovyShell shell;
    private ApplicationContext springContext;

    public GroovyRestlet() {
        this(null);
    }

    public GroovyRestlet(final ApplicationContext springContext) {
        super();
        this.springContext = springContext;
        shell = new GroovyShell(Thread.currentThread().getContextClassLoader());
        declareShellContext();
    }

    /**
     * To build from script specified by <code>scriptURI</code>
     * 
     * @param userDefinedContext
     * @param scriptURI
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object build(final Map<String, Object> userDefinedContext,
            final URI scriptURI) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("To build from {}", scriptURI);
            }
            final Binding context = shell.getContext();
            final Map oldVariables = new HashMap(context.getVariables());
            // merge userDefinedContext to variables
            context.getVariables().putAll(userDefinedContext);
            try {
                return build(scriptURI);
            } finally {
                context.getVariables().clear();
                context.getVariables().putAll(oldVariables);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * To build from script specified by <code>scriptURI</code>
     * 
     * @param scriptURI
     * @return
     */
    public Object build(final URI scriptURI) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("To build from {}", scriptURI);
            }
            // Initialize builder if it has not beed done.
            getBuilder().init();
            shell.getContext().setVariable("springContext", springContext);
            getBuilder().setVariable("springContext", springContext);
            shell.getContext().setVariable("builder", builder);
            return shell.evaluate(new File(scriptURI));
        } catch (final Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Unable to build from " + scriptURI, e);
            }
            throw new RuntimeException(e);
        }
    }

    public RestletBuilder getBuilder() {
        if (builder == null) {
            builder = new RestletBuilder();
        }
        return builder;
    }

    public void setApplicationContext(
            final ApplicationContext applicationContext) throws BeansException {
        springContext = applicationContext;
    }

    public void setBuilder(final RestletBuilder builder) {
        Validate.notNull(builder);
        this.builder = builder;
    }

    private void declareShellContext() {
        final Binding context = shell.getContext();
        final Global global = new Global();
        context.setVariable("global", global);
        context.setVariable("protocol", Protocol.class);
        context.setVariable("mediaType", MediaType.class);
        context.setVariable("status", Status.class);
        context.setVariable("challengeScheme", ChallengeScheme.class);
        context.setVariable("redirectorMode", Redirector.class);
        context.setVariable("routingMode", Router.class);
    }
}
