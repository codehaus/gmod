package org.codehaus.groovy.science;


import java.util.List;


/**
 * Essentially, one of the symbols used in a {@code SymbolicExpression}. This is
 * what specifies the relationship between an expression and its maximal proper
 * subexpressions.
 * 
 * For instance, if {@code E==m*c**2} were represented as a
 * {@code SymbolicExpression}, the root operator of that expression would be
 * {@code ==}, and the arguments given to that operator would be {@code E} and
 * {@code m*c**2}. The expression representing {@code m*c**2} would in turn have
 * the root operator {@code *}, and so forth. Each of the symbols {@code E},
 * {@code m}, and {@code c} and the constant {@code 2} would be represented by a
 * nullary operator that stored enough information for the program to determine
 * which symbol or constant it represented.
 * 
 * In order to prevent the misuse of an operator, so that expressions like
 * {@code (a==b)*(b==c)} cannot be created accidentally, an {@code Operator}
 * provides a method {@code accepts( List< ? extends SymbolicExpression > )} for
 * verifying that its arguments are appropriate.
 */
public interface Operator
{
	/**
	 * Determines whether a given list of arguments is compatible with this
	 * {@code Operator}.
	 * 
	 * @param argumentList
	 *     the arguments that would potentially be given to this
	 *     {@code Operator}
	 * @return
	 *     {@code true} if the {@code Operator} is compatible with the given
	 *     subexpression arguments; {@code false} otherwise
	 */
	public boolean accepts( List< ? extends SymbolicExpression > argumentList );
}