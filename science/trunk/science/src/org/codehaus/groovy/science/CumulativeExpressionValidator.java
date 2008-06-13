package org.codehaus.groovy.science;


import groovy.lang.Closure;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>An {@code ExpressionValidator} that is built incrementally by specifying
 * restrictions on the expressions it accepts and by specifying exceptions to
 * those restrictions.</p>
 * 
 * <p>A {@code CumulativeExpressionValidator} must be built in a careful order.
 * Whichever restrictions and exceptions have been registered the latest have
 * the highest precedence.</p>
 */
public class CumulativeExpressionValidator implements ExpressionValidator
{
	/**
	 * <p>A type of incremental change to the {@code validates} method.</p>
	 */
	private enum FilterType
	{
		Restriction,
		Exception;
	}
	
	
	private List< Closure > filters;
	private List< FilterType > filterTypes;
	
	
	/**
	 * <p>Constructs a {@code CumulativeExpressionValidator} that accepts any
	 * expression whatsoever.</p>
	 */
	public CumulativeExpressionValidator()
	{
		filters      = new ArrayList< Closure >();
		filterTypes  = new ArrayList< FilterType >();
	}
	
	/**
	 * <p>Incrementally change the {@code validates} method by registering a filter
	 * that will override its present output in certain cases.</p>
	 * 
	 * <p>If {@code type} is {@code Restriction}, {@code filter} is a closure that
	 * accepts a {@code SymbolicExpression} and returns {@code false} if it
	 * wants to forbid that expression and {@code true} otherwise.</p>
	 * 
	 * <p>If {@code type} is {@code Exception}, {@code filter} is a closure that
	 * accepts a {@code SymbolicExpression} and return {@code true} if it wants
	 * to allow that expression to bypass earlier restrictions and {@code false}
	 * otherwise.</p>
	 * 
	 * @param type    the type of filter that {@code filter} represents
	 * @param filter  an incremental change to the {@code validates} method
	 */
	private void addFilter( FilterType type, Closure filter )
	{
		if ( filter == null )
			throw new NullPointerException();
		
		filters.add( filter );
		filterTypes.add( type );
	}
	
	/**
	 * <p>Incrementally change the {@code validates} method by registering a
	 * restriction that will narrow the range of expressions that it will
	 * accept.</p>
	 * 
	 * @param restriction
	 *     a closure that accepts a {@code SymbolicExpression} and returns
	 *     {@code false} if it wants to forbid that expression and {@code true}
	 *     otherwise
	 */
	public void addRestriction( Closure restriction )
	{
		addFilter( FilterType.Restriction, restriction );
	}
	
	/**
	 * <p>Incrementally change the {@code validates} method by registering an
	 * exception that will expand the range of expressions that it will
	 * accept.</p>
	 * 
	 * @param restriction
	 *     a closure that accepts a {@code SymbolicExpression} and returns
	 *     {@code true} if it wants to allow that expression to bypass earlier
	 *     restrictions and {@code false} otherwise
	 */
	public void addException( Closure restriction )
	{
		addFilter( FilterType.Exception, restriction );
	}
	
	/* (non-Javadoc)
	 * 
	 * @see org.codehaus.groovy.science.ExpressionValidator#validates(
	 *     org.codehaus.groovy.science.SymbolicExpression
	 * )
	 * 
	 * @throws ClassCastException
	 *     only if at least one of the registered filters returns something
	 *     other than {@code true} or {@code false}
	 * 
	 * @throws MissingMethodException
	 *     only if at least one of the registered filters does not accept a
	 *     single argument of type {@code SymbolicExpression}
	 */
	@Override
	public boolean validates( SymbolicExpression expression )
	{
		boolean result = true;
		
		for ( int filterIndex = 0; filterIndex < filters.size(); filterIndex++ )
		{
			switch ( filterTypes.get( filterIndex ) )
			{
			case Restriction:
				result = result && (Boolean)filters.get( filterIndex ).call( expression );
				break;
			case Exception:
				result = result || (Boolean)filters.get( filterIndex ).call( expression );
				break;
			default:
				throw new IllegalStateException();
			}
		}
		
		return result;
	}
}