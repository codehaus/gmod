package org.codehaus.groovy.science;


import groovy.lang.Closure;
import java.util.ArrayList;


/**
 * An operator intended for use as a constant value in a
 * {@code SymbolicExpression}.
 * 
 * @see org.codehaus.groovy.science.SymbolicExpression
 * 
 * @param <T>  the type of the constant value wrapped by this operator
 */
public class ConstantOperator< T >
{
	/**
	 * The constant value this operator wraps.
	 */
	private T value;
	
	/**
	 * Creates a {@code ConstantOperator} that wraps the given constant
	 * value.
	 * 
	 * @param value  the constant value to wrap
	 * 
	 * @throws NullPointerException  if {@code value} is {@code null}
	 */
	public ConstantOperator( T value )
	{
		if ( value == null )
			throw new NullPointerException();
		
		this.value = value;
	}
	
	/**
	 * <p>Creates a {@code SymbolicExpression} that represents the given
	 * constant value. This is done using {@code ConstantOperator}.</p>
	 * 
	 * @param value  the value for the expression to represent
	 * 
	 * @return  an expression representing the given value
	 * 
	 * @throws NullPointerException  if {@code value} is {@code null}
	 */
	public static SymbolicExpression con( Object value )
	{
		return new SymbolicExpression(
			new ConstantOperator< Object >( value ),
			new ArrayList< SymbolicExpression >()
		);
	}
	
	/**
	 * <p>Retrieves the constant value that a {@code SymbolicExpression}
	 * using {@code ConstantOperator} represents.</p>
	 * 
	 * @param expression  the constant expression
	 * 
	 * @return  the value represented by the constant expression
	 * 
	 * @throws IllegalArgumentException
	 *     if {@code expression} is non-{@code null} but is not a nullary
	 *     expression with a {@code ConstantOperator} as its operator
	 * 
	 * @throws NullPointerException  if {@code expression} is {@code null}
	 */
	public static Object unCon( SymbolicExpression expression )
	{
		if ( expression == null )
			throw new NullPointerException();
		
		if ( !isCon( expression ) )
			throw new IllegalArgumentException();
		
		return ((ConstantOperator< ? >)expression.getOperator()).getValue();
	}
	
	/**
	 * <p>Returns {@code true} if the given {@code SymbolicExpression} is a
	 * nullary expression using {@code ConstantOperator}.</p>
	 * 
	 * @param expression  the expression to check
	 * 
	 * @return
	 *     {@code true} if the given expression is nullary and uses
	 *     {@code ConstantOperator}; {@code false} otherwise
	 * 
	 * @throws NullPointerException  if {@code expression} is {@code null}
	 */
	public static boolean isCon( SymbolicExpression expression )
	{
		if ( expression == null )
			throw new NullPointerException();
		
		return (
			(expression.getOperator() instanceof ConstantOperator)
			&&
			expression.getArgumentList().isEmpty()
		);
	}
	
	/**
	 * <p>Wraps the given {@code Closure} to create a corresponding
	 * {@code Closure} that accepts constant {@code SymbolicExpression}s and
	 * returns a constant {@code SymbolicExpression}, where the constant values
	 * of all of these expressions are values that the original {@code Closure}
	 * would have accepted and returned.</p>
	 * 
	 * <p>For instance, in Groovy, {@code inCon( { a, b -> a + b } )} should
	 * result in a {@code Closure} that is functionally identical to
	 * {@code { a, b -> con( unCon( a ) + unCon( b ) ) }}. In fact, those
	 * {@code Closure}s should throw the same exceptions when given the same
	 * parameters.</p>
	 * 
	 * @param simpleClosure  the {@code Closure} to wrap
	 * 
	 * @return  the wrapped {@code Closure}
	 * 
	 * @throws IllegalArgumentException
	 *     if {@code expression} is non-{@code null} but is not a nullary
	 *     expression with a {@code ConstantOperator} as its operator
	 * 
	 * @throws NullPointerException  if {@code expression} is {@code null}
	 */
	public static Closure inCon( Closure simpleClosure )
	{
		if ( simpleClosure == null )
			throw new NullPointerException();
		
		final Closure finalSimpleClosure = simpleClosure; 
		
		return new Closure( null )
		{
			@SuppressWarnings("unused")
            public SymbolicExpression doCall( SymbolicExpression... arguments )
			{
				Object[] argumentValues = new Object[ arguments.length ];
				for ( int index = 0; index < arguments.length; index++ )
				{
					argumentValues[ index ] = unCon( arguments[ index ] );
				}
				
				return con( finalSimpleClosure.call( argumentValues ) );
			}
		};
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
	    final int prime = 59;
	    int result = 10;
	    result = prime * result + value.hashCode();
	    
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
	    	(      getClass() != ConstantOperator.class)
	    	||
	    	(  obj.getClass() != ConstantOperator.class)
	    )
		    return false;
	    
	    final ConstantOperator< ? > other = (ConstantOperator< ? >) obj;
	    
	    return value.equals( other.value );
    }
	
	/**
	 * @see org.codehaus.groovy.science.ConstantOperator#value
	 * 
	 * @return  the constant value this operator wraps
	 */
	public T getValue()
	{
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return ( "(Constant: " + value + ")" );
	}
}