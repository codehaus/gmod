/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.restlet.Directory;

/**
 * Shortcut to create {@link Directory}
 * <h4>Important attributes</h4>
 * <ul>
 * <li><code>root</code>: uri of this directory. Since GroovyRestlet 0.3
 * this string must be in valid URI format. Neither relative nor absolute file
 * path, like <code>.</code> or <code>c:/file</code>, is not allowed.</li>
 * </ul>
 * 
 * @author keke
 * @reversion $Revision$
 * @version
 * @see Directory#Directory(org.restlet.Context, String)
 */
public class DirectoryFactory extends RestletFactory {
	protected static final String	ROOT	= "root";

	public DirectoryFactory() {
		super("directory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see groovy.util.AbstractFactory#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object newInstanceInner(final FactoryBuilderSupport builder,
	        final Object name, final Object value, final Map attributes)
	        throws InstantiationException, IllegalAccessException {
		// will try to get root value from attributes, if no root is defined
		// will try to use value as the root
		String root = (String) attributes.remove(ROOT);

		if (root == null) {
			if (value != null && value instanceof String) {
				root = (String) value;
			}
		}
		Validate.notNull(root, "Root should not be null");
		return new Directory(FactoryUtils.getParentRestletContext(builder),
		        root);
	}

}
