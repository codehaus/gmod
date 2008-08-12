package org.codehaus.groovy.science;


import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.runtime.InvokerHelper;


/**
 * <p>A validator for {@code SymbolicExpressions} that can be built up from
 * simpler cases.</p>
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
public class CumulativeExpressionValidator
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
	 * <p>Invokes the method {@code isCase} on the given {@code caseValue} using
	 * the Groovy runtime, testing the {@code switchValue} to see if it would
	 * match the {@code caseValue} in a Groovy {@code switch} statement.</p>
	 * 
	 * @param caseValue    the object representing the case being matched
	 * @param switchValue  the object representing the value being switched on
	 * 
	 * @return
	 *     {@code true} if the {@code switchValue} matches the
	 *     {@code caseValue}; {@code false} otherwise
	 */
	private static boolean invokeIsCase( Object caseValue, Object switchValue )
	{
		return ((Boolean)InvokerHelper.invokeMethod(
			caseValue,
			"isCase",
			new Object[]{ switchValue }
		)).booleanValue();
	}
	
	/**
	 * <p>Incrementally changes the {@code validates} method by registering a
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
	 * <p>Incrementally changes the {@code validates} method by registering a
	 * filter that will override its present output in certain cases.</p>
	 * 
	 * <p>If {@code type} is {@code Restriction}, {@code filterPattern} is a
	 * pattern expression that the candidate {@code SymbolicExpression}s must
	 * match in order to validate.</p>
	 * 
	 * <p>If {@code type} is {@code Exception}, {@code filterPattern} is a
	 * pattern expression that matches a {@code SymbolicExpression} if it wants
	 * to allow that expression to bypass earlier restrictions.</p>
	 * 
	 * @param type
	 *     the type of filter that {@code filter} represents
	 * 
	 * @param filterPattern
	 *     an incremental change to the {@code validates} method
	 */
	private void addFilter( FilterType type, SymbolicExpression filterPattern )
	{
		if ( filterPattern == null )
			throw new NullPointerException();
		
		
		final SymbolicExpression finalFilterPattern = filterPattern;
		
		filters.add( new Object() {
			
			@SuppressWarnings("unused")
            public boolean isCase( SymbolicExpression switchValue )
			{
				return PatternTermOperator.matchesExistFor(
					finalFilterPattern,
					switchValue
				);
			}
		} );
		
		filterTypes.add( type );
	}
	
	/**
	 * <p>Incrementally changes the {@code validates} method by registering a
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
	 * <p>Incrementally changes the {@code validates} method by registering a
	 * restriction that will narrow the range of expressions that it will
	 * accept.</p>
	 * 
	 * @see PatternTermOperator
	 * 
	 * @param restrictionPattern
	 *     a pattern expression that fails to match a {@code SymbolicExpression}
	 *     if it wants to forbid that expression and matches it otherwise
	 */
	public void allowOnly( SymbolicExpression restrictionPattern )
	{
		addFilter( FilterType.Restriction, restrictionPattern );
	}
	
	/**
	 * <p>Incrementally changes the {@code validates} method by registering an
	 * exception that will expand the range of expressions that it will
	 * accept.</p>
	 * 
	 * @param exception
	 *     an object whose {@code isCase} method in Groovy accepts a
	 *     {@code SymbolicExpression} and returns {@code true} if it wants to
	 *     allow that expression to bypass earlier restrictions and
	 *     {@code false} otherwise
	 */
	public void allowAlso( Object exception )
	{
		addFilter( FilterType.Exception, exception );
	}
	
	/**
	 * <p>Incrementally changes the {@code validates} method by registering an
	 * exception that will expand the range of expressions that it will
	 * accept.</p>
	 * 
	 * @see PatternTermOperator
	 * 
	 * @param exceptionPattern
	 *     a pattern expression that matches a {@code SymbolicExpression} if it
	 *     wants to allow that expression to bypass earlier restrictions and
	 *     doesn't match it otherwise
	 */
	public void allowAlso( SymbolicExpression exceptionPattern )
	{
		addFilter( FilterType.Exception, exceptionPattern );
	}
	
	/**
	 * <p>Incrementally changes the {@code validates} method by also allowing,
	 * out of all of the {@code SymbolicExpression}s with a given kind of
	 * operator and a particular number of arguments, those expressions whose
	 * arguments satisfy a given list of individual conditions.</p>
	 * 
	 * <p>For instance, in Groovy, an addition operator {@code additionOp}, a
	 * subtraction operator {@code subtractionOp}, and a multiplication operator
	 * {@code multiplicationOp} might be allowed for a
	 * {@code CumulativeExpressionValidator} {@code numberContext} using the
	 * following code:</p>
	 * 
	 * {@code
	 * numberContext.allowAlso(
	 *    [ additionOp, subtractionOp, multiplicationOp ],
	 *    [ numberContext, numberContext ]
	 * );
	 * }
	 * 
	 * <p>Likewise, if a function {@code isObviouslyZero} is defined on
	 * {@code SymbolicExpression}s, a division operator might be allowed in the
	 * following way in order to avoid blatant division by zero:</p>
	 * 
	 * {@code
	 * numberContext.allowAlso(
	 *    divisionOp,
	 *    [ numberContext, numberContext & { !isObviouslyZero( it ) } ]
	 * );
	 * }
	 * 
	 * @param operatorCase
	 *     an object whose Groovy {@code isCase} method must return {@code true}
	 *     when given a {@code SymbolicExpression}'s operator in order for this
	 *     filter to apply to that expression
	 * 
	 * @param argumentCases
	 *     a list of objects whose Groovy {@code isCase} methods must all return
	 *     {@code true} when given the corresponding arguments of a
	 *     {@code SymbolicExpression} in order for this filter to allow that
	 *     expression to bypass earlier restrictions
	 */
	public void allowAlso( Object operatorCase, List< Object > argumentCases )
	{
		final Object finalOperatorCase = operatorCase;
		final List< Object > finalArgumentCases = argumentCases;
		final int numberOfArguments = argumentCases.size();
		
		allowAlso( new Object() {
			
			@SuppressWarnings("unused")
            public boolean isCase( SymbolicExpression switchValue )
			{
				List< SymbolicExpression > argumentList =
					switchValue.getArgumentList();
				
				// This filter should have no effect on whether this validator
				// accepts expressions with other root operators or root
				// arities.
				if ( !(
					argumentList.size() == numberOfArguments
					&&
					invokeIsCase( finalOperatorCase, switchValue.getOperator() )
				) )
					return false;
				
				for (
					int argumentIndex = 0;
					argumentIndex < numberOfArguments;
					argumentIndex++
				)
				{
					if ( !invokeIsCase(
    					finalArgumentCases.get( argumentIndex ),
    					argumentList.get( argumentIndex )
					) )
						return false;
				}
				
				return true;
			}
		} );
	}

	/**
	 * <p>Incrementally changes the {@code validates} method by also allowing,
	 * out of all of the {@code SymbolicExpression}s with a given kind of
	 * operator, those expressions whose argument lists satisfy a given
	 * condition.</p>
	 * 
	 * <p>For instance, in Groovy, an addition operator {@code additionOp} that
	 * can take any number of numeric arguments might be allowed for a
	 * {@code CumulativeExpressionValidator} {@code numberContext} using the
	 * following code:</p>
	 * 
	 * {@code
	 * numberContext.allowAlso(
	 *    additionOp,
	 *    { it.every { it in numberContext } }
	 * );
	 * }
	 * 
	 * <p>This can be done for multiple operators at once:</p>
	 * 
	 * {@code
	 * numberContext.allowAlso(
	 *    [ additionOp, multiplicationOp ],
	 *    { it.every { it in numberContext } }
	 * );
	 * }
	 * 
	 * @param operatorCase
	 *     an object whose Groovy {@code isCase} method must return {@code true}
	 *     when given a {@code SymbolicExpression}'s operator in order for this
	 *     filter to apply to that expression
	 * 
	 * @param argumentCases
	 *     an object whose Groovy {@code isCase} method must return {@code true}
	 *     when given the argument list of a {@code SymbolicExpression} in order
	 *     for this filter to allow that expression to bypass earlier
	 *     restrictions
	 */
	public void allowAlso( Object operatorCase, Object argumentListCase )
	{
		final Object finalOperatorCase = operatorCase;
		final Object finalArgumentListCase = argumentListCase;
		
		allowAlso( new Object() {
			
			@SuppressWarnings("unused")
            public boolean isCase( SymbolicExpression switchValue )
			{
				
				// This filter should have no effect on whether this validator
				// accepts expressions with other root operators.
				if ( !invokeIsCase(
					finalOperatorCase,
					switchValue.getOperator()
				) )
					return false;
				
				return invokeIsCase(
					finalArgumentListCase,
					switchValue.getArgumentList()
				);
			}
		} );
	}
	
	/**
	 * <p>Incrementally changes the {@code validates} method by only allowing,
	 * out of all of the {@code SymbolicExpression}s with a given kind of
	 * operator and a particular number of arguments, those expressions whose
	 * arguments satisfy a given list of individual conditions.</p>
	 * 
	 * <p>For instance, in Groovy, if a function {@code isObviouslyZero} is
	 * defined on {@code SymbolicExpression}s, a division operator
	 * {@code divisionOp} might be allowed and restricted in the following way
	 * so as to avoid blatant division by zero:</p>
	 * 
	 * {@code
	 * numberContext.allowAlso(
	 *    divisionOp,
	 *    [ numberContext, numberContext ]
	 * );
	 * numberContext.allowOnly(
	 *    divisionOp,
	 *    [ true, { !isObviouslyZero( it ) } ]
	 * );
	 * }
	 * 
	 * <p>Note that this kind of restriction does <em>not</em> prevent this
	 * validator from accepting expressions that have root operators other than
	 * {@code divisionOp}, and it will <em>not</em> prevent {@code divisionOp}
	 * from being applied to a number of arguments other than two (if that kind
	 * of usage has been allowed). The operator condition and the number of
	 * argument conditions given must match an expression for this filter to
	 * even apply to that expression. This is to make it easy to apply
	 * restrictions to operators one at a time.</p>
	 * 
	 * @param operatorCase
	 *     an object whose Groovy {@code isCase} method must return {@code true}
	 *     when given a {@code SymbolicExpression}'s operator in order for this
	 *     filter to apply to that expression
	 * 
	 * @param argumentCases
	 *     a list of objects whose Groovy {@code isCase} methods must return at
	 *     least one {@code false} when given the corresponding arguments of a
	 *     {@code SymbolicExpression} in order for this filter to forbid that
	 *     expression
	 */
	public void allowOnly( Object operatorCase, List< Object > argumentCases )
	{
		final Object finalOperatorCase = operatorCase;
		final List< Object > finalArgumentCases = argumentCases;
		final int numberOfArguments = argumentCases.size();
		
		allowAlso( new Object() {
			
			@SuppressWarnings("unused")
            public boolean isCase( SymbolicExpression switchValue )
			{
				List< SymbolicExpression > argumentList =
					switchValue.getArgumentList();
				
				// This filter should have no effect on whether this validator
				// accepts expressions with other root operators or root
				// arities.
				if ( !(
					argumentList.size() == numberOfArguments
					&&
					invokeIsCase( finalOperatorCase, switchValue.getOperator() )
				) )
					return true;
				
				for (
					int argumentIndex = 0;
					argumentIndex < numberOfArguments;
					argumentIndex++
				)
				{
					if ( !invokeIsCase(
							finalArgumentCases.get( argumentIndex ),
							argumentList.get( argumentIndex )
					) )
						return false;
				}
				
				return true;
			}
		} );
	}
	
	/**
	 * <p>Incrementally changes the {@code validates} method by only allowing,
	 * out of all of the {@code SymbolicExpression}s with a given kind of
	 * operator and a particular number of arguments, those expressions whose
	 * argument lists satisfy a given condition.</p>
	 * 
	 * <p>For instance, in Groovy, if a sorting function {@code sortExpressions}
	 * is defined on immutable {@code List}s of {@code SymbolicExpression}s, an
	 * addition operator {@code additionOp} might be allowed and restricted in
	 * the following way so as to allow it to have any number of numeric
	 * arguments and to enforce that its arguments are always sorted
	 * correctly:</p>
	 * 
	 * {@code
	 * niceNumberContext.allowAlso(
	 *    additionOp,
	 *    { it.every { it in niceNumberContext } }
	 * );
	 * niceNumberContext.allowOnly(
	 *    additionOp,
	 *    { it == sortExpressions( it ) }
	 * );
	 * }
	 * 
	 * <p>Note that this kind of restriction does <em>not</em> prevent this
	 * validator from accepting expressions that have root operators other than
	 * {@code additionOp}. The operator condition must match an expression for
	 * this filter to even apply to that expression. This is to make it easy to
	 * apply restrictions to operators one at a time.</p>
	 * 
	 * @param operatorCase
	 *     an object whose Groovy {@code isCase} method must return {@code true}
	 *     when given a {@code SymbolicExpression}'s operator in order for this
	 *     filter to apply to that expression
	 * 
	 * @param argumentCases
	 *     an object whose Groovy {@code isCase} method must return
	 *     {@code false} when given the argument list of a
	 *     {@code SymbolicExpression} in order for this filter to forbid that
	 *     expression
	 */
	public void allowOnly( Object operatorCase, Object argumentListCase )
	{
		final Object finalOperatorCase = operatorCase;
		final Object finalArgumentListCase = argumentListCase;
		
		allowAlso( new Object() {
			
			@SuppressWarnings("unused")
            public boolean isCase( SymbolicExpression switchValue )
			{
				
				// This filter should have no effect on whether this validator
				// accepts expressions with other root operators.
				if ( !invokeIsCase(
					finalOperatorCase,
					switchValue.getOperator()
				) )
					return true;
				
				return invokeIsCase(
					finalArgumentListCase,
					switchValue.getArgumentList()
				);
			}
		} );
	}
	
	/**
	 * <p>Checks the given expression against this
	 * {@code CumulativeExpressionValidator}'s criteria and returns whether the
	 * criteria are satisfied.</p>
	 * 
	 * @param expression  the expression to validate
	 * 
	 * @return
	 *     {@code true} if the given expression fits the criteria; {@code false}
	 *     otherwise
	 */
	public boolean validates( SymbolicExpression expression )
	{
		boolean result = defaultValue;
		
		for ( int filterIndex = 0; filterIndex < filters.size(); filterIndex++ )
		{
			switch ( filterTypes.get( filterIndex ) )
			{
			case Restriction:
				result = result && invokeIsCase(
					filters.get( filterIndex ),
					expression
				);
				break;
			case Exception:
				result = result || invokeIsCase(
					filters.get( filterIndex ),
					expression
				);
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
	 * @see org.codehaus.groovy.science.CumulativeExpressionValidator#validates(
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
	 * <p>Note that further changes to this
	 * {@code CumulativeExpressionValidator} and/or the {@code other} case
	 * object after this method is called will affect the behavior of the
	 * returned {@code CumulativeExpressionValidator}.</p>
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
			new CumulativeExpressionValidator( false );
		
		result.allowAlso( this );
		result.allowAlso( other );
		
		return result;
	}
	
	/**
	 * <p>Returns a new {@code CumulativeExpressionValidator} that accepts
	 * exactly those expressions this validator accepts that are also accepted
	 * by the given object's {@code isCase} method in Groovy.</p>
	 * 
	 * <p>Note that further changes to this
	 * {@code CumulativeExpressionValidator} and/or the {@code other} case
	 * object after this method is called will affect the behavior of the
	 * returned {@code CumulativeExpressionValidator}.</p>
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
			new CumulativeExpressionValidator( true );
		
		result.allowOnly( this );
		result.allowOnly( other );
		
		return result;
	}
	
	/**
	 * <p>Returns a new {@code CumulativeExpressionValidator} that accepts
	 * exactly those expressions with are accepted by this validator or the
	 * given object's {@code isCase} method in Groovy, but not both.</p>
	 * 
	 * <p>Note that further changes to this
	 * {@code CumulativeExpressionValidator} and/or the {@code other} case
	 * object after this method is called will affect the behavior of the
	 * returned {@code CumulativeExpressionValidator}.</p>
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
					invokeIsCase( that, switchValue )
				);
			}
		} );
	}
	
	/**
	 * <p>Returns a new {@code CumulativeExpressionValidator} that accepts
	 * exactly those expressions this validator does not accept.</p>
	 * 
	 * <p>Note that further changes to this
	 * {@code CumulativeExpressionValidator} after this method is called will
	 * affect the behavior of the returned
	 * {@code CumulativeExpressionValidator}.</p>
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