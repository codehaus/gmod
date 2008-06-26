package org.codehaus.groovy.science;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <p>A symbolic expression, represented as an operator applied to a list of
 * of arguments, which are themselves symbolic expressions.</p>
 */
public class SymbolicExpression
{
	/**
	 * <p>The operator being used at the top level of this
	 * {@code SymbolicExpression}.</p>
	 */
	private Object operator;
	
	/**
	 * <p>The subexpressions that are used as the arguments of this expression's
	 * root operator.</p>
	 */
	private List< SymbolicExpression > argumentList;

	/**
	 * <p>Creates a {SymbolicExpression} with the given operator and
	 * subexpressions.</p>
	 * 
	 * @param operator      the operator to use at the top level
	 * @param argumentList  the arguments of that operator
	 * 
	 * @throws NullPointerException
	 *     if {@code operator} or {@code argumentList} is {@code null},
	 *     or if {@code argumentList} contains {@code null}
	 */
	public SymbolicExpression(
		Object operator,
		SymbolicExpression... argumentList
	)
	{
		this( operator, Arrays.asList( argumentList ) );
	}
	
	/**
	 * <p>Creates a {SymbolicExpression} with the given operator and
	 * subexpressions.</p>
	 * 
	 * @param operator      the operator to use at the top level
	 * @param argumentList  the arguments of that operator
	 * 
	 * @throws NullPointerException
	 *     if {@code operator} or {@code argumentList} is {@code null},
	 *     or if {@code argumentList} contains {@code null}
	 */
	public SymbolicExpression(
		Object operator,
		List< SymbolicExpression > argumentList
	)
	{
		if (
			(operator == null)
			||
			(argumentList == null)
		)
			throw new NullPointerException();
		
		for ( SymbolicExpression argument: argumentList )
		{
			if ( argument == null )
				throw new NullPointerException();
		}
		
		this.operator = operator;
		this.argumentList = new ArrayList< SymbolicExpression >( argumentList );
	}
	
	
	/**
	 * <p>Creates a nullary {@code SymbolicExpression} with the given
	 * operator.</p>
	 * 
	 * @param operator  the root operator to use
	 * 
	 * @return  the nullary expression with the given operator
	 * 
	 * @throws NullPointerException  if {@code operator} is {@code null}
	 */
	public static SymbolicExpression expr( Object operator )
	{
		return new SymbolicExpression(
			operator,
			new ArrayList< SymbolicExpression >()
		);
	}
	
	/**
	 * <p>Creates a {@code SymbolicExpression} with the given operator and
	 * arguments.</p>
	 * 
	 * @param operator      the root operator to use
	 * @param argumentList  the arguments to apply that operator to
	 * 
	 * @return  the expression with the given operator and arguments
	 * 
	 * @throws NullPointerException
	 *     if {@code operator} or {@code argumentList} is {@code null}, or if
	 *     {@code argumentList} contains {@code null}
	 */
	public static SymbolicExpression expr(
		Object operator,
		List< SymbolicExpression > argumentList
	)
	{
		return new SymbolicExpression( operator, argumentList );
	}
	
	/**
	 * <p>Creates a {@code SymbolicExpression} with the given operator and
	 * arguments.</p>
	 * 
	 * @param operator   the root operator to use
	 * @param arguments  the arguments to apply that operator to
	 * 
	 * @return  the expression with the given operator and arguments
	 * 
	 * @throws NullPointerException
	 *     if {@code operator} or {@code arguments} is {@code null}, or if
	 *     {@code arguments} contains {@code null}
	 */
	public static SymbolicExpression expr(
		Object operator,
		SymbolicExpression... arguments
	)
	{
		if ( arguments == null )
			throw new NullPointerException();
		
		return new SymbolicExpression( operator, Arrays.asList( arguments ) );
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
	 * @see org.codehaus.groovy.science.SymbolicExpression#operator
	 * 
	 * @return
	 *     the operator being used at the top level of this
	 *     {@code SymbolicExpression}
	 */
	public Object getOperator()
	{
		return operator;
	}
	
	/**
	 * @see org.codehaus.groovy.science.SymbolicExpression#argumentList
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
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.Plus} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Plus
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression plus( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Plus,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.Minus} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Minus
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression minus( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Minus,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.Multiply} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Multiply
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression multiply( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Multiply,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.Power} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Power
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression power( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Power,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.Div} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Div
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression div( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Div,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.Mod} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Mod
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression mod( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Mod,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.Or} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Or
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression or( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Or,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.And} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#And
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression and( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.And,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.Xor} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Xor
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression xor( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Xor,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.GetAt} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#GetAt
	 * 
	 * @param index  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression getAt( SymbolicExpression index )
	{
		return new SymbolicExpression(
			OverloadableOperators.GetAt,
			Arrays.asList( new SymbolicExpression[]{ this, index } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression and two others using
	 * the {@code OverloadableOperators.PutAt} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Negative
	 * 
	 * @param index  the expression acting as the index
	 * @param value  the expression acting as the value being put at the index
	 * 
	 * @return  the resulting expression
	 */
	public SymbolicExpression putAt(
		SymbolicExpression index,
		SymbolicExpression value
	)
	{
		return new SymbolicExpression(
			OverloadableOperators.PutAt,
			Arrays.asList( new SymbolicExpression[]{ this, index, value } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.LeftShift} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#LeftShift
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression leftShift( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.LeftShift,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of combining this expression with another using the
	 * {@code OverloadableOperators.RightShift} operator.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#RightShift
	 * 
	 * @param other  the expression to combine with this one
	 * 
	 * @return  the combined expression
	 */
	public SymbolicExpression rightShift( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.RightShift,
			Arrays.asList( new SymbolicExpression[]{ this, other } )
		);
	}
	
	/**
	 * <p>Returns the result of applying the
	 * {@code OverloadableOperators.BitwiseNegate} operator to this
	 * expression.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#BitwiseNegate
	 * 
	 * @return  the resulting expression
	 */
	public SymbolicExpression bitwiseNegate()
	{
		return new SymbolicExpression(
			OverloadableOperators.BitwiseNegate,
			Arrays.asList( new SymbolicExpression[]{ this } )
		);
	}
	
	/**
	 * <p>Returns the result of applying the
	 * {@code OverloadableOperators.Negative} operator to this expression.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Negative
	 * 
	 * @return  the resulting expression
	 */
	public SymbolicExpression negative( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Negative,
			Arrays.asList( new SymbolicExpression[]{ this } )
		);
	}
	
	/**
	 * <p>Returns the result of applying the
	 * {@code OverloadableOperators.Positive} operator to this expression.</p>
	 * 
	 * @see org.codehaus.groovy.science.OverloadableOperators#Positive
	 * 
	 * @return  the resulting expression
	 */
	public SymbolicExpression positive( SymbolicExpression other )
	{
		return new SymbolicExpression(
			OverloadableOperators.Positive,
			Arrays.asList( new SymbolicExpression[]{ this } )
		);
	}
}