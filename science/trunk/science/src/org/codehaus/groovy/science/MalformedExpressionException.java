package org.codehaus.groovy.science;

/**
 * <p>An {@code Exception} indicating that a {@code SymbolicExpression} was in
 * an incorrect format.</p>
 * 
 * @see org.codehaus.groovy.science.SymbolicExpression
 */
public class MalformedExpressionException extends Exception
{
	private static final long serialVersionUID = 1;
	
	/**
	 * <p>Constructs a {@code MalformedExpressionException} with no message or
	 * cause.</p>
	 */
	public MalformedExpressionException()
	{
		super();
	}
	
	/**
	 * <p>Constructs a {@code MalformedExpressionException} with the given
	 * message and no cause.</p>
	 * 
	 * @param message
	 *     a message describing the nature of this
	 *     {@code MalformedExpressionException}
	 */
	public MalformedExpressionException( String message )
	{
		super( message );
	}
	
	/**
	 * <p>Constructs a {@code MalformedExpressionException} with the given
	 * message and cause.</p>
	 * 
	 * @param message
	 *     a message describing the nature of this
	 *     {@code MalformedExpressionException}
	 * 
	 * @param cause
	 *     a thrown {@code Throwable} that was responsible for this
	 *     {@code MalformedExpressionException} being thrown
	 */
	public MalformedExpressionException( String message, Throwable cause )
	{
		super( message, cause );
	}
	
	/**
	 * <p>Constructs a {@code MalformedExpressionException} with the given
	 * cause and a message based on that cause.</p>
	 * 
	 * @param cause
	 *     a thrown {@code Throwable} that was responsible for this
	 *     {@code MalformedExpressionException} being thrown
	 */
	public MalformedExpressionException( Throwable cause )
	{
		super( cause );
	}
}