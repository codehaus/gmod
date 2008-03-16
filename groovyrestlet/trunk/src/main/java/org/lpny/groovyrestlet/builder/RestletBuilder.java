/**
 * 
 */
package org.lpny.groovyrestlet.builder;

import groovy.util.FactoryBuilderSupport;

import java.util.List;

import org.lpny.groovyrestlet.builder.factory.AbstractFactory;
import org.lpny.groovyrestlet.builder.factory.ApplicationFactory;
import org.lpny.groovyrestlet.builder.factory.ClientFactory;
import org.lpny.groovyrestlet.builder.factory.ComponentFactory;
import org.lpny.groovyrestlet.builder.factory.DirectoryFactory;
import org.lpny.groovyrestlet.builder.factory.FilterFactory;
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
	private static final Logger	LOG	        = LoggerFactory
	                                                .getLogger(RestletBuilder.class);
	private boolean	            initialized	= false;

	public RestletBuilder() {
		super();
	}

	public void init() {
		if (!initialized) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No factory was registered, register all defaults");
			}
			registerFactories();
			initialized = true;
		}
	}

	/**
	 * 
	 * @param factories
	 */
	public void setFactories(final List<AbstractFactory> factories) {
		for (final AbstractFactory factory : factories) {
			registerFactory(factory.getName(), factory);
		}
		initialized = true;
	}

	private void registerFactories() {
		registerFactory(new ComponentFactory());
		registerFactory(new ApplicationFactory());
		registerFactory(new RestletFactory());
		registerFactory(new ResourceFactory());
		registerFactory(new RouterFactory());
		registerFactory(new DirectoryFactory());
		registerFactory(new ClientFactory());
		registerFactory(new ServerFactory());
		registerFactory(new GuardFactory());
		registerFactory(new RedirectorFactory());
		registerFactory(new FilterFactory());
	}

	private void registerFactory(final AbstractFactory factory) {
		registerFactory(factory.getName(), factory);
	}
}
