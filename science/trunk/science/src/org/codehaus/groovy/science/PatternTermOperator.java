package org.codehaus.groovy.science;


import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.Closure;


// TODO: Reduce some of the code reuse here by factoring out some common
// {@code Iterator} functionality, and move some of these inner classes and
// utility methods into their own files, if it makes sense to do so.


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
 * <p>The {@code name} of a {@code PatternTermOperator} can be anything, but it
 * is intended to be used to signify the object that the pattern term will
 * associate in the match result with whatever {@code SymbolicExpression} it has
 * matched.</p>
 * 
 * <p>A {@code PatternTermOperator} that is created with no matcher specified
 * will match anything. If the {@code name} has been specified, it will
 * associate that name with the matched {@code SymbolicExpression} in its match
 * results. Otherwise, its match results will store no information about the
 * match.</p>
 * 
 * @see org.codehaus.groovy.science.SymbolicExpression
 */
public class PatternTermOperator
{
	/**
	 * <p>An {@code Iterable} that iterates through the elements of each of a
	 * list of other {@code Iterable}s, one by one.</p>
	 * 
	 * @param <T>
	 *     the type of value produced by each of the {@code Iterable}s in the
	 *     concatenation
	 */
	private static class ConcatenationOfIterables< T > implements Iterable< T >
	{
		/**
		 * <p>The {@code Iterable}s in the concatenation.</p>
		 */
		private List< Iterable< T > > innerIterables;
		
		/**
		 * <p>The number of {@code Iterable}s in the concatenation.</p>
		 */
		private int sizeOfConcatenation;
		
		
		/**
		 * <p>Creates a new {@code ConcatenationOfIterables} that is a
		 * concatenation of the given {@code Iterable}s.</p>
		 * 
		 * @param innerIterables
		 *     the {@code Iterable}s to be in the concatenation
		 * 
		 * @throws NullPointerException
		 *     if {@code innerIterables} is {@code null} or contains
		 *     {@code null}
		 */
		public ConcatenationOfIterables( List< Iterable< T > > innerIterables )
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
			
			sizeOfConcatenation = innerIterables.size();
		}
		
		
		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator< T > iterator()
		{
			return new Iterator< T >()
			{
				/**
				 * <p>A flag that is {@code true} only when {@code nextValue}
				 * actually represents either the next value this
				 * {@code Iterator} will generate or {@code null} if this
				 * {@code Iterator} has no more values.</p>
				 */
				private boolean nextIsCalculated = false;
				
				/**
				 * <p>The value that this {@code Iterator} is about to generate
				 * via its {@code next} method. This value is {@code null} if
				 * there are no more elements for this {@code Iterator} to
				 * generate (in which case {@code nextIsCalculated} is also
				 * {@code true}) or if the next value has not been calculated
				 * yet (in which case {@code nextIsCalculated} is also
				 * {@code false}).</p>
				 */
				private T nextValue = null;
				
				/**
				 * <p>An {@code Iterator} belonging to the current
				 * {@code Iterable} being traversed in the concatenation of
				 * {@code Iterable}s, or {@code null} if there is no
				 * {@code Iterable} being traversed at the moment (which is the
				 * case before the iteration starts and after the iteration
				 * ends).</p>
				 */
				private Iterator< T > innerIterator = null;
				
				/**
				 * <p>The index, between {@code 0}, inclusive, and
				 * {@code sizeOfConcatenation}, inclusive, of the next
				 * {@code Iterable} out of the {@code innerIterables} to use. A
				 * value of {@code sizeOfConcatenation} indicates that the
				 * current {@code innerIterator} is the last one.</p>
				 */
				private int nextIterableIndex = 0;
				
				/**
				 * <p>Calculates the next value to return from this
				 * {@code Iterator}, if necessary.</p>
				 */
				private void calculateNext()
				{
					if ( nextIsCalculated )
						return;
					
					
					while ( true )
					{
						if ( innerIterator == null )
						{
							if ( sizeOfConcatenation <= nextIterableIndex )
							{
								nextValue = null;
								nextIsCalculated = true;
								return;
							}
							
							innerIterator = innerIterables.get(
								nextIterableIndex
							).iterator();
							
							nextIterableIndex++;
						}
						
						if ( innerIterator.hasNext() )
						{
							nextValue = innerIterator.next();
							nextIsCalculated = true;
							return;
						}
						
						innerIterator = null;
					}
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
				public T next()
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
	 *     the type of value produced by each of the {@code Iterable}s in the
	 *     product
	 */
	private static class ProductOfIterables< T >
		implements Iterable< List< T > >
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
				 * <p>Calculates the next value to return from this
				 * {@code Iterator}, if necessary.</p>
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
	 * <p>The name of this term, or {@code null} if it has no name.</p>
	 */
	private Object name;
	
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
	 * <p>Creates a {@code PatternTermOperator} with no name that matches
	 * anything whatsoever.</p>
	 */
	public PatternTermOperator()
	{
		this( null, null );
	}
	
	/**
	 * <p>Creates a {@code PatternTermOperator} with the given name that matches
	 * anything whatsoever.</p>
	 * 
	 * @param name
	 *     the name of this term, which is also the object for the pattern
	 *     expression to associate with whatever subject expression it is
	 *     presented with; or {@code null} if it has no name and does not
	 *     associate anything
	 */
	public PatternTermOperator( Object name )
	{
		this( name, null );
	}
	
	/**
	 * <p>Creates a {@code PatternTermOperator} with the given matcher
	 * behavior and no name.</p>
	 * 
	 * <p>A matcher is a closure that accepts a {@code SymbolicExpression} and
	 * returns either an {@code Iterable} of {@code Map}s, a single {@code Map}
	 * (as a synonym of a single-valued {@code Iterable}) or {@code null} (as a
	 * synonym of an empty {@code Iterable}). The {@code Map}s, meanwhile, can
	 * contain any information that is useful to keep track of in a pattern
	 * match using this term; they are not restricted by type.</p>
	 * 
	 * @param matcher
	 *     the matcher this operator should simulate, or {@code null} if it
	 *     should use a matcher that matches anything whatsoever
	 */
	public PatternTermOperator( Closure matcher )
	{
		this( null, matcher );
	}
	
	/**
	 * <p>Creates a {@code PatternTermOperator} with the given matcher
	 * behavior and name.</p>
	 * 
	 * <p>A matcher is a closure that accepts a {@code SymbolicExpression} and
	 * returns either an {@code Iterable} of {@code Map}s, a single {@code Map}
	 * (as a synonym of a single-valued {@code Iterable}) or {@code null} (as a
	 * synonym of an empty {@code Iterable}). The {@code Map}s, meanwhile, can
	 * contain any information that is useful to keep track of in a pattern
	 * match using this term; they are not restricted by type.</p>
	 * 
	 * @param name     the name of this term, or {@code null} if it has no name
	 * 
	 * @param matcher
	 *     the matcher this operator should simulate, or {@code null} if it
	 *     should use a matcher that matches anything whatsoever
	 */
	public PatternTermOperator( Object name, Closure matcher )
	{
		this.name = name;
		
		if ( matcher != null )
		{
			this.matcher = matcher;
			return;
		}
		
		if ( name == null )
		{
			this.matcher = new Closure( null ) {
				
				private static final long serialVersionUID = 1L;
				
				@SuppressWarnings("unused")
				public Map< Object, SymbolicExpression >
					doCall( SymbolicExpression expression )
				{
					if ( expression == null )
						throw new NullPointerException();
					
					return new HashMap< Object, SymbolicExpression >();
				}
			};
			
			return;
		}
		
		
		final Object finalName = name;
		
		this.matcher = new Closure( null ) {
			
			private static final long serialVersionUID = 1L;
			
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
		};
	}
	
	
	/**
	 * <p>Creates a pattern {@code SymbolicExpression} that matches any
	 * {@code SymbolicExpression} whatsoever.</p>
	 * 
	 * <p>This is done by using a {@code PatternTermOperator}.</p>
	 * 
	 * @return
	 *     a pattern expression that matches anything
	 */
	public static SymbolicExpression pTerm()
	{
		return pTerm( null, null );
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
	 *     the name of this term, which is also the object for the pattern
	 *     expression to associate with whatever subject expression it is
	 *     presented with; or {@code null} if it has no name and does not
	 *     associate anything
	 * 
	 * @return
	 *     a pattern expression that matches anything and associates it with the
	 *     given {@code name}
	 */
	public static SymbolicExpression pTerm( Object name )
	{
		return pTerm( name, null );
	}
	
	/**
	 * <p>Creates a pattern {@code SymbolicExpression} that uses the given
	 * matcher (by putting it in a {@code PatternTermOperator}).</p>
	 * 
	 * @param matcher
	 *     the matcher for the {@code SymbolicExpression} to simulate, or
	 *     {@code null} if it should use a matcher that matches anything
	 *     whatsoever
	 * 
	 * @return  a {@code SymbolicExpression} that simulates the given matcher
	 */
	public static SymbolicExpression pTerm( Closure matcher )
	{
		return pTerm( null, matcher );
	}
	
	/**
	 * <p>Creates a pattern {@code SymbolicExpression} with the given name that
	 * uses the given matcher (by putting it in a
	 * {@code PatternTermOperator}).</p>
	 * 
	 * @param name
	 *     the name of this term, or {@code null} if it has no name
	 * 
	 * @param matcher
	 *     the matcher for the {@code SymbolicExpression} to simulate, or
	 *     {@code null} if it should use a matcher that matches anything
	 *     whatsoever
	 * 
	 * @return
	 *     a pattern expression that simulates the given matcher and has the
	 *     given name
	 */
	public static SymbolicExpression pTerm( Object name, Closure matcher )
	{
		return new SymbolicExpression(
			new PatternTermOperator( name, matcher ),
			new ArrayList< SymbolicExpression >()
		);
	}
	
	/**
	 * <p>Creates a nameless pattern {@code SymbolicExpression} that matches any
	 * of a certain set of {@code SymbolicExpression}s and returns empty match
	 * results when it matches. The {@code SymbolicExpression}s accepted are
	 * those that satisfy the given {@code caseValue} object's Groovy
	 * {@code isCase} method.</p>
	 * 
	 * @param caseValue
	 *     an object whose {@code isCase} method will determine whether this
	 *     pattern term will match a particular {@code SymbolicExpression}
	 * 
	 * @return
	 *     a pattern expression that matches everything that satisfies the
	 *     {@code caseValue}
	 * 
	 * @throws NullPointerException  if {@code caseValue} is {@code null}
	 */
	public static SymbolicExpression pCase( Object caseValue )
	{
		return pCase( null, caseValue );
	}
	
	/**
	 * <p>Creates a pattern {@code SymbolicExpression} with the given name that
	 * matches any of a certain set of {@code SymbolicExpression}s and
	 * associates that name with them in the match result. The
	 * {@code SymbolicExpression}s accepted are those that satisfy the given
	 * {@code caseValue} object's Groovy {@code isCase} method.</p>
	 * 
	 * @param name
	 *     the name of this term, or {@code null} if it has no name
	 * 
	 * @param caseValue
	 *     an object whose {@code isCase} method will determine whether this
	 *     pattern term will match a particular {@code SymbolicExpression}
	 * 
	 * @return
	 *     a pattern expression with the given name that matches everything that
	 *     satisfies the {@code caseValue}
	 * 
	 * @throws NullPointerException  if {@code caseValue} is {@code null}
	 */
	public static SymbolicExpression pCase( Object name, Object caseValue )
	{
		if ( caseValue == null )
			throw new NullPointerException();
		
		final Object finalName = name;
		final Object finalCaseValue = caseValue;
		
		return pTerm(
			name,
			new Closure( null )
			{
				private static final long serialVersionUID = 1L;
				
				@SuppressWarnings("unused")
				public Iterable< Map< ?, ? > >
					doCall( SymbolicExpression expression )
				{
					if ( ((Boolean)InvokerHelper.invokeMethod(
						finalCaseValue,
						"isCase",
						new Object[]{ expression }
					)).booleanValue() )
					{
						Map< Object, Object > result =
							new HashMap< Object, Object >();
						
						if ( finalName != null )
							result.put( finalName, expression );
						
						return Arrays.asList( new Map< ?, ? >[]{ result } );
					}
					
					return new ArrayList< Map< ?, ? > >();
				}
			}
		);
	}
	
	/**
	 * <p>Returns a pattern expression that matches the given
	 * {@code innerPattern} pattern expression against each subexpression of a
	 * subject expression, one at a time. Each of those match result
	 * {@code Map}s is augmented by associating {@code name} with a
	 * {@code Closure} representing the "jumped" expression segment.</p>
	 * 
	 * <p>The subexpressions are traversed starting with the root subexpression
	 * and going inward, with each argument of an expression explored in the
	 * order specified by that expression's argument list.</p>
	 * 
	 * <p>An expression segment {@code Closure} is a closure that represents all
	 * of a {@code SymbolicExpression} except one subexpression of it. The
	 * closure accepts a {@code SymbolicExpression} to put in place of that
	 * subexpression, and it returns the completed
	 * {@code SymbolicExpression}.</p> 
	 * 
	 * <p>Note that if any application of {@code innerPattern} results in a
	 * match result that contains an association for {@code name}, that result
	 * will be skipped in order to avoid a naming conflict.</p>
	 * 
	 * @param name
	 *     the object to associate, in each match, with the expression segment
	 *     "jumped" to get to where {@code innerPattern} was applied
	 * 
	 * @param innerPattern
	 *     the pattern expression to try to match to each subexpression of a
	 *     {@code SymbolicExpression}
	 * 
	 * @return
	 *     a pattern expression that takes any subject expression it is applied
	 *     to and matches {@code innerPattern} to each of the subject
	 *     expression's subexpressions, augmenting each of the match results by
	 *     associating {@code name} with a {@code Closure} representing the
	 *     "jumped" expression segment
	 * 
	 * @throws NullPointerException
	 *     if {@code name} or {@code innerPattern} is {@code null}
	 */
	public static SymbolicExpression pJump(
		Object name,
		SymbolicExpression innerPattern
	)
	{
		if (
			(name == null)
			||
			(innerPattern == null)
		)
			throw new NullPointerException();
		
		return pJumpHelper( name, innerPattern );
	}
	
	/**
	 * <p>Returns a pattern expression that matches the given
	 * {@code innerPattern} pattern expression against each subexpression of a
	 * subject expression, one at a time.</p>
	 * 
	 * <p>The subexpressions are traversed starting with the root subexpression
	 * and going inward, with each argument of an expression explored in the
	 * order specified by that expression's argument list.</p>
	 * 
	 * @param innerPattern
	 *     the pattern expression to try to match to each subexpression of a
	 *     {@code SymbolicExpression}
	 * 
	 * @return
	 *     a pattern expression that takes any subject expression it is applied
	 *     to and matches {@code innerPattern} to each of the subject
	 *     expression's subexpressions
	 * 
	 * @throws NullPointerException  if {@code innerPattern} is {@code null}
	 */
	public static SymbolicExpression pJump( SymbolicExpression innerPattern )
	{
		if ( innerPattern == null )
			throw new NullPointerException();
		
		return pJumpHelper( null, innerPattern );
	}
	
	/**
	 * <p>This method provides the actual implementation of
	 * {@code pJump( Object, SymbolicExpression )} (when {@code name} is not
	 * {@code null}) and {@code pJump( Object )} (when {@code name} is
	 * {@code null}).</p>
	 * 
	 * @see pJump( Object, SymbolicExpression )
	 * @see pJump( SymbolicExpression )
	 * 
	 * @throws NullPointerException
	 *     if {@code innerPattern} is {@code null}
	 */
	private static SymbolicExpression pJumpHelper(
		Object name,
		SymbolicExpression innerPattern
	)
	{
		if ( innerPattern == null )
			throw new NullPointerException();
		
		final Object finalName = name;
		final SymbolicExpression finalInnerPattern = innerPattern;
		
		return pTerm( new Closure( null ) {
			
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("unused")
			public Iterable< Map< ?, ? > >
				doCall( SymbolicExpression expression )
			{
				if ( expression == null )
					throw new NullPointerException();
				
				final SymbolicExpression finalExpression = expression;
				
				return new Iterable< Map< ?, ? > >()
				{
					public Iterator< Map< ?, ? > > iterator()
					{
						return new Iterator< Map< ?, ? > >()
						{
							/**
							 * <p>A flag that is {@code true} only when
							 * {@code nextValue} actually represents either the
							 * next value this {@code Iterator} will generate or
							 * {@code null} if this {@code Iterator} has no more
							 * values.</p>
							 */
							private boolean nextIsCalculated = false;
							
							/**
							 * <p>The value that this {@code Iterator} is about
							 * to generate via its {@code next} method. This
							 * value is {@code null} if there are no more
							 * elements for this {@code Iterator} to generate
							 * (in which case {@code nextIsCalculated} is also
							 * {@code true}) or if the next value has not been
							 * calculated yet (in which case
							 * {@code nextIsCalculated} is also
							 * {@code false}).</p>
							 */
							private Map< ?, ? > nextValue;
							
							/**
							 * <p>An {@code Iterator} that loops through the
							 * subexpressions of {@code expression} one by
							 * one.</p>
							 * 
							 * <p>This iterator should be in perfect sync with
							 * {@code theseSegments} at all times. The values
							 * returned by {@code theseSubexpressions} should be
							 * exactly the same as the "holes" in the segments
							 * returned by {@code theseSegments}.</p>
							 * 
							 * @see theseSegments
							 */
							private Iterator< SymbolicExpression >
								theseSubexpressions =
								subexpressions( finalExpression ).iterator();
							
							/**
							 * <p>An {@code Iterator} that loops through the
							 * expression segments of {@code expression} one by
							 * one.</p>
							 * 
							 * <p>This iterator should be in perfect sync with
							 * {@code theseSubexpressions} at all times. The
							 * values returned by {@code theseSubexpressions}
							 * should be exactly the same as the "holes" in the
							 * segments returned by {@code theseSegments}.</p>
							 * 
							 * @see theseSubexpressions
							 */
							private Iterator< Closure > theseSegments =
								expressionSegments(
									finalExpression
								).iterator();
							
							/**
							 * <p>The {@code Iterator} yielding the matches of
							 * the current application of
							 * {@code innerPattern}.</p>
							 */
							private Iterator< Map< ?, ? > > patternResults =
								null;
							
							/**
							 * <p>The expression segment representing the "jump"
							 * from {@code expression} to the subexpression
							 * currently being matched using
							 * {@code innerPattern}.</p>
							 */
							private Closure currentSegment;
							
							/**
							 * <p>Calculates the next value to return from this
							 * {@code Iterator}, if necessary.</p>
							 */
							private void calculateNext()
							{
								if ( nextIsCalculated )
									return;
								
								while ( true )
								{
									if ( patternResults == null )
									{
										if ( !theseSegments.hasNext() )
										{
											nextValue = null;
											nextIsCalculated = true;
											
											return;
										}
										
										currentSegment = theseSegments.next();
										patternResults = matchesFor(
											finalInnerPattern,
											theseSubexpressions.next()
										).iterator();
									}
									
									while ( patternResults.hasNext() )
									{
// NOTE: This is deep nesting. Something should probably be done about this....

Map< Object, Object > thisResult =
	new HashMap< Object, Object >( patternResults.next() );

if ( finalName == null )
{
	nextValue = thisResult;
	nextIsCalculated = true;
	
	return;
}

if ( !thisResult.containsKey( finalName ) )
{
	thisResult.put( finalName, currentSegment );
	
	nextValue = thisResult;
	nextIsCalculated = true;
	
	return;
}
									}
									
									patternResults = null;
								}
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
		} );
	}
	
	/**
	 * <p>Returns an {@code Iterable} whose {@code Iterator}s iterate through
	 * and provide each of the subexpressions of {@code expression}.</p>
	 * 
	 * <p>The subexpressions are traversed starting with the root subexpression
	 * and going inward, with each argument of an expression explored in the
	 * order specified by that expression's argument list.</p>
	 * 
	 * @param expression  the expression to iterate on
	 * 
	 * @return  an {@code Iterable} representing a sequence of subexpressions
	 * 
	 * @throws NullPointerException  if {@code expression} is {@code null}
	 */
	private static Iterable< SymbolicExpression > subexpressions(
		SymbolicExpression expression
	)
	{
		if ( expression == null )
			throw new NullPointerException();
		
		List< Iterable< SymbolicExpression > > innerIterables =
			new ArrayList< Iterable< SymbolicExpression > >();
		
		innerIterables.add( Arrays.asList( new SymbolicExpression[]{
			expression
		} ) );
		
		for ( SymbolicExpression argument: expression.getArgumentList() )
		{
			innerIterables.add( subexpressions( argument ) );
		}
		
		return new ConcatenationOfIterables< SymbolicExpression >(
			innerIterables
		);
	}
	
	/**
	 * <p>Returns an {@code Iterable} whose {@code Iterator}s iterate through
	 * each of the subexpressions of {@code expression} and provide
	 * {@code Closure}s that represent all of {@code expression} except for
	 * those subexpressions.</p>
	 * 
	 * <p>Each of the "expression segment" closures accepts a
	 * {@code SymbolicExpression} and returns a {@code SymbolicExpression}
	 * exactly like the original {@code expression} except that one of the
	 * subexpressions has been replaced by the given expression.</p>
	 * 
	 * <p>The subexpressions are traversed starting with the root subexpression
	 * and going inward, with each argument of an expression explored in the
	 * order specified by that expression's argument list.</p>
	 * 
	 * @param expression  the expression to iterate on
	 * 
	 * @return
	 *     an {@code Iterable} representing a sequence of expression segments
	 * 
	 * @throws NullPointerException  if {@code expression} is {@code null}
	 */
	private static Iterable< Closure > expressionSegments(
		SymbolicExpression expression
	)
	{
		if ( expression == null )
			throw new NullPointerException();
		
		return expressionSegments(
			new Closure( null )
			{
				private static final long serialVersionUID = 1L;
				
				@SuppressWarnings("unused")
				public SymbolicExpression doCall(
					SymbolicExpression replacement
				)
				{
					return replacement;
				}
			},
			expression
		);
	}
	
	/**
	 * <p>Returns an {@code Iterable} whose {@code Iterator}s iterate through
	 * each of the subexpressions of {@code expression} and provide
	 * {@code Closure}s that represent all of {@code segmentSoFar( expression )}
	 * except those subexpressions.</p>
	 * 
	 * <p>Each of the closures accepts a {@code SymbolicExpression} and returns
	 * a {@code SymbolicExpression} exactly like
	 * {@code segmentSoFar( expression )} except that one of the subexpressions
	 * has been replaced by the given expression.</p>
	 * 
	 * <p>The subexpressions are traversed starting with the root subexpression
	 * and going inward, with each argument of an expression explored in the
	 * order specified by that expression's argument list.</p>
	 * 
	 * @see expressionSegments( SymbolicExpression )
	 * 
	 * @param segmentSoFar
	 *     an expression segment that any segment of {@code expression} that is
	 *     not replaced should be plugged into
	 * 
	 * @param expression  the expression to iterate on
	 * 
	 * @return
	 *     an {@code Iterable} representing a sequence of expression segments
	 * 
	 * @throws NullPointerException
	 *     if {@code segmentSoFar} or {@code expression} is {@code null}
	 */
	private static Iterable< Closure > expressionSegments(
		Closure segmentSoFar,
		SymbolicExpression expression
	)
	{
		if (
			(segmentSoFar == null)
			||
			(expression == null)
		)
			throw new NullPointerException();
		
		final Closure finalSegmentSoFar = segmentSoFar;
		
		List< Iterable< Closure > > innerIterables =
			new ArrayList< Iterable< Closure > >();
		
		innerIterables.add( Arrays.asList( new Closure[]{ new Closure( null ) {
			
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("unused")
			public SymbolicExpression doCall( SymbolicExpression replacement )
			{
				return (SymbolicExpression)finalSegmentSoFar.call(
					new Object[]{ replacement }
				);
			}
		} } ) );
		
		final Object operator = expression.getOperator();
		
		final List< SymbolicExpression > argumentList =
			expression.getArgumentList();
		
		int numberOfArguments = argumentList.size();
		for ( int index = 0; index < numberOfArguments; index++ )
		{
			final int thisIndex = index;
			innerIterables.add( expressionSegments(
				new Closure( null )
				{
					private static final long serialVersionUID = 1L;
					
					@SuppressWarnings("unused")
					public SymbolicExpression doCall(
						SymbolicExpression replacement
					)
					{
						List< SymbolicExpression > theseArguments =
							new ArrayList< SymbolicExpression >( argumentList );
						
						theseArguments.set( thisIndex, replacement );
						
						return (SymbolicExpression)finalSegmentSoFar.call(
							new Object[]{ new SymbolicExpression(
								operator,
								theseArguments
							) }
						);
					}
				},
				argumentList.get( index )
			) );
		}
		
		return new ConcatenationOfIterables< Closure >( innerIterables );
	}
	
	/**
	 * <p>Applies the {@code pattern} expression to the {@code subject}
	 * expression and returns an {@code Iterable} whose {@code Iterator}s will
	 * iterate through each of the possible match results.</p>
	 * 
	 * <p>Note that if any of the matchers in the pattern expression returns an
	 * {@code Iterable} of something other than {@code Map}s, this method could
	 * very well return that bad {@code Iterable}.</p>
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
					 * <p>Calculates the next value to return from this
					 * {@code Iterator}, if necessary.</p>
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
								if ( !result.get( key ).equals(
									map.get( key )
								) )
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
	 * <p>Applies the {@code pattern} expression to the {@code subject}
	 * expression and returns the first match result, where the order is
	 * determined by the {@code Iterable} produced by {@code matchesFor}.</p>
	 * 
	 * @param pattern  a pattern {@code SymbolicExpression}
	 * @param subject  a {@code SymbolicExpression} to try to match
	 * 
	 * @return  the first match result
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern} or {@code subject} is {@code null}
	 */
	public static Map< ?, ? > firstMatchFor(
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
		
		Iterator< Map< ?, ? > > matches =
			matchesFor( pattern, subject ).iterator();
		
		if ( matches.hasNext() )
			return matches.next();
		
		return null;
	}
	
	/**
	 * <p>Applies the {@code pattern} expression to the {@code subject}
	 * expression and determines whether there are any results.</p>
	 * 
	 * @param pattern  a pattern {@code SymbolicExpression}
	 * @param subject  a {@code SymbolicExpression} to try to match
	 * 
	 * @return
	 *     {@code true} if {@code subject} fits {@code pattern}; {@code false}
	 *     otherwise
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern} or {@code subject} is {@code null}
	 */
	public static boolean matchesExistFor(
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
		
		return matchesFor( pattern, subject ).iterator().hasNext();
	}
	
	/**
	 * <p>Applies the {@code pattern} expression to every subexpression of the
	 * {@code subject} expression and returns an {@code Iterable} whose
	 * {@code Iterator}s will iterate through each of the possible match
	 * results.</p>
	 * 
	 * <p>The subexpressions are traversed starting with the root subexpression
	 * (the {@code subject} itself) and going inward, with each argument of an
	 * expression explored in the order specified by that expression's argument
	 * list.</p>
	 * 
	 * <p>Note that if any of the matchers in the pattern expression returns an
	 * {@code Iterable} of something other than {@code Map}s, this method could
	 * very well return that bad {@code Iterable}.</p>
	 * 
	 * @param pattern  a pattern {@code SymbolicExpression}
	 * @param subject  a {@code SymbolicExpression} to try to match
	 * 
	 * @return  an {@code Iterable} of the possible match results
	 * 
	 * @throws NullPointerException
	 *     if {@code pattern} or {@code subject} is {@code null}
	 */
	public static Iterable< Map< ?, ? > > matchesAnywhereFor(
		SymbolicExpression pattern,
		SymbolicExpression subject
	)
	{
		List< Iterable< Map< ?, ? > > > innerIterables =
			new ArrayList< Iterable< Map< ?, ? > > >();
		
		innerIterables.add( matchesFor( pattern, subject ) );
		
		for ( SymbolicExpression subSubject: subject.getArgumentList() )
		{
			innerIterables.add( matchesAnywhereFor( pattern, subSubject ) );
		}
		
		return new ConcatenationOfIterables< Map< ?, ? > >( innerIterables );
	}
	
	
	/**
	 * @see PatternTermOperator#name
	 * 
	 * @return  the name of this term, or {@code null} if it has no name
	 */
	public Object getName()
	{
		return name;
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