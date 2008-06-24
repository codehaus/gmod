package org.codehaus.groovy.science;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import groovy.lang.Closure;


/**
 * <p>A nullary operator intended for use as a placeholder for a calculated
 * subexpression in the replacement {@code SymbolicExpression} of a pattern
 * search-and-replace.</p>
 * 
 * <p>A replacement expression is an expression that, when applied to a match
 * result using this class's {@code replacementFor} method, produces either
 * a {@code SymbolicExpression} to use as a search-and-replace result or
 * {@code null} to indicate it cannot generate a result expression for that
 * match result. A replacement expression oftentimes includes a
 * {@code ReplacementTermOperator}; if it does not, it only produces itself, no
 * matter what the match result was. Any nullary expression with a
 * {@code ReplacementTermOperator} as its operator, when used as a replacement
 * expression, behaves just like that {@code ReplacementTermOperator}'s own
 * replacer {@code Closure}.</p>
 * 
 * @see org.codehaus.groovy.science.SymbolicExpression
 */
public class ReplacementTermOperator
{
	/**
	 * <p>The replacer this operator simulates.</p>
	 * 
	 * <p>A replacer is a closure that accepts a {@code Map} and either returns
	 * a {@code SymbolicExpression} or returns {@code null} if no appropriate
	 * expression can be built based on that {@code Map}. The {@code Map} is
	 * usually one of the results of matching a pattern against some other
	 * {@code SymbolicExpression}.</p>
	 */
	private Closure replacer;
	
	/**
	 * <p>Creates a {@code ReplacementTermOperator} with the given replacer
	 * behavior.</p>
	 * 
	 * <p>A replacer is a closure that accepts a {@code Map} and either returns
	 * a {@code SymbolicExpression} or returns {@code null} if no appropriate
	 * expression can be built based on that {@code Map}. The {@code Map} is
	 * usually one of the results of matching a pattern against some other
	 * {@code SymbolicExpression}.</p>
	 * 
	 * @param replacer  the replacer this operator should simulate
	 * 
	 * @throws NullPointerException  if {@code replacer} is {@code null}
	 */
	public ReplacementTermOperator( Closure replacer )
	{
		if ( replacer == null )
			throw new NullPointerException();
		
		this.replacer = replacer;
	}
	
	
	/**
	 * <p>Creates a replacement {@code SymbolicExpression} that uses the given
	 * replacer (by putting it in a {@code ReplacementTermOperator}).</p>
	 * 
	 * @param replacer
	 *     the replacer for the {@code SymbolicExpression} to simulate
	 * 
	 * @return  a {@code SymbolicExpression} that simulates the given replacer
	 * 
	 * @throws NullPointerException  if {@code replacer} is {@code null}
	 */
	public static SymbolicExpression rTerm( Closure replacer )
	{
		if ( replacer == null )
			throw new NullPointerException();
		
		return new SymbolicExpression(
			new ReplacementTermOperator( replacer ),
			new ArrayList< SymbolicExpression >()
		);
	}
	
	/**
	 * <p>Creates a replacement {@code SymbolicExpression} that produces a
	 * {@code SymbolicExpression} that is already present in the match result by
	 * dereferencing the match result with the given {@code name}. If the
	 * {@code name} is not associated with a {@code SymbolicExpression} in the
	 * match result, this replacement expression will fail to produce
	 * anything.</p>
	 * 
	 * <p>This is done by using a {@code ReplacementTermOperator}.</p>
	 * 
	 * @param name
	 *     the object the replacement expression should hope to find associated
	 *     with the {@code SymbolicExpression} it needs to produce
	 * 
	 * @return
	 *     a replacement expression that always produces the
	 *     {@code SymbolicExpression} associated with the given {@code name} if
	 *     that expression exists
	 * 
	 * @throws NullPointerException  if {@code name} is {@code null}
	 */
	public static SymbolicExpression rTerm( Object name )
	{
		if ( name == null )
			throw new NullPointerException();
		
		final Object finalName = name;
		
		return new SymbolicExpression(
			new ReplacementTermOperator( new Closure( null ) {
				
				@SuppressWarnings("unused")
				public SymbolicExpression doCall( Map< ?, ? > matchInformation )
				{
					if ( matchInformation == null )
						throw new NullPointerException();
					
					try
					{
						if ( !matchInformation.containsKey( finalName ) )
							return null;
					}
					catch ( ClassCastException e )
					{
						return null;
					}
					
					Object result = matchInformation.get( finalName );
					
					if ( result instanceof SymbolicExpression )
						return (SymbolicExpression)result;
					
					return null;
				}
			} ),
			new ArrayList< SymbolicExpression >()
		);
	}
	
	/**
	 * <p>Generates the result of using the {@code replacement}
	 * {@code SymbolicExpression} given the specified {@code matchInformation}.
	 * If the replacement expression fails to produce a result expression, this
	 * method returns {@code null}.</p>
	 * 
	 * @param matchInformation
	 *     the result of applying a pattern expression to a subject expression
	 * 
	 * @param replacement
	 *     the replacement expression associated with the presumed pattern
	 *     expression
	 * 
	 * @return  the result expression, if that exists; {@code null} otherwise
	 * 
	 * @throws NullPointerException
	 *     if {@code matchInformation} or {@code replacement} is {@code null}
	 */
	public static SymbolicExpression replacementFor(
		Map< ?, ? > matchInformation,
		SymbolicExpression replacement
	)
	{
		if (
			(matchInformation == null)
			||
			(replacement == null)
		)
			throw new NullPointerException();
		
		Object replacementOperator = replacement.getOperator();
		
		List< SymbolicExpression > subReplacements =
			replacement.getArgumentList();
		
		int numberOfArguments = subReplacements.size();
		
		if (
			(replacementOperator instanceof ReplacementTermOperator)
			&&
			(numberOfArguments == 0)
		)
		{
			return (
				(SymbolicExpression)
				((ReplacementTermOperator)replacementOperator).getReplacer()
					.call( new Object[]{ matchInformation } )
			);
		}
		
		
		List< SymbolicExpression > newArgumentList =
			new ArrayList< SymbolicExpression >();
		
		for ( SymbolicExpression oldArgument: replacement.getArgumentList() )
		{
			SymbolicExpression newArgument =
				replacementFor( matchInformation, oldArgument );
			
			if ( newArgument == null )
				return null;
			
			newArgumentList.add( newArgument );
		}
		
		
		return new SymbolicExpression( replacementOperator, newArgumentList );
	}
	
	/**
	 * <p>Performs a pattern search-and-replace on the given subject expression,
	 * and returns an {@code Iterable} of the possible result expressions.<p>
	 * 
	 * @param pattern
	 *     the pattern expression to match against {@code subject}
	 * 
	 * @param replacement
	 *     the replacement expression to apply to each of the results of
	 *     matching {@code pattern} against {@code subject}
	 * 
	 * @param subject
	 *     the expression to perform the pattern search-and-replace on
	 * 
	 * @return
	 *     an {@code Iterable} over the possible results of the pattern search-
	 *     and-replace.
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern}, {@code replacement}, or {@code subject} is
	 *     {@code null}
	 */
	public static Iterable< SymbolicExpression > replacementsFor(
		SymbolicExpression pattern,
		SymbolicExpression replacement,
		SymbolicExpression subject
	)
	{
		if (
			(pattern == null)
			||
			(replacement == null)
			||
			(subject == null)
		)
			throw new NullPointerException();
		
		final SymbolicExpression finalReplacement = replacement;
		
		final Iterable< Map< ?, ? > > matches =
			PatternTermOperator.matchesFor( pattern, subject );
		
		return new Iterable< SymbolicExpression >()
		{
			/* (non-Javadoc)
			 * @see java.lang.Iterable#iterator()
			 */
			@Override
			public Iterator< SymbolicExpression > iterator()
			{
				return new Iterator< SymbolicExpression >()
				{
					/**
					 * <p>An {@code Iterator} over all of the possible results
					 * of matching {@code pattern} against
					 * {@code expression}.</p>
					 */
					private Iterator< Map< ?, ? > > matchIterator =
						matches.iterator();
					
					/**
					 * <p>A flag that is {@code true} only when
					 * {@code nextValue} actually represents either the next
					 * value this {@code Iterator} will generate or {@code null}
					 * if this {@code Iterator} has no more values.</p>
					 */
					private boolean nextIsCalculated = false;
					
					/**
					 * <p>The value that this {@code Iterator} is about to
					 * generate via its {@code next} method. This value is
					 * {@code null} if there are no more elements for this
					 * {@code Iterator} to generate (in which case
					 * {@code nextIsCalculated} is also {@code true}) or if the
					 * next value has not been calculated yet (in which case
					 * {@code nextIsCalculated} is also {@code false}).</p>
					 */
					private SymbolicExpression nextValue;
					
					
					/**
					 * Calculates the next value to return from this
					 * {@code Iterator}, if necessary. 
					 */
					private void calculateNext()
					{
						if ( nextIsCalculated )
							return;
						
						while ( matchIterator.hasNext() )
						{
							SymbolicExpression result = replacementFor(
								matchIterator.next(),
								finalReplacement
							);
							
							if ( result != null )
							{
								nextValue = result;
								nextIsCalculated = true;
								
								return;
							}
						}
						
						nextValue = null;
						nextIsCalculated = true;
					}
					
					/* (non-Javadoc)
					 * @see java.util.Iterator#hasNext()
					 */
					@Override
					public boolean hasNext()
					{
						calculateNext();
						
						return (nextValue != null);
					}
					
					/* (non-Javadoc)
					 * @see java.util.Iterator#next()
					 */
					@Override
					public SymbolicExpression next()
					{
						calculateNext();
						
						if ( nextValue == null )
							throw new NoSuchElementException();
						
						nextIsCalculated = false;
						
						return nextValue;
					}
					
					/* (non-Javadoc)
					 * @see java.util.Iterator#remove()
					 */
					@Override
					public void remove()
					{
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
	
	
	/**
	 * @see ReplacementTermOperator#matcher
	 * 
	 * @return  the replacer this operator should simulate
	 */
	public Closure getReplacer()
	{
		return replacer;
	}
}