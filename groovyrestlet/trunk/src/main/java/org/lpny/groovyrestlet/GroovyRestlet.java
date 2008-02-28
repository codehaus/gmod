/**
 * 
 */
package org.lpny.groovyrestlet;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.net.URI;

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
import org.springframework.context.ApplicationContext;

/**
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public class GroovyRestlet {
    private static final Logger      LOG = LoggerFactory
                                                 .getLogger(GroovyRestlet.class);
    private final RestletBuilder     builder;
    private final GroovyShell        shell;
    private final ApplicationContext springContext;

    public GroovyRestlet() {
        this(null);
    }

    public GroovyRestlet(final ApplicationContext springContext) {
        super();
        this.springContext = springContext;
        shell = new GroovyShell(Thread.currentThread().getContextClassLoader());
        builder = new RestletBuilder();
        builder.setVariable("springContext", springContext);
        // declare shell variables
        declareShellContext();
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
            return shell.evaluate(new File(scriptURI));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void declareShellContext() {
        final Binding context = shell.getContext();
        final Global global = new Global();
        context.setVariable("builder", builder);
        context.setVariable("global", global);
        context.setVariable("protocol", Protocol.class);
        context.setVariable("mediaType", MediaType.class);
        context.setVariable("springContext", springContext);
        context.setVariable("status", Status.class);
        context.setVariable("challengeScheme", ChallengeScheme.class);
        context.setVariable("redirectorMode", Redirector.class);
        context.setVariable("routingMode", Router.class);
    }
}
