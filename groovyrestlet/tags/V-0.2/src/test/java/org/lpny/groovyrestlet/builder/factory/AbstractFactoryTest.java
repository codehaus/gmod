/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import org.lpny.groovyrestlet.builder.mock.MockFactoryBuilderSupport;

/**
 * @author keke
 * @reversion $Revision$
 * @version
 */
public abstract class AbstractFactoryTest {

    protected FactoryBuilderSupport createMockBuilder() {
        final FactoryBuilderSupport mockBuilder = new MockFactoryBuilderSupport();
        mockBuilder.setVariable("springContext", null);
        return mockBuilder;
    }

}
