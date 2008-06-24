package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.PatternTermOperator.ProductOfIterables

import static org.codehaus.groovy.science.PatternTermOperator.*
import static org.codehaus.groovy.science.SymbolicExpression.*


class PatternTermOperatorTest extends GroovyTestCase
{
	void testProductOfIterablesConstructorFailure()
	{
		// Make sure that {@code PatternMatchingTools.ProductOfIterables}'s
		// constructor fails when necessary.
		
		shouldFail(
			NullPointerException.class,
			{ new ProductOfIterables( null ) }
		);
		
		shouldFail(
			NullPointerException.class,
			{ new ProductOfIterables( [ null ] ) }
		);
		
		shouldFail(
			NullPointerException.class,
			{ new ProductOfIterables( [ [ 1, 2, 3 ], null [ 4, 5, 6 ] ] ) }
		);
	}
	
	void testProductOfIterablesExample()
	{
		// Test {@code ProductOfIterables} by making sure that the example given
		// in its documentation actually works.
		
		def iterator = new ProductOfIterables( [
			[ 1, 2 ],
			[ 3, 4 ],
			[ 5, 6 ]
		] ).iterator();
		
		[
			[ 1, 3, 5 ],
			[ 2, 3, 5 ],
			[ 1, 4, 5 ],
			[ 2, 4, 5 ],
			[ 1, 3, 6 ],
			[ 2, 3, 6 ],
			[ 1, 4, 6 ],
			[ 2, 4, 6 ]
		].each {
			assertEquals( it, iterator.next() );
		};
		
		assert !iterator.hasNext();
	}
	
	void testPTermFailure()
	{
		// Make sure that {@code pTerm( Closure )} and {@code pTerm( String )}
		// fail when necessary.
		
		shouldFail( NullPointerException.class, { pTerm( (Closure)null ) } );
		shouldFail( NullPointerException.class, { pTerm( (String)null ) } );
		
		
		def dummy = expr( "dummy" );
		
		shouldFail(
			ClassCastException.class,
			{
				matchesFor(
					pTerm(
						{ "This is not null, a Map, or an Iterator< Map >." }
					),
					dummy
				)
			}
		);
	}
	
	void testPTerm()
	{
		// Test {@code pTerm( Closure )} and {@code pTerm( String )} by using
		// them to make simple pattern expressions and testing those expressions
		// with {@code matchesFor}.
		
		def dummy = expr( "dummy" );
		
		
		def nullMatches = matchesFor( pTerm( { null } ), dummy ).iterator();
		
		assert !nullMatches.hasNext();
		
		
		def singleMatches = matchesFor( pTerm( { [:] } ), dummy ).iterator();
		
		assertEquals( singleMatches.next(), [:] );
		assert !singleMatches.hasNext();
		
		
		def listMatches = matchesFor(
			pTerm( { [ [ a: 1 ], [ a: it ] ] } ),
			dummy
		).iterator();
		
		assertEquals( listMatches.next(), [ a: 1 ] );
		assertEquals( listMatches.next(), [ a: dummy ] );
		assert !listMatches.hasNext();
		
		
		def nameMatches = matchesFor( pTerm( "x" ), dummy ).iterator();
		
		assertEquals( nameMatches.next(), [ x: dummy ] );
		assert !nameMatches.hasNext();
	}
	
	void testMatchesForFailure()
	{
		// Make sure that {@code matchesFor} fails when necessary.
		
		def dummy = expr( "dummy" );
		
		shouldFail( NullPointerException.class, { matchesFor( null, null ) } );
		shouldFail( NullPointerException.class, { matchesFor( null, dummy ) } );
		shouldFail( NullPointerException.class, { matchesFor( dummy, null ) } );
	}
	
	void testMatchesFor()
	{
		// Test {@code matchesFor} with some complicated pattern expressions,
		// making sure that it returns the correct match results.
		
		def dummy = expr( "dummy" );
		
		
		def multiPattern = (
			pTerm( { [ [ a: 1 ], [ a: 2 ] ] } )
			+
			pTerm( { [ [ b: 1 ], [ b: 2 ] ] } )
		);
		
		def multiNoMatches = matchesFor( multiPattern, dummy ).iterator();
		assert !multiNoMatches.hasNext();
		
		def multiMatches = matchesFor( multiPattern, dummy + dummy ).iterator();
		assertEquals( multiMatches.next(), [ a: 1, b: 1 ] );
		assertEquals( multiMatches.next(), [ a: 2, b: 1 ] );
		assertEquals( multiMatches.next(), [ a: 1, b: 2 ] );
		assertEquals( multiMatches.next(), [ a: 2, b: 2 ] );
		assert !multiMatches.hasNext();
		
		
		def conflictingPattern = (
			pTerm( { [ [ a: 1, b: 1 ], [ a: 1, b: 2 ], [ a: 2, b: 2 ] ] } )
			+
			pTerm( { [ [ b: 1, c: 1 ], [ b: 1, c: 2 ], [ b: 2, c: 2 ] ] } )
		);
		
		def conflictingMatches =
			matchesFor( conflictingPattern, dummy + dummy ).iterator();
		
		assertEquals( conflictingMatches.next(), [ a: 1, b: 1, c: 1 ] );
		assertEquals( conflictingMatches.next(), [ a: 1, b: 1, c: 2 ] );
		assertEquals( conflictingMatches.next(), [ a: 1, b: 2, c: 2 ] );
		assertEquals( conflictingMatches.next(), [ a: 2, b: 2, c: 2 ] );
		assert !conflictingMatches.hasNext();
	}
}