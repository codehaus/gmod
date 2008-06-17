package org.codehaus.groovy.science;


import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.runtime.InvokerHelper;


/**
 * <p>An {@code ExpressionValidator} that can be built up from simpler cases.</p>
 * 
 * <p>Note that when building a {@code CumulativeExpressionValidator} using
 * {@code allowOnly( Object )} and {@code allowAlso( Object )}, the operations
 * must be performed in a careful order. Whichever restrictions and exceptions
 * have been registered the latest have the highest precedence.</p>
 * 
 * <p>Also note that all case objects given when building a
 * {@code CumulativeExpressionValidator} are used directly, so if those objects
 * are not immutable, further modifications to those objects will affect the
 * validator's behavior.</p>
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
	 * <p>The default value to return when validating an expression that is
	 * neither explicitly accepted nor explicitly rejected by any of the
	 * filters.</p>
	 */
	private boolean defaultValue;
		
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
		this( true );
	}
	
	/**
	 * <p>Constructs a {@code CumulativeExpressionValidator} that either accepts
	 * all expressions or rejects all expressions.</p>
	 * 
	 * @param defaultValue
	 *     {@code true} if the new {@code CumulativeExpressionValidator} should
	 *     initially accept all expressions; {@code false} if it should reject
	 *     all expressions
	 */
	public CumulativeExpressionValidator( boolean defaultValue )
	{
		this.defaultValue = defaultValue;
		
		filters      = new ArrayList< Object >();
		filterTypes  = new ArrayList< FilterType >();
	}
	
	/**
	 * <p>Duplicates the given {@code CumulativeExpressionValidator}.</p>
	 * 
	 * @param other  the {@code CumulativeExpressionValidator} to duplicate
	 */
	public CumulativeExpressionValidator( CumulativeExpressionValidator other )
	{
		defaultValue = other.defaultValue;
		
		filters      = new ArrayList< Object >(      other.filters      );
		filterTypes  = new ArrayList< FilterType >(  other.filterTypes  );
	}
	
	/**
	 * <p>Constructs a {@code CumulativeExpressionValidator} that accepts
	 * exactly those expressions that cause the given object's {@code isCase}
	 * method in Groovy to return true.</p>
	 * 
	 * @param defaultValue  the initial behavior of the {@code validates} method
	 */
	public CumulativeExpressionValidator( Object defaultValue )
	{
		this( true );
		
		if ( defaultValue instanceof Boolean )
		{
			this.defaultValue = ((Boolean)defaultValue).booleanValue();
		}
		else
		{
			allowOnly( defaultValue );
		}
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
	 */
	@Override
	public boolean validates( SymbolicExpression expression )
	{
		boolean result = defaultValue;
		
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
	
	/**
	 * <p>Checks the given expression against this
	 * {@code CumulativeExpressionValidator}'s criteria and returns whether the
	 * criteria are satisfied.</p>
	 * 
	 * <p>This is an alias for {@code validates( SymbolicExpression )}.</p>
	 * 
	 * @see org.codehaus.groovy.science.ExpressionValidator#validates(
	 *     org.codehaus.groovy.science.SymbolicExpression
	 * )
	 * 
	 * @param switchValue  the expression to validate
	 * 
	 * @return
	 *     {@code true} if the given expression fits the criteria; {@code false}
	 *     otherwise
	 */
	public boolean isCase( SymbolicExpression switchValue )
	{
		return validates( switchValue );
	}
	
	/**
	 * <p>Returns a new {@code CumulativeExpressionValidator} that accepts
	 * all of the expressions this validator accepts as well as all expressions
	 * accepted by the given object's {@code isCase} method in Groovy.</p>
	 * 
	 * @param other  the case to take a disjunction with
	 * 
	 * @return
	 *     the result of taking the disjunction of this validator and the given
	 *     case
	 */
	public CumulativeExpressionValidator or( Object other )
	{
		CumulativeExpressionValidator result =
			new CumulativeExpressionValidator( this );
		
		result.allowAlso( other );
		
		return result;
	}
	
	/**
	 * <p>Returns a new {@code CumulativeExpressionValidator} that accepts
	 * exactly those expressions this validator accepts that are also accepted
	 * by the given object's {@code isCase} method in Groovy.</p>
	 * 
	 * @param other  the case to take a conjunction with
	 * 
	 * @return
	 *     the result of taking the conjunction of this validator and the given
	 *     case
	 */
	public CumulativeExpressionValidator and( Object other )
	{
		CumulativeExpressionValidator result =
			new CumulativeExpressionValidator( this );
		
		result.allowOnly( other );
		
		return result;
	}
	
	/**
	 * <p>Returns a new {@code CumulativeExpressionValidator} that accepts
	 * exactly those expressions with are accepted by this validator or the
	 * given object's {@code isCase} method in Groovy, but not both.</p>
	 * 
	 * @param other  the case to take a symmetric difference with
	 * 
	 * @return
	 *     the result of taking the symmetric difference of this validator and
	 *     the given case
	 */
	public CumulativeExpressionValidator xor( Object other )
	{
		final CumulativeExpressionValidator self = this;
		final Object that = other;
		
		return new CumulativeExpressionValidator( new Object() {
			
			@SuppressWarnings("unused")
            public boolean isCase( SymbolicExpression switchValue )
			{
				return (
					self.validates( switchValue )
					^
					((Boolean)InvokerHelper.invokeMethod(
						that,
						"isCase",
						new Object[]{ switchValue }
					)).booleanValue()
				);
			}
		} );
	}
	
	/**
	 * <p>Returns a new {@code CumulativeExpressionValidator} that accepts
	 * exactly those expressions this validator does not accept.</p>
	 * 
	 * @return  the result of taking the complement of this validator
	 */
	public CumulativeExpressionValidator bitwiseNegate()
	{
		final CumulativeExpressionValidator self = this;
		
		return new CumulativeExpressionValidator( new Object() {
			
			@SuppressWarnings("unused")
            public boolean isCase( SymbolicExpression switchValue )
			{
				return( !self.validates( switchValue ) );
			}
		} );
	}
}