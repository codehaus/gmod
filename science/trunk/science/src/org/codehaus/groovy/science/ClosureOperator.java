package org.codehaus.groovy.science;


import groovy.lang.Closure;
import java.util.List;


/**
 * <p>An {@code Operator} implementation that wraps a Groovy closure that
 * specifies what argument lists the operator can accept.</p>
 * 
 * <p>The method {@code accepts( List< ? extends SymbolicExpression > )} assumes
 * that the wrapped closure will accept the given value and return a boolean
 * value. In other words, the closure specified should be compatible with the
 * signature {@code Boolean acceptsClosure( List< ? extends SymbolicExpression >
 * argumentList )}. If it is not, {@code accepts( List< ? extends
 * SymbolicExpression > )} is undefined (but will likely result in an exception
 * being thrown).</p>
 * 
 */
public class ClosureOperator implements Operator
{
	/**
	 * <p>The closure used to determine whether this {@code ClosureOperator}
	 * accepts a given list of arguments.</p>
	 */
	private Closure acceptsClosure;
	
	/**
	 * <p>The name of this {@code ClosureOperator}, or {@code null} if this
	 * operator has no name.</p>
	 */
	private String name;
	
	
	/**
	 * <p>Creates an instance of {@code ClosureOperator} that wraps the given
	 * closure.</p>
	 * 
	 * @param acceptsClosure  the closure to be wrapped
	 * 
	 * @throws NullPointerException  if {@code acceptsClosure} is {@code null}
	 */
	public ClosureOperator( Closure acceptsClosure )
	{
		this( null, acceptsClosure );
	}
	
	/**
	 * <p>Creates an instance of {@code ClosureOperator} that wraps the given
	 * closure and has the given name.</p>
	 * 
	 * @param name            the name of this {@code ClosureOperator}
	 * @param acceptsClosure  the closure to be wrapped
	 * 
	 * @throws NullPointerException  if {@code acceptsClosure} is {@code null}
	 */
	public ClosureOperator( String name, Closure acceptsClosure )
	{
		if ( acceptsClosure == null )
			throw new NullPointerException();
		
		this.acceptsClosure  = acceptsClosure;
		this.name            = name;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
	    final int prime = 7;
	    int result = 60;
	    result = prime * result + acceptsClosure.hashCode();
	    result = prime * result + ((name == null) ? 0 : name.hashCode());
	    
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
	    	(      getClass() != ClosureOperator.class)
	    	||
	    	(  obj.getClass() != ClosureOperator.class)
	    )
		    return false;
	    
	    final ClosureOperator other = (ClosureOperator) obj;
	    
	    return (
	    	acceptsClosure.equals( other.acceptsClosure )
	    	&&
	    	((name == null) ? (other.name == null) : name.equals( other.name ))
	    );
    }
    
	/* (non-Javadoc)
	 * 
	 * @see stuff.Operator#accepts(java.util.List)
	 * 
	 * @throws NullPointerException
	 *     if {@code argumentList} is {@code null} or if the wrapped closure
	 *     returns {@code null} for these arguments
	 * 
	 * @throws ClassCastException
	 *     if the wrapped closure returns a non-boolean value for these
	 *     arguments
	 */
    @Override
	public boolean accepts( List< ? extends SymbolicExpression > argumentList )
	{
		if ( argumentList == null )
			throw new NullPointerException();
		
		return (
			(
				(Boolean) acceptsClosure.call( new Object[]{ argumentList } )
			).booleanValue()
		);
	}
	
    @Override
	public String toString()
	{
		// Return this object's name, if it exists.
		if ( name != null )
			return name;
		
		// Otherwise, return the usual {@code toString} representation for
		// {@code Object}s.
		return super.toString();
	}
}