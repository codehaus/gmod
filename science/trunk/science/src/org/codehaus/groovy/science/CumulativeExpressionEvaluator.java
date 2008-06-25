package org.codehaus.groovy.science;


import groovy.lang.Closure;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.codehaus.groovy.runtime.InvokerHelper;


/**
 * <p>An evaluator for {@code SymbolicExpressions} that can be built up from
 * simpler cases.</p>
 * 
 * <p>Note that when building a {@code CumulativeExpressionEvaluator}, whichever
 * behaviors are specified the latest have the highest precedence.</p>
 * 
 * <p>Also note that all objects given when building a
 * {@code CumulativeExpressionEvaluator} are used directly unless otherwise
 * noted, so if those objects are mutable, further modifications to those
 * objects will affect the evaluator's behavior.</p>
 */
public class CumulativeExpressionEvaluator extends Closure
{
	/**
	 * <p>The incremental changes made to the {@code evaluate} method, in the
	 * order those changes were registered.</p>
	 * 
	 * <p>Each of these behaviors is a closure that must accept a single
	 * {@code SymbolicExpression} and return either a non-{@code null} value
	 * that expression it thinks that expression should evaluate to or
	 * {@code null} if it has no suggestions. The behaviors are arranged in
	 * order of precedence, with the most recently added behaviors (which have
	 * the highest precedence) at the beginning of the list.</p>
	 */
	private Deque< Closure > behaviors;
	
	
	/**
	 * <p>Constructs a {@code CumulativeExpressionEvaluator} that fails to
	 * evaluate any expression whatsoever.</p>
	 */
	public CumulativeExpressionEvaluator()
	{
		super( null );
		
		behaviors = new ArrayDeque< Closure >();
	}
	
	/**
	 * <p>Constructs a {@code CumulativeExpressionEvaluator} initially tries to
	 * evaluate everything using the given behavior.</p>
	 * 
	 * @param defaultBehavior
	 *     a closure that takes a {@code CumulativeExpressionEvaluator} and
	 *     returns the result of evaluating that expression or {@code null} if
	 *     it can determine no result
	 */
	public CumulativeExpressionEvaluator( Closure defaultBehavior )
	{
		this();
		
		behaviors.addFirst( defaultBehavior );
	}
	
	/**
	 * <p>Duplicates the given {@code CumulativeExpressionEvaluator}.</p>
	 * 
	 * @param other  the {@code CumulativeExpressionEvaluator} to duplicate
	 */
	public CumulativeExpressionEvaluator( CumulativeExpressionEvaluator other )
	{
		super( null );
		
		behaviors = new ArrayDeque< Closure >( other.behaviors );
	}
	
	
	/**
	 * <p>Incrementally changes the {@code evaluates} method by registering a
	 * behavior that will override its present output in certain cases.</p>
	 * 
	 * <p>The behavior is specified as a {@code Closure} that accepts a 
	 * {@code SymbolicExpression} and returns a non-{@code null} value to
	 * replace the current output with or {@code null} if the existing behavior
	 * should still be used.</p>
	 * 
	 * @param behavior  an incremental change to the {@code evaluates} method
	 */
	public void setBehavior( Closure behavior )
	{
		if ( behavior == null )
			throw new NullPointerException();
		
		behaviors.addFirst( behavior );
	}
	
	/**
	 * <p>Incrementally changes the {@code evaluates} method by registering a
	 * behavior that will override its present output in certain cases.</p>
	 * 
	 * <p>The behavior is specified as a {@code Closure} that accepts a 
	 * {@code SymbolicExpression} and returns a non-{@code null} value to
	 * replace the current output with or {@code null} if the existing behavior
	 * should still be used.</p>
	 * 
	 * <p>The behavior will only be overridden for a particular
	 * {@code SymbolicExpression} if the given {@code expressionCase} case
	 * object's Groovy {@code isCase} method returns {@code true} when applied
	 * to the expression.</p>
	 * 
	 * @param expressionCase
	 *     a condition that must be met for the {@code behavior} to apply
	 * 
	 * @param behavior
	 *     an incremental change to the {@code evaluates} method
	 */
	public void setBehaviorCase( Object expressionCase, Closure behavior )
	{
		setBehavior( filter( expressionCase ), behavior );
	}
	
	/**
	 * <p>Incrementally changes the {@code evaluates} method by registering a
	 * behavior that will override its present output in certain cases.</p>
	 * 
	 * <p>The behavior is specified as two {@code Closure}s. The {@code fixer}
	 * takes a {@code SymbolicExpression} and converts it into a
	 * value that the {@code behavior} can handle. The {@code behavior} takes
	 * the converted expression and returns a non-{@code null} value to replace
	 * the current output with (for the original expression} or {@code null} if
	 * the existing behavior should still be used.</p>
	 * 
	 * @param fixer
	 *     a converter that is applied to the {@code SymbolicExpression} before
	 *     calling the {@code behavior}
	 * 
	 * @param behavior  an incremental change to the {@code evaluates} method
	 */
	public void setBehavior( Closure fixer, Closure behavior )
	{
		if (
			(fixer == null)
			||
			(behavior == null)
		)
			throw new NullPointerException();
		
		final Closure finalFixer = fixer;
		final Closure finalBehavior = behavior;
		
		behaviors.addFirst( new Closure( null ) {
			
			@SuppressWarnings("unused")
            public Object doCall( SymbolicExpression expression )
			{
				Object fixedExpression =
					finalFixer.call( new Object[]{ expression } );
				
				if ( fixedExpression == null )
					return null;
				
				
				return finalBehavior.call( new Object[]{ fixedExpression } );
			}
		} );
	}

	/**
	 * <p>Incrementally changes the {@code evaluates} method by registering a
	 * behavior that will override its present output in certain cases.</p>
	 * 
	 * <p>The behavior is specified as a {@code Closure} that accepts a 
	 * {@code SymbolicExpression} and returns a non-{@code null} value to
	 * replace the current output with or {@code null} if the existing behavior
	 * should still be used.</p>
	 * 
	 * <p>The behavior will only be overridden for a particular
	 * {@code SymbolicExpression} if the Groovy {@code isCase} methods of
	 * {@code operatorCase} and {@code argumentListCase} both return
	 * {@code true} when applied to the respective parts of the expression.</p>
	 * 
	 * @param operatorCase
	 *     a condition that must apply to the {@code SymbolicExpression}'s
	 *     operator before the {@code behavior} can be called
	 * 
	 * @param argumentListCase
	 *     a condition that must apply to the {@code SymbolicExpression}'s
	 *     argument list before the {@code behavior} can be called
	 * 
	 * @param behavior  an incremental change to the {@code evaluates} method
	 */
	public void setBehaviorCase(
		Object operatorCase,
		Object argumentListCase,
		Closure behavior
	)
	{
		setBehavior(
			filter( operatorCase ),
			filter( argumentListCase ),
			behavior
		 );
	}
	
	/**
	 * <p>Incrementally changes the {@code evaluates} method by registering a
	 * behavior that will override its present output in certain cases.</p>
	 * 
	 * <p>The behavior is specified in {@code Closure}s. The
	 * {@code operatorFixer} closure and the {@code argumentListFixer} closure
	 * accept their respective parts of a {@code SymbolicExpression} and convert
	 * them into values that the {@code behavior} can handle. The
	 * {@code behavior} then accepts the resulting expression and returns a 
	 * non-{@code null} value to replace the current output with (for the
	 * original expression) or {@code null} if the existing behavior should
	 * still be used.</p>
	 * 
	 * <p>If any of the fixers returns {@code null} for a particular
	 * {@code SymbolicExpression}, the behavior does not apply to that
	 * expression.</p>
	 * 
	 * @param operatorFixer
	 *     a converter that is applied to the {@code SymbolicExpression}'s
	 *     operator before calling the {@code behavior}
	 * 
	 * @param argumentListFixer
	 *     a converter that is applied to the {@code SymbolicExpression}'s
	 *     argument list before calling the {@code behavior}
	 * 
	 * @param behavior  an incremental change to the {@code evaluates} method
	 */
	public void setBehavior(
		Closure operatorFixer,
		Closure argumentListFixer,
		Closure behavior
	)
	{
		setBehavior(
			expressionFixer( operatorFixer, argumentListFixer ),
			behavior
		);
	}
	
	/**
	 * <p>Incrementally changes the {@code evaluates} method by registering a
	 * behavior that will override its present output in certain cases.</p>
	 * 
	 * <p>The behavior is specified as a {@code Closure} that accepts a 
	 * {@code SymbolicExpression} and returns a non-{@code null} value to
	 * replace the current output with or {@code null} if the existing behavior
	 * should still be used.</p>
	 * 
	 * <p>The behavior will only be overridden for a particular
	 * {@code SymbolicExpression} if the Groovy {@code isCase} methods of
	 * {@code operatorCase} and the elements of {@code argumentCases} all return
	 * {@code true} when applied to the respective parts of the expression.</p>
	 * 
	 * @param operatorCase
	 *     a condition that must apply to the {@code SymbolicExpression}'s
	 *     operator before the {@code behavior} can be called
	 * 
	 * @param argumentCases
	 *     conditions that must apply to the {@code SymbolicExpression}'s
	 *     arguments before the {@code behavior} can be called
	 * 
	 * @param behavior  an incremental change to the {@code evaluates} method
	 */
	public void setBehaviorCase(
		Object operatorCase,
		List< Object > argumentCases,
		Closure behavior
	)
	{
		ArrayList< Closure > argumentFixers = new ArrayList< Closure >();
		
		for ( Object argumentCase: argumentCases )
		{
			argumentFixers.add( filter( argumentCase ) );
		}
		
		setBehavior(
			filter( operatorCase ),
			argumentFixers,
			behavior
		 );
	}
	
	/**
	 * <p>Incrementally changes the {@code evaluates} method by registering a
	 * behavior that will override its present output in certain cases.</p>
	 * 
	 * <p>The behavior is specified in {@code Closure}s. The
	 * {@code operatorFixer} closure and the elements of
	 * {@code argumentFixers} accept their parts of a {@code SymbolicExpression}
	 * and convert them into values that the {@code behavior} can handle. The
	 * {@code behavior} then accepts the resulting expression and returns a 
	 * non-{@code null} value to replace the current output with (for the
	 * original expression) or {@code null} if the existing behavior should
	 * still be used.</p>
	 * 
	 * <p>If any of the fixers returns {@code null} for a particular
	 * {@code SymbolicExpression}, the behavior does not apply to that
	 * expression.</p>
	 * 
	 * @param operatorFixer
	 *     a converter that is applied to the {@code SymbolicExpression}'s
	 *     operator before calling the {@code behavior}
	 * 
	 * @param argumentFixers
	 *     converters that are applied to the {@code SymbolicExpression}'s
	 *     arguments before calling the {@code behavior}
	 * 
	 * @param behavior  an incremental change to the {@code evaluates} method
	 */
	public void setBehavior(
		Closure operatorFixer,
		List< Closure > argumentFixers,
		Closure behavior
	)
	{
		setBehavior(
			operatorFixer,
			listFixer( argumentFixers ),
			behavior
		);
	}
	
	/**
	 * <p>Evaluates the given expression in the context of this
	 * {@code CumulativeExpressionEvaluator}.</p>
	 * 
	 * @param expression  the expression to evaluate
	 * 
	 * @return
	 *     the value the expression evaluates to, or {@code null} if no result
	 *     can be determined
	 */
	public Object evaluate( SymbolicExpression expression )
	{
		if ( expression == null )
			throw new NullPointerException();
		
		for ( Closure behavior: behaviors )
		{
			Object result = behavior.call( new Object[]{ expression } );
			
			if ( result != null )
				return result;
		}
		
		return null;
	}
	
	/**
	 * <p>Evaluates the given expression in the context of this
	 * {@code CumulativeExpressionEvaluator}.</p>
	 * 
	 * <p>This is an alias for {@code evaluate( SymbolicExpression )}.</p>
	 * 
	 * @see org.codehaus.groovy.science.CumulativeExpressionEvaluator#evaluate(
	 *     SymbolicExpression
	 * )
	 * 
	 * @param expression  the expression to evaluate
	 * 
	 * @return  the value the expression evaluates to
	 */
	public Object doCall( SymbolicExpression expression )
		throws MalformedExpressionException
	{
		if ( expression == null )
			throw new NullPointerException();
		
		for ( Closure behavior: behaviors )
		{
			Object result = behavior.call( new Object[]{ expression } );
			
			if ( result != null )
				return result;
		}
		
		throw new MalformedExpressionException();
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
	 * <p>Converts a case object into a closure that takes a
	 * {@code SymbolicExpression} and returns that returns its argument if
	 * Returns a closure that accepts an object and returns either that object
	 * (if the object matches the case) or {@code null} (otherwise).</p>
	 * 
	 * <p>This method is useful when calling a {@code setBehavior} method when
	 * the operator and/or one or more of the arguments do not need to be
	 * changed before the behavior is executed.</p>
	 * 
	 * <p>A call to this method is essentially the same as the Groovy expression
	 * {@code ({ it in condition ? it : null })}.</p>
	 * 
	 * @param caseValue  the case object to convert
	 * 
	 * @return  the resulting {@code Closure} version of {@code condition}
	 */
	public static Closure filter( Object caseValue )
	{
		if ( caseValue == null )
			throw new NullPointerException();
		
		final Object finalCaseValue = caseValue;
		
		return new Closure( null )
		{
			@SuppressWarnings("unused")
            public Object doCall( Object switchValue )
			{
				if ( !invokeIsCase( finalCaseValue, switchValue ) )
					return null;
				
				return switchValue;
			}
		};
	}
	
	/**
	 * <p>Converts a list of fixers into a single fixer that fixes a
	 * {@code List} of objects. A "fixer" is a {@code Closure} that accepts an
	 * object and either returns a converted version of that object or returns
	 * {@code null} (if it cannot convert that object).</p>
	 * 
	 * @param fixers  the list of individual element fixers
	 * 
	 * @return  the resulting list fixer
	 */
	public static Closure listFixer( List< Closure > fixers )
	{
		if ( fixers == null )
			throw new NullPointerException();
		
		final List< Closure > finalFixers = fixers;
		final int numberOfElements = fixers.size();
		
		return new Closure( null )
		{
			@SuppressWarnings("unused")
            public Object doCall( List< Object > subjects )
			{
				if ( subjects.size() != numberOfElements )
					throw new IllegalArgumentException();
				
				List< Object > results = new ArrayList< Object >();
				
				for ( int index = 0; index < numberOfElements; index++ )
				{
					Object result = finalFixers.get( index ).call( new Object[]{
						subjects.get( index )
					} );
					
					if ( result == null )
						return null;
					
					results.add( result );
				}
				
				return results;
			}
		};
	}
	
	// returned closure throws ClassCastException if original closures aren't
	// actually fixers
	
	/**
	 * <p>Converts an operation fixer and an argument list fixer into a single
	 * fixer that fixes a {@code SymbolicExpression}. A "fixer" is a
	 * {@code Closure} that accepts an object and either returns a converted
	 * version of that object or returns {@code null} (if it cannot convert that
	 * object).</p>
	 * 
	 * <p>If the given {@code Closure}s are not valid fixers of the appropriate
	 * types, calling the fixer returned by this method might result in an
	 * exception.</p>
	 * 
	 * @param operatorFixer
	 *     the fixer to apply to the {@code SymbolicExpression}'s operator
	 * 
	 * @param argumentListFixer
	 *     the fixer to apply to the {@code SymbolicExpression}'s argument list
	 * 
	 * @return  the resulting {@code SymbolicExpression} fixer
	 */
	public static Closure expressionFixer(
		Closure operatorFixer,
		Closure argumentListFixer
	)
	{
		if (
			(operatorFixer == null)
			||
			(argumentListFixer == null)
		)
			throw new NullPointerException();
		
		final Closure finalOperatorFixer = operatorFixer;
		final Closure finalArgumentListFixer = argumentListFixer;
		
		return new Closure( null )
		{
			@SuppressWarnings({ "unused", "unchecked" })
            public Object doCall( SymbolicExpression subject )
			{
				Object resultOperator = finalOperatorFixer.call( new Object[]{
					subject.getOperator()
				} );
				
				if ( resultOperator == null )
					return null;
				
				
				List< SymbolicExpression > resultArgumentList =
					(List< SymbolicExpression >)finalArgumentListFixer.call(
						new Object[]{ subject.getArgumentList() }
					);
				
				if ( resultArgumentList == null )
					return null;
				
				
				return new SymbolicExpression(
					resultOperator,
					resultArgumentList
				);
			}
		};
	}
	
	/**
	 * <p>Converts a {@code Closure} into a {@code Closure} that takes a
	 * {@code SymbolicExpression} and returns the result of passing the
	 * expression's arguments to the original {@code Closure}.</p>
	 * 
	 * <p>If the {@code Closure} returned by this method is called with a
	 * {@code SymbolicExpression} whose arguments cannot be passed to the
	 * original {@code Closure}, an exception will be thrown.</p>
	 * 
	 * @param simpleClosure  the {@code Closure} to convert
	 * 
	 * @return  the converted form of {@code simpleClosure}
	 */
	public static Closure withArgs( Closure simpleClosure )
	{
		if ( simpleClosure == null )
			throw new NullPointerException();
		
		final Closure finalSimpleClosure = simpleClosure; 
		
		return new Closure( null )
		{
			@SuppressWarnings("unused")
            public SymbolicExpression doCall(
				SymbolicExpression expression
			)
			{
				return (SymbolicExpression)finalSimpleClosure.call(
					expression.getArgumentList().toArray()
				); 
			}
		};
	}
}