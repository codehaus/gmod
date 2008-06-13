package org.codehaus.groovy.science;


/**
 * <p>An object that can determine whether a given {@code SymbolicExpression}
 * fits some kind of criteria.</p>
 */
public interface ExpressionValidator
{
	/**
	 * <p>Checks the given expression against this object's criteria and returns
	 * whether the criteria are satisfied.</p>
	 * 
	 * @param expression  the expression to validate
	 * 
	 * @return
	 *     {@code true} if the given expression fits the criteria;
	 *     {@code false} otherwise
	 */
	public boolean validates( SymbolicExpression expression );
}