package org.codehaus.groovy.science;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import groovy.lang.Closure;


/**
 * <p>A nullary operator intended for use as a placeholder for an arbitrary
 * subexpression in a pattern {@code SymbolicExpression}.</p>
 * 
 * <p>A pattern expression is an expression that, when matched against another
 * expression using this class's {@code matchesFor} method, produces zero or
 * more possibilities for match results, which are represented as {@code Map}s.
 * A pattern expression usually includes a {@code PatternTermOperator}; if it
 * does not, it only matches itself, and it only ever produces empty match
 * results. Any nullary expression with a {@code PatternTermOperator} as its
 * operator, when used as a pattern expression, behaves just like that
 * {@code PatternTermOperator}'s own matcher {@code Closure}.</p>
 * 
 * @see org.codehaus.groovy.science.SymbolicExpression
 */
public class PatternTermOperator
{
	/**
	 * <p>An {@code Iterable} that iterates through the Cartesian product of a
	 * list of other {@code Iterable}s.</p>
	 * 
	 * <p>If any of the original {@code Iterable}s is empty, this product is
	 * also empty. Otherwise, the first element of this product is a
	 * {@code List} containing the first elements of each of the original
	 * {@code Iterable}s in the order in which they were given. The next few
	 * {@code List}s are obtained by first exhausting all elements in the first
	 * of the {@code Iterable}s (combining those values with the first elements
	 * of the other {@code Iterable}s) and then cycling through those elements
	 * again with the next element of the second iterator, and so on.</p>
	 * 
	 * <p>This might be better illustrated in the following Groovy example:</p>
	 * 
	 * {@code
	 * def iterator = new ProductOfIterables( [
	 *     [ 1, 2 ],
	 *     [ 3, 4 ],
	 *     [ 5, 6 ]
	 * ] ).iterator();
	 * 
	 * [
	 *     [ 1, 3, 5 ],
	 *     [ 2, 3, 5 ],
	 *     [ 1, 4, 5 ],
	 *     [ 2, 4, 5 ],
	 *     [ 1, 3, 6 ],
	 *     [ 2, 3, 6 ],
	 *     [ 1, 4, 6 ],
	 *     [ 2, 4, 6 ]
	 * ].each {
	 *     assert ( it == iterator.next() );
	 * };
	 * 
	 * assert !iterator.hasNext();
	 * }
	 * 
	 * <p>If one of the {@code Iterable}s in this product is modified or starts
	 * to generate a different list of values before an {@code Iterator}
	 * produced by this product has finished, the behavior of that
	 * {@code Iterator} is undefined.</p>
	 * 
	 * @param <T>
	 *     the type of value produced by each of the {@code Iterator}s in the
	 *     product
	 */
	private static class ProductOfIterables< T > implements Iterable< List< T > >
	{
		/**
		 * <p>The {@code Iterable}s in the product.</p>
		 */
		private List< Iterable< T > > innerIterables;
		
		/**
		 * <p>The number of {@code Iterable}s in the product.</p>
		 */
		private int sizeOfProduct;
		
		
		/**
		 * <p>Creates a new {@code ProductOfIterables} that is a product of the
		 * given {@code Iterable}s.</p>
		 * 
		 * @param innerIterables  the {@code Iterable}s to be in the product
		 * 
		 * @throws NullPointerException
		 *     if {@code innerIterables} is {@code null} or contains
		 *     {@code null}
		 */
		public ProductOfIterables( List< Iterable< T > > innerIterables )
		{
			if ( innerIterables == null )
				throw new NullPointerException();
			
			for ( Iterable< T > innerIterable: innerIterables )
			{
				if ( innerIterable == null )
					throw new NullPointerException();
			}
			
			
			this.innerIterables =
				new ArrayList< Iterable< T > >( innerIterables );
			
			sizeOfProduct = innerIterables.size();
		}
		
		
		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator< List< T > > iterator()
		{
			final List< Iterator< T > > innerIterators =
				new ArrayList< Iterator< T > >();
			
			final List< List< T > > iteratorValueLists =
				new ArrayList< List< T > >();
			
			final List< Integer > iteratorIndexes = new ArrayList< Integer >();
			
			final List< T > firstValue = new ArrayList< T >();
			
			
			for ( int index = 0; index < sizeOfProduct; index++ )
			{
				Iterator< T > thisIterator =
					innerIterables.get( index ).iterator();
				
				if ( !thisIterator.hasNext() )
					return new ArrayList< List< T > >().iterator();
				
				T thisIteratorFirstValue = thisIterator.next();
				
				innerIterators.add( thisIterator );
				
				List< T > iterableValue =
					new ArrayList< T >();
				iterableValue.add( thisIteratorFirstValue );
				iteratorValueLists.add( iterableValue );
				
				iteratorIndexes.add( Integer.valueOf( 0 ) );
				
				firstValue.add( thisIteratorFirstValue );
			}
			
			
			return new Iterator< List< T > >()
			{
				/**
				 * <p>A flag that is {@code true} only when {@code nextValue}
				 * actually represents either the next value this
				 * {@code Iterator} will generate or {@code null} if this
				 * {@code Iterator} has no more values.</p>
				 */
				private boolean nextIsCalculated = true;
				
				/**
				 * <p>The value that this {@code Iterator} is about to generate
				 * via its {@code next} method. This value is {@code null} if
				 * there are no more elements for this {@code Iterator} to
				 * generate (in which case {@code nextIsCalculated} is also
				 * {@code true}) or if the next value has not been calculated
				 * yet (in which case {@code nextIsCalculated} is also
				 * {@code false}).</p>
				 */
				private List< T > nextValue = firstValue;
				
				/**
				 * Calculates the next value to return from this
				 * {@code Iterator}, if necessary. 
				 */
				private void calculateNext()
				{
					if ( nextIsCalculated )
						return;
					
					
					nextValue = null;
					
					boolean incrementSucceeded = incrementIndexes();
					
					if ( incrementSucceeded )
					{
						nextValue = new ArrayList< T >();
						
						for ( int index = 0; index < sizeOfProduct; index++ )
						{
							nextValue.add( iteratorValueLists.get( index ).get(
								iteratorIndexes.get( index ).intValue()
							) );
						}
					}
					
					nextIsCalculated = true;
				}
				
				/**
				 * <p>Calculates the next combination of indexes to generate a
				 * {@code List} with, and generates any new values of the
				 * {@code innerIterables} the new combination of indexes will
				 * require.</p>
				 * 
				 * <p>If each of the {@code innerIterables} generates exactly
				 * <i>n</i> values, then the way the indexes are modified here
				 * is similar to the way the digits are modified when
				 * incrementing a {@code sizeOfProduct}-digit integer in base
				 * <i>n</i>. Hence, this operation can be seen as "incrementing"
				 * the list of indexes.</p> 
				 * 
				 * @return
				 *     {@code true} if the indexes were successfully
				 *     incremented; {@code false} otherwise
				 */
				private boolean incrementIndexes()
				{
					for ( int place = 0; place < sizeOfProduct; place++ )
					{
						Iterator< T > innerIterator =
							innerIterators.get( place );
						
						List< T > iteratorValueList =
							iteratorValueLists.get( place );
						
						int valueListSize = iteratorValueList.size();
						
						int iteratorIndex =
							iteratorIndexes.get( place ).intValue();
						
						int newIteratorIndex = iteratorIndex + 1;
						
						assert ( newIteratorIndex <= valueListSize ); 
						
						if ( newIteratorIndex != valueListSize )
						{
							iteratorIndexes.set(
								place,
								Integer.valueOf( newIteratorIndex )
							);
							
							return true;
						}
						
						if ( innerIterator.hasNext() )
						{
							iteratorValueList.add( innerIterator.next() );
							
							iteratorIndexes.set(
								place,
								Integer.valueOf( newIteratorIndex )
							);
							
							return true;
						}
						
						iteratorIndexes.set( place, Integer.valueOf( 0 ) );
					}
					
					return false;
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
				public List< T > next()
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
	}
	
	
	/**
	 * <p>The matcher this operator simulates.</p>
	 * 
	 * <p>A matcher is a closure that accepts a {@code SymbolicExpression} and
	 * returns either an {@code Iterable} of {@code Map}s, a single {@code Map}
	 * (as a synonym of a single-valued {@code Iterable}) or {@code null} (as a
	 * synonym of an empty {@code Iterable}). The {@code Map}s, meanwhile, can
	 * contain any information that is useful to keep track of in a pattern
	 * match using this term; they are not restricted by type.</p>
	 */
	private Closure matcher;
	
	/**
	 * <p>Creates a {@code PatternTermOperator} with the given matcher
	 * behavior.</p>
	 * 
	 * <p>A matcher is a closure that accepts a {@code SymbolicExpression} and
	 * returns either an {@code Iterable} of {@code Map}s, a single {@code Map}
	 * (as a synonym of a single-valued {@code Iterable}) or {@code null} (as a
	 * synonym of an empty {@code Iterable}). The {@code Map}s, meanwhile, can
	 * contain any information that is useful to keep track of in a pattern
	 * match using this term; they are not restricted by type.</p>
	 * 
	 * @param matcher  the matcher this operator should simulate
	 * 
	 * @throws NullPointerException  if {@code matcher} is {@code null}
	 */
	public PatternTermOperator( Closure matcher )
	{
		if ( matcher == null )
			throw new NullPointerException();
		
		this.matcher = matcher;
	}
	
	
	/**
	 * <p>Creates a pattern {@code SymbolicExpression} that uses the given
	 * matcher (by putting it in a {@code PatternTermOperator}).</p>
	 * 
	 * @param matcher
	 *     the matcher for the {@code SymbolicExpression} to simulate
	 * 
	 * @return  a {@code SymbolicExpression} that simulates the given matcher
	 * 
	 * @throws NullPointerException  if {@code matcher} is {@code null}
	 */
	public static SymbolicExpression pTerm( Closure matcher )
	{
		if ( matcher == null )
			throw new NullPointerException();
		
		return new SymbolicExpression(
			new PatternTermOperator( matcher ),
			new ArrayList< SymbolicExpression >()
		);
	}
	
	/**
	 * <p>Creates a pattern {@code SymbolicExpression} that matches any
	 * {@code SymbolicExpression} whatsoever and gives a match result with one
	 * entry mapping the given {@code name} to the {@code SymbolicExpression}
	 * that was just matched.</p>
	 * 
	 * <p>This is done by using a {@code PatternTermOperator}.</p>
	 * 
	 * @param name
	 *     the object for the pattern expression to associate with whatever
	 *     subject expression it is presented with
	 * 
	 * @return
	 *     a pattern expression that matches anything and associates it with the
	 *     given {@code name}
	 * 
	 * @throws NullPointerException  if {@code name} is {@code null}
	 */
	public static SymbolicExpression pTerm( Object name )
	{
		if ( name == null )
			throw new NullPointerException();
		
		final Object finalName = name;
		
		return new SymbolicExpression(
			new PatternTermOperator( new Closure( null ) {
				
				@SuppressWarnings("unused")
				public Map< Object, SymbolicExpression >
					doCall( SymbolicExpression expression )
				{
					if ( expression == null )
						throw new NullPointerException();
					
					Map< Object, SymbolicExpression > result =
						new HashMap< Object, SymbolicExpression >();
					
					result.put( finalName, expression );
					
					return result;
				}
			} ),
			new ArrayList< SymbolicExpression >()
		);
	}
	
	/**
	 * <p>Applies the {@code pattern} expression to the {@code subject}
	 * expression and returns an {@code Iterable} whose {@code Iterator}s will
	 * iterate through each of the possible match results.<p>
	 * 
	 * <p>Note that if any of the matchers in the pattern expression returns an
	 * {@code Iterable} of something other than {@code Map}s, this method could
	 * very well return that bad {@code Iterable}.<p>
	 * 
	 * @param pattern  a pattern {@code SymbolicExpression}
	 * @param subject  a {@code SymbolicExpression} to try to match
	 * 
	 * @return  an {@code Iterable} of the possible match results
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern} or {@code subject} is {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static Iterable< Map< ?, ? > > matchesFor(
		SymbolicExpression pattern,
		SymbolicExpression subject
	)
	{
		if (
			(pattern == null)
			||
			(subject == null)
		)
			throw new NullPointerException();
		
		Object patternOperator = pattern.getOperator();
		
		List< SymbolicExpression > subPatterns =
			pattern.getArgumentList();
		
		int numberOfArguments = subPatterns.size();
		
		if (
			(patternOperator instanceof PatternTermOperator)
			&&
			(numberOfArguments == 0)
		)
		{
			Object matches = ((PatternTermOperator)patternOperator).getMatcher()
				.call( new Object[]{ subject } );
			
			if ( matches instanceof Iterable )
				return (Iterable< Map< ?, ? > >)matches;
			
			if ( matches == null )
				return new ArrayList< Map< ?, ? > >();
			
			if ( matches instanceof Map )
			{
				List< Map< ?, ? > > result = new ArrayList< Map< ?, ? > >();
				result.add( (Map< ?, ? >)matches );
				
				return result;
			}
			
			throw new ClassCastException();
		}
		
		if ( !subject.getOperator().equals( patternOperator ) )
			return new ArrayList< Map< ?, ? > >();
		
		List< SymbolicExpression > subSubjects =
			subject.getArgumentList();
		
		if ( numberOfArguments != subSubjects.size() )
			return new ArrayList< Map< ?, ? > >();
		
		
		List< Iterable< Map< ?, ? > > > subIterables =
			new ArrayList< Iterable< Map< ?, ? > > >();
		
		for ( int index = 0; index < numberOfArguments; index++ )
		{
			subIterables.add( matchesFor(
				subPatterns.get( index ),
				subSubjects.get( index )
			) );
		}
		
		final Iterable< List< Map< ?, ? > > > possibilityIterable =
			new ProductOfIterables< Map< ?, ? > >( subIterables );
		
		
		return new Iterable< Map< ?, ? > >()
		{
			/* (non-Javadoc)
			 * @see java.lang.Iterable#iterator()
			 */
			@Override
			public Iterator< Map< ?, ? > > iterator()
			{
				return new Iterator< Map< ?, ? > >()
				{
					private Iterator< List< Map< ?, ? > > >
						possibilityIterator =
						possibilityIterable.iterator();
					
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
					private Map< ?, ? > nextValue;
					
					/**
					 * Calculates the next value to return from this
					 * {@code Iterator}, if necessary. 
					 */
					private void calculateNext()
					{
						if ( nextIsCalculated )
							return;
						
						while ( possibilityIterator.hasNext() )
						{
							Map< ?, ? > result =
								fuseMaps( possibilityIterator.next() );
							
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
					
					/**
					 * <p>Combines the given {@code Map}s to form a single map
					 * that has all of their entries. If any of the maps'
					 * entries conflict, this method returns {@code null}
					 * instead.</p>
					 * 
					 * @param maps  the {@code Map}s to combine
					 * 
					 * @return
					 *     the result of combining the given {@code Map}s, or
					 *     {@code null} if the maps had conflicting values
					 */
					private Map< ?, ? > fuseMaps( List< Map< ?, ? > > maps )
					{
						Map< Object, Object > result =
							new HashMap< Object, Object >();
						
						Set< Object > keySet = new HashSet< Object >();
						
						for ( Map< ?, ? > map: maps )
						{
							Set< Object > thisKeySet =
								new HashSet< Object >( map.keySet() );
							
							Set< Object > commonKeys =
								new HashSet< Object >( keySet );
							
							commonKeys.retainAll( thisKeySet );
							
							for ( Object key: commonKeys )
							{
								if (
									!result.get( key ).equals(
										map.get( key )
									)
								)
									return null;
							}
							
							result.putAll( map );
							keySet.addAll( thisKeySet );
						}
						
						return result;
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
					public Map< ?, ? > next()
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
	 * @see PatternTermOperator#matcher
	 * 
	 * @return  the matcher this operator should simulate
	 */
	public Closure getMatcher()
	{
		return matcher;
	}
}