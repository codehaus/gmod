package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.SymbolicExpression

import static org.codehaus.groovy.science.ConstantOperator.*
import static org.codehaus.groovy.science.PatternTermOperator.*
import static org.codehaus.groovy.science.ReplacementTermOperator.*
import static org.codehaus.groovy.science.SymbolicExpression.*


class ReplacementTermOperatorTest extends GroovyTestCase
{
	void testRTermFailure()
	{
		// Make sure that {@code rTerm( Closure )} and {@code rTerm( String )}
		// fail when necessary.
		
		shouldFail( NullPointerException.class, { rTerm( (Closure)null ) } );
		shouldFail( NullPointerException.class, { rTerm( (String)null ) } );
		
		
		def dummy = expr( "dummy" );
		
		shouldFail(
			ClassCastException.class,
			{
				replacementFor(
					[:],
					rTerm( { "This is not null or a SymbolicExpression." } )
				)
			}
		);
	}
	
	void testRTerm()
	{
		// Test {@code rTerm( Closure )} and {@code rTerm( String )} by using
		// them to make simple replacement expressions and testing those
		// expressions with {@code replacementFor}.
		
		def dummy = expr( "dummy" );
		
		
		assertEquals( replacementFor( [:], rTerm( { null } ) ), null );
		assertEquals( replacementFor( [:], rTerm( { dummy } ) ), dummy );
		
		
		assertEquals(
			replacementFor( [:], rTerm( {
				if ( it.containsKey( "x" ) )
					return dummy;
			} ) ),
			null
		);
		
		assertEquals(
			replacementFor( [ x: null ], rTerm( {
				if ( it.containsKey( "x" ) )
					return dummy;
			} ) ),
			dummy
		);
		
		
		def rTermX1 = rTerm( { it[ "x" ] } );
		
		assertEquals( replacementFor( [:], rTermX1 ), null );
		assertEquals( replacementFor( [ x: null ], rTermX1 ), null );
		assertEquals( replacementFor( [ x: dummy ], rTermX1 ), dummy );
		
		
		def rTermX2 = rTerm( "x" );
		
		assertEquals( replacementFor( [:], rTermX2 ), null );
		assertEquals( replacementFor( [ x: null ], rTermX2 ), null );
		assertEquals( replacementFor( [ x: dummy ], rTermX2 ), dummy );
	}
	
	void testRJumpFailure()
	{
		// Make sure that {@code rJump} fails when necessary.
		
		def dummy = expr( "dummy" );
		
		
		shouldFail( NullPointerException.class, { rJump( null, dummy ) } );
		shouldFail( NullPointerException.class, { rJump( dummy, null ) } );
		
		shouldFail(
			ClassCastException.class,
			{ replacementFor(
				[ jump: { it } ],
				rJump(
					"jump",
					rTerm( { "This is not null or a SymbolicExpression." } )
				)
			) }
		);
		
		assertEquals(
			replacementFor(
				[ jump: { "This is not null or a SymbolicExpression." } ],
				rJump( "jump", dummy )
			),
			null
		);
		
		assertEquals(
			replacementFor(
				[ jump: { -> "This closure has no parameters." } ],
				rJump( "jump", dummy )
			),
			null
		);
	}
	
	void testRJump()
	{
		// Test {@code rJump} by using it to make a replacement expression and
		// testing that expression with {@code replacementsFor}.
		
		def plusToDivResults = replacementsFor(
			pJump( "jump", pTerm( "a" ) + pTerm( "b" ) ),
			rJump( "jump", rTerm( "a" ) / rTerm( "b" ) ),
			con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 )) + con( 5 )
		).iterator();
		
		[
			(con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 ))) / con( 5 ),
			(con( 1 ) + con( 2 )) / (con( 3 ) + con( 4 )) + con( 5 ),
			con( 1 ) / con( 2 ) + (con( 3 ) + con( 4 )) + con( 5 ),
			con( 1 ) + con( 2 ) + con( 3 ) / con( 4 ) + con( 5 )
		].each {
			assertEquals( it, plusToDivResults.next() );
		};
		assertFalse( plusToDivResults.hasNext() );
	}
	
	void testReplacementFor()
	{
		// Test {@code replacementFor} with some replacement expressions, making
		// sure that it returns the correct result expressions.
		
		def constantFoldingReplacement =
			rTerm( { (inCon { a, b -> a + b })( it.a, it.b ) } );
		
		shouldFail(
			IllegalArgumentException.class,
			{ replacementFor(
				[ a: con( 1 ), b: con( 2 ) * con( 3 ) ],
				constantFoldingReplacement
			) }
		);
		
		assertEquals(
			replacementFor(
				[ a: con( 1 ), b: con( 2 ) ],
				constantFoldingReplacement
			),
			con( 3 )
		);
		
		
		assertEquals(
			replacementFor(
				[ a: con( 1 ) * con( 2 ), b: con( 3 ) ],
				rTerm( "a" )
			),
			con( 1 ) * con( 2 )
		);
		
		
		assertEquals(
			replacementFor(
				[ a: con( 1 ), b: con( 2 ) ],
				rTerm( { null } )
			),
			null
		);
	}
	
	void testReplacementsFor()
	{
		// Test {@code replacementsFor} by using it for an example
		// application of pattern search-and-replace.
		
		def alternationMatcher = { SymbolicExpression... alternatives ->
			pTerm( { subject ->
				def matchResultList = [];
				alternatives.each { alternative ->
					for ( thisResult in matchesFor( alternative, subject ) )
					{
						matchResultList.add( thisResult );
					}
				};
				return matchResultList;
			} )
		};
		
		
		def distributiveReplacements = replacementsFor(
			alternationMatcher(
				pTerm( "a" ) * pTerm( "b" ),
				pTerm( "b" ) * pTerm( "a" )
			)
			+
			alternationMatcher(
				pTerm( "a" ) * pTerm( "c" ),
				pTerm( "c" ) * pTerm( "a" )
			),
			rTerm( "a" ) * (rTerm( "b" ) + rTerm( "c" )),
			con( 1 ) * con( 2 ) + con( 1 ) * con( 2 )
		).iterator();
		
		assertEquals(
			distributiveReplacements.next(),
			con( 1 ) * (con( 2 ) + con( 2 ))
		);
		
		assertEquals(
			distributiveReplacements.next(),
			con( 2 ) * (con( 1 ) + con( 1 ))
		);
		
		assertFalse( distributiveReplacements.hasNext() );
	}
	
	void testFirstReplacementFor()
	{
		// Test {@code firstReplacementsFor} by using it for an example
		// application of pattern search-and-replace.
		
		def alternationMatcher = { SymbolicExpression... alternatives ->
			pTerm( { subject ->
				def matchResultList = [];
				alternatives.each { alternative ->
					for ( thisResult in matchesFor( alternative, subject ) )
					{
						matchResultList.add( thisResult );
					}
				};
				return matchResultList;
			} )
		};
		
		
		def distributiveReplacement = firstReplacementFor(
			alternationMatcher(
				pTerm( "a" ) * pTerm( "b" ),
				pTerm( "b" ) * pTerm( "a" )
			)
			+
			alternationMatcher(
				pTerm( "a" ) * pTerm( "c" ),
				pTerm( "c" ) * pTerm( "a" )
			),
			rTerm( "a" ) * (rTerm( "b" ) + rTerm( "c" )),
			con( 1 ) * con( 2 ) + con( 1 ) * con( 2 )
		);
		
		assertEquals(
			distributiveReplacement,
			con( 1 ) * (con( 2 ) + con( 2 ))
		);
	}
	
	void testReplaceRepeatedly()
	{
		// Test {@code replaceRepeatedly} by using it for an example application
		// of pattern search-and-replace.
		
		assertFalse(
			con( 1 ) + (con( 2 ) + (con( 3 ) + (con( 4 ) + con( 5 ))))
				== con( 1 ) + con( 2 ) + con( 3 ) + con( 4 ) + con( 5 )
		);
		
		assertEquals(
			replaceRepeatedly(
				pTerm( "a" ) + (pTerm( "b" ) + pTerm( "c" )),
				rTerm( "a" ) + rTerm( "b" ) + rTerm( "c" ),
				con( 1 ) + (con( 2 ) + (con( 3 ) + (con( 4 ) + con( 5 ))))
			),
			con( 1 ) + con( 2 ) + con( 3 ) + con( 4 ) + con( 5 )
		);
	}
	
	void testReplacementsAnywhereFor()
	{
		// Test {@code replacementsAnywhereFor} by using it to demonstrate an
		// example application of pattern search-and-replace.
		
		def plusToDivResults = replacementsAnywhereFor(
			pTerm( "a" ) + pTerm( "b" ),
			rTerm( "a" ) / rTerm( "b" ),
			con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 )) + con( 5 )
		).iterator();
		
		[
			(con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 ))) / con( 5 ),
			(con( 1 ) + con( 2 )) / (con( 3 ) + con( 4 )) + con( 5 ),
			con( 1 ) / con( 2 ) + (con( 3 ) + con( 4 )) + con( 5 ),
			con( 1 ) + con( 2 ) + con( 3 ) / con( 4 ) + con( 5 )
		].each {
			assertEquals( it, plusToDivResults.next() );
		};
		assertFalse( plusToDivResults.hasNext() );
	}
	
	void testFirstReplacementAnywhereFor()
	{
		// Test {@code firstReplacementAnywhereFor} by using it for an example
		// application of pattern search-and-replace.
		
		def alternationMatcher = { SymbolicExpression... alternatives ->
			pTerm( { subject ->
				def matchResultList = [];
				alternatives.each { alternative ->
					for ( thisResult in matchesFor( alternative, subject ) )
					{
						matchResultList.add( thisResult );
					}
				};
				return matchResultList;
			} )
		};
		
		
		def distributiveReplacement = firstReplacementAnywhereFor(
			alternationMatcher(
				pTerm( "a" ) * pTerm( "b" ),
				pTerm( "b" ) * pTerm( "a" )
			)
			+
			alternationMatcher(
				pTerm( "a" ) * pTerm( "c" ),
				pTerm( "c" ) * pTerm( "a" )
			),
			rTerm( "a" ) * (rTerm( "b" ) + rTerm( "c" )),
			con( 1 ) * con( 2 ) + con( 1 ) * con( 2 ) + con( 1 ) * con( 3 )
		);
		
		assertEquals(
			distributiveReplacement,
			con( 1 ) * (con( 2 ) + con( 2 )) + con( 1 ) * con( 3 )
		);
	}
	
	void testReplaceAnywhereRepeatedly()
	{
		// Test {@code replaceAnywhereRepeatedly} by using it for example
		// applications of pattern search-and-replace.
		
		def alternationMatcher = { SymbolicExpression... alternatives ->
			pTerm( { subject ->
				def matchResultList = [];
				alternatives.each { alternative ->
					for ( thisResult in matchesFor( alternative, subject ) )
					{
						matchResultList.add( thisResult );
					}
				};
				return matchResultList;
			} )
		};
		
		
		def distributiveReplacement = replaceAnywhereRepeatedly(
			alternationMatcher(
				pTerm( "a" ) * pTerm( "b" ),
				pTerm( "b" ) * pTerm( "a" )
			)
			+
			alternationMatcher(
				pTerm( "a" ) * pTerm( "c" ),
				pTerm( "c" ) * pTerm( "a" )
			),
			rTerm( "a" ) * (rTerm( "b" ) + rTerm( "c" )),
			con( 1 ) * con( 2 ) + con( 1 ) * con( 2 ) + con( 1 ) * con( 3 )
		);
		
		// Note that the specific order of operations is of interest here. The
		// above expression is (((1*2)+(1*2))+(1*3)), so the transformation from
		// ((1*2)+(1*2)) to (1*(2+2)), the only transformation possible, will be
		// performed first, followed by the transformation from
		// ((1*(2+2))+(1*3)) to (1*((2+2)+3)).
		
		assertEquals(
			distributiveReplacement,
			con( 1 ) * (con( 2 ) + con( 2 ) + con( 3 ))
		);
		
		
		def diabolicalDistributiveReplacement = replaceAnywhereRepeatedly(
			alternationMatcher(
				pTerm( "a" ) * pTerm( "b" ),
				pTerm( "b" ) * pTerm( "a" )
			)
			+
			alternationMatcher(
				pTerm( "a" ) * pTerm( "c" ),
				pTerm( "c" ) * pTerm( "a" )
			),
			rTerm( "a" ) * (rTerm( "b" ) + rTerm( "c" )),
			con( 1 ) * con( 2 ) + con( 1 ) * con( 2 ) + con( 3 ) * con( 2 )
		);
		
		// Here, the eagerness of the search-and-replace mechanism prevents this
		// expression from being simplified as well as one might expect. The
		// above expression is (((1*2)+(1*2))+(3*2)), so the transformation from
		// ((1*2)+(1*2)) to (1*(2+2)) will be performed first, making it
		// impossible to make any further replacements.
		
		assertEquals(
			diabolicalDistributiveReplacement,
			con( 1 ) * (con( 2 ) + con( 2 )) + con( 3 ) * con( 2 )
		);
	}
	
	void testReplaceAll()
	{
		// Test {@code replaceAll} by using it for example applications of
		// pattern search-and-replace.
		
		def plusToDivTrippedUpResult = replaceAll(
			pTerm( "a" ) + pTerm( "b" ),
			rTerm( "a" ) / rTerm( "b" ),
			con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 )) + con( 5 )
		);
		
		assertEquals(
			plusToDivTrippedUpResult,
			(con( 1 ) + con( 2 ) + (con( 3 ) + con( 4 ))) / con( 5 )
		);
		
		
		def plusToDivResult = replaceAll(
			pTerm( "a" ) + pTerm( "b" ),
			rTerm( "a" ) / rTerm( "b" ),
			con( 1 ) + con( 2 ) - (con( 3 ) + con( 4 )) - con( 5 )
		);
		
		assertEquals(
			plusToDivResult,
			(con( 1 ) / con( 2 ) - con( 3 ) / con( 4 )) - con( 5 )
		);
	}
}