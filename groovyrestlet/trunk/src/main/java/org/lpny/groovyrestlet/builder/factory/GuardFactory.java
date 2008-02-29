/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.restlet.Guard;
import org.restlet.data.ChallengeScheme;

/**
 * Shortcut factory to create {@link Guard}.
 * 
 * <h4>Important attributes</h4>
 * <ul>
 * <li><code>scheme</code>: specify the {@link ChallengeScheme} used to
 * create this {@link Guard}, if no scheme is specified, this factory will use
 * <code>None</code> as default</li>
 * <li><code>realm</code>: specify the realm used to create this
 * {@link Guard}</li>
 * </ul>
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 * @see Guard#Guard(org.restlet.Context, ChallengeScheme, String)
 */
public class GuardFactory extends RestletFactory {
    private static final ChallengeScheme NONE_SCHEME = ChallengeScheme
            .valueOf("None");
    /**
     * <b>Attribute</b>: Guard Realm
     */
    protected static final String REALM = "realm";
    /**
     * <b>Attribute:</b> Guard challengeSchema
     */
    protected static final String SCHEME = "scheme";

    public GuardFactory() {
        super("guard");
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
        ChallengeScheme schema = (ChallengeScheme) attributes.remove(SCHEME);
        if (schema == null) {
            schema = NONE_SCHEME;
        }
        return new Guard(FactoryUtils.getParentRestletContext(builder), schema,
                (String) attributes.remove(REALM));
    }

}
