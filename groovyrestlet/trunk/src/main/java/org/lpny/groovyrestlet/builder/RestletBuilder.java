/**
 * 
 */
package org.lpny.groovyrestlet.builder;

import groovy.util.FactoryBuilderSupport;

import org.lpny.groovyrestlet.builder.factory.ApplicationFactory;
import org.lpny.groovyrestlet.builder.factory.ClientFactory;
import org.lpny.groovyrestlet.builder.factory.ComponentFactory;
import org.lpny.groovyrestlet.builder.factory.DirectoryFactory;
import org.lpny.groovyrestlet.builder.factory.GuardFactory;
import org.lpny.groovyrestlet.builder.factory.RedirectorFactory;
import org.lpny.groovyrestlet.builder.factory.ResourceFactory;
import org.lpny.groovyrestlet.builder.factory.RestletFactory;
import org.lpny.groovyrestlet.builder.factory.RouterFactory;
import org.lpny.groovyrestlet.builder.factory.ServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Restlet Builder
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 * @see <a href="http://www.restlet.org">Restlet</a>
 * @see <a href="http://groovy.codehaus.org/GroovyMarkup">Groovy Markup</a>
 */
public class RestletBuilder extends FactoryBuilderSupport {
    private static final Logger LOG = LoggerFactory
                                            .getLogger(RestletBuilder.class);

    public RestletBuilder() {
        super();
        registerFactories();
    }

    private void registerFactories() {
        registerFactory("component", new ComponentFactory());
        registerFactory("application", new ApplicationFactory());
        registerFactory("restlet", new RestletFactory());
        registerFactory("resource", new ResourceFactory());
        registerFactory("router", new RouterFactory());
        registerFactory("directory", new DirectoryFactory());
        registerFactory("client", new ClientFactory());
        registerFactory("server", new ServerFactory());
        registerFactory("guard", new GuardFactory());
        registerFactory("redirector", new RedirectorFactory());
    }
}
