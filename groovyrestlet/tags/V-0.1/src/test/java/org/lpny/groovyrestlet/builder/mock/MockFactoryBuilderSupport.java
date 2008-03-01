/**
 * 
 */
package org.lpny.groovyrestlet.builder.mock;

import groovy.util.FactoryBuilderSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * @author keke
 * @reversion $Revision$
 * @version
 */
public class MockFactoryBuilderSupport extends FactoryBuilderSupport {
    private Map context = new HashMap();

    @Override
    public Map getContext() {
        return context;
    }

    public void setContext(final Map context) {
        this.context = context;
    }
}
