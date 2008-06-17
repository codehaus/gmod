package org.codehaus.groovy.science;


import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.runtime.InvokerHelper;


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
	
	
	/**
	 * <p>The conditions set by each of the incremental changes made to the
	 * {@code validates} method, in the order those changes were registered.</p>
	 */
	private List< Object > filters;
	
	
	/**
	 * <p>The types of each of the incremental changes made to the
	 * {@code validates} method, in the order those changes were registered.</p>
	 */
	private List< FilterType > filterTypes;
	
	
	/**
	 * <p>Constructs a {@code CumulativeExpressionValidator} that accepts any
	 * expression whatsoever.</p>
	 */
	public CumulativeExpressionValidator()
	{
		filters      = new ArrayList< Object >();
		filterTypes  = new ArrayList< FilterType >();
	}
	
	/**
	 * <p>Incrementally change the {@code validates} method by registering a
	 * filter that will override its present output in certain cases.</p>
	 * 
	 * <p>If {@code type} is {@code Restriction}, {@code filter} is an object
	 * whose {@code isCase} method in Groovy accepts a
	 * {@code SymbolicExpression} and returns {@code false} if it wants to
	 * forbid that expression and {@code true} otherwise.</p>
	 * 
	 * <p>If {@code type} is {@code Exception}, {@code filter} is an object
	 * whose {@code isCase} method in Groovy accepts a
	 * {@code SymbolicExpression} and returns {@code true} if it wants to allow
	 * that expression to bypass earlier restrictions and {@code false}
	 * otherwise.</p>
	 * 
	 * @param type    the type of filter that {@code filter} represents
	 * @param filter  an incremental change to the {@code validates} method
	 */
	private void addFilter( FilterType type, Object filter )
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
	 *     an object whose {@code isCase} method in Groovy accepts a
	 *     {@code SymbolicExpression} and returns {@code false} if it wants to
	 *     forbid that expression and {@code true} otherwise
	 */
	public void allowOnly( Object restriction )
	{
		addFilter( FilterType.Restriction, restriction );
	}
	
	/**
	 * <p>Incrementally change the {@code validates} method by registering an
	 * exception that will expand the range of expressions that it will
	 * accept.</p>
	 * 
	 * @param restriction
	 *     an object whose {@code isCase} method in Groovy accepts a
	 *     {@code SymbolicExpression} and returns {@code true} if it wants to
	 *     allow that expression to bypass earlier restrictions and
	 *     {@code false} otherwise
	 */
	public void allowAlso( Object restriction )
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
				result = result && ((Boolean)InvokerHelper.invokeMethod(
					filters.get( filterIndex ),
					"isCase",
					new Object[]{ expression }
				)).booleanValue();
				break;
			case Exception:
				result = result || ((Boolean)InvokerHelper.invokeMethod(
					filters.get( filterIndex ),
					"isCase",
					new Object[]{ expression }
				)).booleanValue();
				break;
			default:
				throw new IllegalStateException();
			}
		}
		
		return result;
	}
}