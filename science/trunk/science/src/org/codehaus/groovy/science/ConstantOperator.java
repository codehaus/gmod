package org.codehaus.groovy.science;


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
	 * Constructs a {@code ConstantOperator} that wraps the given constant
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