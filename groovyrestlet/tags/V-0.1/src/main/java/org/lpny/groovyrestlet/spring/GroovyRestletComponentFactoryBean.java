/**
 * 
 */
package org.lpny.groovyrestlet.spring;

import java.net.URI;

import org.apache.commons.lang.Validate;
import org.lpny.groovyrestlet.GroovyRestlet;
import org.restlet.Restlet;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * A Spring {@link FactoryBean} to create a {@link Restlet} component using
 * {@link GroovyRestlet}.
 * 
 * @author keke
 * @version 0.1
 * @since 0.1
 * @revision $Revision$
 */
public class GroovyRestletComponentFactoryBean extends AbstractFactoryBean {
    private GroovyRestlet groovyRestlet;
    private Class<?> objectType;
    private String scrpitUri;

    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    public void setGroovyRestlet(final GroovyRestlet groovyRestlet) {
        this.groovyRestlet = groovyRestlet;
    }

    public void setObjectType(final Class<?> objectType) {
        this.objectType = objectType;
    }

    public void setScrpitUri(final String scrpitUri) {
        this.scrpitUri = scrpitUri;
    }

    @Override
    protected Object createInstance() throws Exception {
        Validate.notNull(groovyRestlet);
        Validate.notEmpty(scrpitUri);
        return groovyRestlet.build(new URI(scrpitUri));
    }

}
