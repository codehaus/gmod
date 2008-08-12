package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.PatternTermOperator.ConcatenationOfIterables
import org.codehaus.groovy.science.PatternTermOperator.ProductOfIterables

import static org.codehaus.groovy.science.ConstantOperator.*
import static org.codehaus.groovy.science.PatternTermOperator.*
import static org.codehaus.groovy.science.SymbolicExpression.*


class PatternTermOperatorTest extends GroovyTestCase
{
	void testProductOfIterablesConstructorFailure()
	{
		// Make sure that {@code ProductOfIterables}'s constructor fails when
		// necessary.
		
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
		
		assertFalse( iterator.hasNext() );
	}
	
	void testConcatenationOfIterablesConstructorFailure()
	{
		// Make sure that {@code ConcatenationOfIterables}'s constructor fails
		// when necessary.
		
		shouldFail(
			NullPointerException.class,
			{ new ConcatenationOfIterables( null ) }
		);
		
		shouldFail(
			NullPointerException.class,
			{ new ConcatenationOfIterables( [ null ] ) }
		);
		
		shouldFail(
			NullPointerException.class,
			{ new ConcatenationOfIterables(
				[ [ 1, 2, 3 ], null [ 4, 5, 6 ] ]
			) }
		);
	}
	
	void testConcatenationOfIterables()
	{
		// Test {@code ConcatenationOfIterables} by making sure that a
		// particular usage of it actually gives the right values in the right
		// order.
		
		def iterator = new ConcatenationOfIterables( [
			[ 1, 2 ],
			[ 3, 4 ],
			[ 5, 6 ]
		] ).iterator();
		
		[ 1, 2, 3, 4, 5, 6 ].each {
			assertEquals( it, iterator.next() );
		};
		
		assertFalse( iterator.hasNext() );
	}
	
	void testPTermFailure()
	{
		// Make sure that {@code pTerm()}, {@code pTerm( Object )},
		// {@code pTerm( Closure )}, and {@code pTerm( Object, Closure )} fail
		// when necessary.
				
		def dummy = expr( "dummy" );
		
		shouldFail(
			ClassCastException.class,
			{ matchesFor(
				pTerm( { "This is not null, a Map, or an Iterator< Map >." } ),
				dummy
			) }
		);
	}
	
	void testPTerm()
	{
		// Test {@code pTerm( Closure )} and {@code pTerm( Object )} by using
		// them to make simple pattern expressions and testing those expressions
		// with {@code matchesFor}.
		
		def dummy = expr( "dummy" );
		
		
		def nullMatches = matchesFor( pTerm( { null } ), dummy ).iterator();
		
		assertFalse( nullMatches.hasNext() );
		
		
		def singleMatches = matchesFor( pTerm( { [:] } ), dummy ).iterator();
		
		assertEquals( singleMatches.next(), [:] );
		assertFalse( singleMatches.hasNext() );
		
		
		def listMatches = matchesFor(
			pTerm( { [ [ a: 1 ], [ a: it ] ] } ),
			dummy
		).iterator();
		
		assertEquals( listMatches.next(), [ a: 1 ] );
		assertEquals( listMatches.next(), [ a: dummy ] );
		assertFalse( listMatches.hasNext() );
		
		
		def nameMatches = matchesFor( pTerm( "x" ), dummy ).iterator();
		
		assertEquals( nameMatches.next(), [ x: dummy ] );
		assertFalse( nameMatches.hasNext() );
	}
	
	void testPJumpFailure()
	{
		// Make sure that {@code pJump( Object, SymbolicExpression )} and
		// {@code pJump( SymbolicExpression )} fail when necessary.
		
		def dummy = expr( "dummy" );
		
		
		shouldFail( NullPointerException.class, { pJump( null ) } );
		shouldFail( NullPointerException.class, { pJump( dummy, null ) } );
		shouldFail( NullPointerException.class, { pJump( null, dummy ) } );
		
		shouldFail(
			ClassCastException.class,
			{ matchesFor(
				pJump( pTerm(
					{ "This is not null, a Map, or an Iterator< Map >." }
				) ),
				dummy
			).iterator().hasNext() }
		);
	}
	
	void testPJump()
	{
		// Make sure that {@code pJump( Object, SymbolicExpression )} traverses
		// subexpressions in the correct order and provides correct expression
		// segments.
		
		def dummy = expr( "dummy" );
		
		def plusPattern = pJump( "jump", pTerm( "a" ) + pTerm( "b" ) );
		
		
		def plusNoMatches =
			matchesFor( plusPattern, expr( "dummy" ) ).iterator();
		
		assertFalse( plusNoMatches.hasNext() );
		
		
		def plusMatches = matchesFor(
			plusPattern,
			con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 )) + con( 5 )
		).iterator();
		
		[
			[
				jump: dummy,
				a: con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 )),
				b: con( 5 )
			],
			[
				jump: dummy + con( 5 ),
				a: con( 1 ) + con( 2 ),
				b: con( 3 ) + con( 4 )
			],
			[
				jump: dummy + (con( 3 ) + con( 4 )) + con( 5 ),
				a: con( 1 ),
				b: con( 2 )
			],
			[
				jump: con( 1 ) + con( 2 ) + dummy + con( 5 ),
				a: con( 3 ),
				b: con( 4 )
			]
		].each {
			def modifiedMatch = new HashMap( plusMatches.next() );
			modifiedMatch[ "jump" ] = modifiedMatch[ "jump" ]( dummy );
			
			assertEquals( it, modifiedMatch );
		};
		assertFalse( plusMatches.hasNext() );
	}
	
	void testMatchesForFailure()
	{
		// Make sure that {@code matchesFor}, {@code firstMatchFor}, and
		// {@code matchesExistFor} fail when necessary.
		
		def dummy = expr( "dummy" );
		
		shouldFail( NullPointerException, { matchesFor( null, null ) } );
		shouldFail( NullPointerException, { matchesFor( null, dummy ) } );
		shouldFail( NullPointerException, { matchesFor( dummy, null ) } );
		shouldFail( NullPointerException, { firstMatchFor( null, null ) } );
		shouldFail( NullPointerException, { firstMatchFor( null, dummy ) } );
		shouldFail( NullPointerException, { firstMatchFor( dummy, null ) } );
		shouldFail( NullPointerException, { matchesExistFor( null, null ) } );
		shouldFail( NullPointerException, { matchesExistFor( null, dummy ) } );
		shouldFail( NullPointerException, { matchesExistFor( dummy, null ) } );
	}
	
	void testMatchesFor()
	{
		// Test {@code matchesFor}, {@code firstMatchFor}, and
		// {@code matchesExistFor} with some complicated pattern expressions,
		// making sure that they return the correct match results.
		
		def dummy = expr( "dummy" );
		
		
		def multiPattern = (
			pTerm( { [ [ a: 1 ], [ a: 2 ] ] } )
			+
			pTerm( { [ [ b: 1 ], [ b: 2 ] ] } )
		);
		
		def multiNoMatches = matchesFor( multiPattern, dummy ).iterator();
		assertFalse( multiNoMatches.hasNext() );
		assertFalse( matchesExistFor( multiPattern, dummy ) );
		assertEquals( firstMatchFor( multiPattern, dummy ), null );
		
		def multiMatches = matchesFor( multiPattern, dummy + dummy ).iterator();
		assertEquals( multiMatches.next(), [ a: 1, b: 1 ] );
		assertEquals( multiMatches.next(), [ a: 2, b: 1 ] );
		assertEquals( multiMatches.next(), [ a: 1, b: 2 ] );
		assertEquals( multiMatches.next(), [ a: 2, b: 2 ] );
		assertFalse( multiMatches.hasNext() );
		assertTrue( matchesExistFor( multiPattern, dummy + dummy ) );
		assertEquals(
			firstMatchFor( multiPattern, dummy + dummy ),
			[ a: 1, b: 1 ]
		);
		
		
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
		assertFalse( conflictingMatches.hasNext() );
		assertTrue( matchesExistFor( conflictingPattern, dummy + dummy ) );
		assertEquals(
			firstMatchFor( conflictingPattern, dummy + dummy ),
			[ a: 1, b: 1, c: 1 ]
		);
	}
	
	void testMatchesAnywhereForFailure()
	{
		// Make sure that {@code matchesAnywhereFor} fails when necessary.
		
		def dummy = expr( "dummy" );
		
		shouldFail(
			NullPointerException.class,
			{ matchesAnywhereFor( null, null ) }
		);
		
		shouldFail(
			NullPointerException.class,
			{ matchesAnywhereFor( null, dummy ) }
		);
		
		shouldFail(
			NullPointerException.class,
			{ matchesAnywhereFor( dummy, null ) }
		);
	}
	
	void testMatchesAnywhereFor()
	{
		// Make sure that {@code matchesAnywhereFor} iterates through
		// subexpressions of the subject expression in the correct order.
		
		def plusPattern = pTerm( "a" ) + pTerm( "b" );
		
		
		def plusNoMatches =
			matchesAnywhereFor( plusPattern, expr( "dummy" ) ).iterator();
		
		assertFalse( plusNoMatches.hasNext() );
		
		
		def plusMatches = matchesAnywhereFor(
			plusPattern,
			con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 )) + con( 5 )
		).iterator();
		
		[
			[ a: con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 )), b: con( 5 ) ],
			[ a: con( 1 ) + con( 2 ), b: con( 3 ) + con( 4 ) ],
			[ a: con( 1 ), b: con( 2 ) ],
			[ a: con( 3 ), b: con( 4 ) ]
		].each {
			assertEquals( it, plusMatches.next() )
		};
		assertFalse( plusMatches.hasNext() );
	}
}