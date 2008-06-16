package org.codehaus.groovy.science;


/**
 * <p>An operator intended for use as an identifier in a
 * {@code SymbolicExpression}. An identifier is a symbol that references a value
 * without representing that value outright. The specific value it references
 * could very well be unknown.</p>
 * 
 * @see org.codehaus.groovy.science.SymbolicExpression
 */
public class IdentifierOperator
{
	/**
	 * <p>The type of value this identifier can refer to.</p>
	 */
	private Class< ? > type;
	
	/**
	 * <p>The name of this identifier.</p>
	 */
	private String name;
	
	/**
	 * <p>Constructs an {@code IdentifierOperator} with the given type and
	 * name.</p>
	 * 
	 * @param type  the type of value this identifier can refer to
	 * @param name  the name of this identifier
	 * 
	 * @throws NullPointerException
	 *     if {@code type} or {@code name} is {@code null}
	 */
	public IdentifierOperator( Class< ? > type, String name )
	{
		if (
			(type == null)
			||
			(name == null)
		)
			throw new NullPointerException();
		
		this.type = type;
		this.name = name;
	}
	
	/**
	 * <p>Constructs an {@code IdentifierOperator} with the given name and the
	 * {@code java.lang.Object} type.</p>
	 * 
	 * @param name  the name of this identifier
	 * 
	 * @throws NullPointerException
	 *     if {@code type} or {@code name} is {@code null}
	 */
	public IdentifierOperator( String name )
	{
		this( Object.class, name );
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
	    final int prime = 23;
	    int result = 30;
	    result = prime * result + type.hashCode();
	    result = prime * result + name.hashCode();
	    
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
	    	(      getClass() != IdentifierOperator.class)
	    	||
	    	(  obj.getClass() != IdentifierOperator.class)
	    )
		    return false;
	    
	    final IdentifierOperator other = (IdentifierOperator) obj;
	    
	    return (
	    	type.equals( other.type )
	    	&&
	    	name.equals( other.name )
	    );
    }
	
	/**
	 * @see org.codehaus.groovy.science.IdentifierOperator#type
	 * 
	 * @return  the type of value this identifier can refer to
	 */
	public Class< ? > getType()
	{
		return type;
	}

	/**
	 * @see org.codehaus.groovy.science.IdentifierOperator#name
	 * 
	 * @return  the name of this identifier
	 */
	public String getName()
	{
		return name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return ( "(Identifier: " + type + " " + name + ")" );
	}
}