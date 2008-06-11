package org.codehaus.groovy.science;


import java.util.ArrayList;
import java.util.List;


/**
 * A symbolic expression, represented as an {@code Operator} applied to a list
 * of arguments, which are themselves symbolic expressions.
 * 
 * As a {@code SymbolicExpression} is created, it is verified to make sure that
 * its operator can be applied to its arguments.
 */
public class SymbolicExpression
{
	/**
	 * The {@code Operator} being used at the top level of this
	 * {@code SymbolicExpression}.
	 */
	private Operator operator;
	
	/**
	 * The subexpressions that are used as the arguments of this expression's
	 * root operator.
	 */
	private List< SymbolicExpression > argumentList;
	
	/**
	 * Creates a {SymbolicExpression} with the given operator and
	 * subexpressions.
	 * 
	 * @param operator      the {@code Operator} to use at the top level
	 * @param argumentList  the arguments of that {@code Operator}
	 * 
	 * @throws NullPointerException
	 *     if {@code operator} or {@code argumentList} is {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *     if the operator is not compatible with the arguments
	 */
	public SymbolicExpression(
		Operator operator,
		List< ? extends SymbolicExpression > argumentList
	)
	{
		// Make sure that the operator is compatible with the arguments.
		if ( !operator.accepts( argumentList ) )
			throw new IllegalArgumentException();
		
		this.operator = operator;
		this.argumentList = new ArrayList< SymbolicExpression >( argumentList );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
	    final int prime = 47;
	    int result = 54;
	    result = prime * result + operator.hashCode();
	    result = prime * result + argumentList.hashCode();
	    
	    return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		
		if (
			(obj == null)
			||
			(      getClass() != SymbolicExpression.class)
			||
			(  obj.getClass() != SymbolicExpression.class)
		)
			return false;
		
		SymbolicExpression that = (SymbolicExpression) obj;
		
		return (
			that.operator.equals( operator )
			&&
			that.argumentList.equals( argumentList )
		);
	}
	
	/**
	 * @see operator
	 * 
	 * @return
	 *     the {@code Operator} being used at the top level of this
	 *     {@code SymbolicExpression}
	 */
	public Operator getOperator()
	{
		return operator;
	}
	
	/**
	 * @see argumentList
	 * 
	 * @return
	 *     the subexpressions that are used as the arguments of this
	 *     expression's root operator
	 */
	public List< SymbolicExpression > getArgumentList()
	{
		return new ArrayList< SymbolicExpression >( argumentList );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return ( "<< " + operator + ": " + argumentList + " >>" );
	}
}