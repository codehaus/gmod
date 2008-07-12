package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.CumulativeExpressionEvaluator
import org.codehaus.groovy.science.CumulativeExpressionValidator
import org.codehaus.groovy.science.ConstantOperator
import org.codehaus.groovy.science.OverloadableOperators
import org.codehaus.groovy.science.SymbolicExpression

import static org.codehaus.groovy.science.ConstantOperator.*
import static org.codehaus.groovy.science.CumulativeExpressionEvaluator.*
import static org.codehaus.groovy.science.PatternTermOperator.*
import static org.codehaus.groovy.science.ReplacementTermOperator.*
import static org.codehaus.groovy.science.SymbolicExpression.*


// NOTE: This example is still a work in progress!

// TODO: Break up the code into pieces that can be individually described and
//       digested.

// TODO: Comment.

// TODO: Figure out whether certain techniques that had to be done the hard way
//       for this example deserve to actually be parts of the library.
//
//       For example, I had to write a special wrapper function for
//       {@code CumulativeExpressionEvaluator} to help me use pattern
//       search-and-replace. The
//       {@code ( operator, [ argument, argument ], result )} interface was
//       meant to make exactly this sort of thing easier, but it looks like in
//       practice (at least for me), it's much more intuitive, expressive, and
//       useful to just give a pattern expression as the template.
//
//       Meanwhile, pattern search-and-replace things like {@code pJump} and
//       {@code replaceAll} that let you search every subexpression of something
//       looked like they could have been useful, but now I think they step a
//       little bit too far. If every bit of an expression is subject to being
//       replaced, then in order to store symbolic data within the expression
//       without letting it be subject to the same replacement process, it has
//       to be hidden by storing that whole {@code SymbolicExpression} inside of
//       a {@code ConstantOperator} or something. On the other hand,
//       {@code CumulativeExpressionValidator} and
//       {@code CumulativeExpressionEvaluator} were designed from the get-go
//       with the assumption that a single {@code SymbolicExpression} could
//       potentially be used to organize and store all kinds of different
//       symbolic data all in one place. A kind of {@code replaceAll} technique
//       that deals with this kind of situation (maybe by searching only as deep
//       as a particular {@code CumulativeExpressionValidator} allows) could
//       help bridge the gap between these two expression manipulation styles.


class DifferentiationExampleTests extends GroovyTestCase
{
	void testDifferentiationExample()
	{
		// Demonstrate how to set up an expression simplifier
		
		def addPatternReplace = { evaluator, pattern, replacement ->
			
			evaluator.setBehavior( { expression ->
				
				def result = firstReplacementAnywhereFor(
					pattern,
					replacement,
					expression
				);
				
				if ( result == expression )
					return null;
				
				return result;
			} );
		};
		
		def pCase = { condition, name = null ->
			
			return pTerm( { candidate ->
				
				if ( candidate == null )
					return null;
				
				if ( !(candidate in condition) )
					return null;
				
				if ( name == null )
					return [:];
				
				return [ (name): candidate ];
			} );
		};
		
		def pCon = { name = null, condition = { true } -> pCase(
			{ (
				(it.operator in ConstantOperator)
				&&
				it.argumentList.isEmpty()
				&&
				(unCon( it ) in condition)
			) },
			name
		) };
		
		def pCNum = { pCon( it, Number ) };
		
		def identifierOp = new Object();
		
		def iNum =
			{ name -> expr( identifierOp, con( Number ), con( name ) ) };
		
		def nameOfINum = { it.argumentList[ 1 ] };
		
		def pINum = { name = null -> pCase(
			{
				return matchesExistFor(
					expr( identifierOp, con( Number ), pCon( name ) ),
					it
				);
			},
			name
		) };
		
		def allowAlsoPattern = { validator, pattern ->
			validator.allowAlso( { matchesExistFor( pattern, it ) } );
		};
		
		def rIf = { condition, ifTrue, ifFalse = { null } ->
			
			return rTerm( { matchResult -> replacementFor(
				matchResult,
				condition( matchResult ) ? ifTrue : ifFalse
			) } );
		};
		
		def numberContext = new CumulativeExpressionValidator( false );
		
		def pNum = { name = null -> pCase( numberContext, name ) };
		
		numberContext.allowAlso(
			{ (it in ConstantOperator) && (it.value in Number) },
			[]
		);
		numberContext.allowAlso(
			identifierOp,
			[ con( Number ), { it.operator in ConstantOperator } ]
		);
		allowAlsoPattern( numberContext, pNum() + pNum() );
		allowAlsoPattern( numberContext, pNum() - pNum() );
		allowAlsoPattern( numberContext, pNum() * pNum() );
		allowAlsoPattern( numberContext, pNum() / pNum() );
		allowAlsoPattern( numberContext, pNum() ** pNum() );
		
		def pNonconNum = { name -> pCase(
			{ (
				(it in numberContext)
				&&
				!matchesExistFor( pCNum( "a" ), it )
			) },
			name
		) };
		
		
		def diffOp = new Object();
		def sDiff =
			{ expression, variable -> expr( diffOp, expression, variable ) };
		
		def simplify = new CumulativeExpressionEvaluator();
		
		addPatternReplace(
			simplify,
			pCNum( "a" ) + pCNum( "b" ),
			rTerm( { con( unCon( it.a ) + unCon( it.b ) ) } )
		);
		addPatternReplace(
			simplify,
			pCNum( "a" ) - pCNum( "b" ),
			rTerm( { con( unCon( it.a ) - unCon( it.b ) ) } )
		);
		addPatternReplace(
			simplify,
			pCNum( "a" ) * pCNum( "b" ),
			rTerm( { con( unCon( it.a ) * unCon( it.b ) ) } )
		);
		addPatternReplace(
			simplify,
			pCNum( "a" ) / pCNum( "b" ),
			rTerm( { con( unCon( it.a ) / unCon( it.b ) ) } )
		);
		addPatternReplace(
			simplify,
			pCNum( "a" ) ** pCNum( "b" ),
			rTerm( { con( unCon( it.a ) ** unCon( it.b ) ) } )
		);
		addPatternReplace(
			simplify,
			pCNum( "a" ) + pNonconNum( "b" ),
			rTerm( "b" ) + rTerm( "a" )
		);
		addPatternReplace(
			simplify,
			pNonconNum( "a" ) * pCNum( "b" ),
			rTerm( "b" ) * rTerm( "a" )
		);
		addPatternReplace(
			simplify,
			pNum( "a" ) + (pNum( "b" ) + pNum( "c" )),
			rTerm( "a" ) + rTerm( "b" ) + rTerm( "c" )
		);
		addPatternReplace(
			simplify,
			pNum( "a" ) * (pNum( "b" ) * pNum( "c" )),
			rTerm( "a" ) * rTerm( "b" ) * rTerm( "c" )
		);
		addPatternReplace(
			simplify,
			pNum( "a" ) / pNum( "b" ) / pNum( "c" ),
			rTerm( "a" ) / (rTerm( "b" ) * rTerm( "c" ))
		);
		addPatternReplace(
			simplify,
			pNum( "a" ) / (pNum( "b" ) / pNum( "c" )),
			rTerm( "a" ) * rTerm( "c" ) / rTerm( "b" )
		);
		addPatternReplace(
			simplify,
			(pNum( "a" ) + pNum( "b" )) * pNum( "c" ),
			rTerm( "c" ) * (rTerm( "a" ) + rTerm( "b" ))
		);
		addPatternReplace(
			simplify,
			(pNum( "a" ) - pNum( "b" )) * pNum( "c" ),
			rTerm( "c" ) * (rTerm( "a" ) - rTerm( "b" ))
		);
		addPatternReplace( simplify, con( 0 ) * pNum( "a" ), con( 0 ) );
		addPatternReplace( simplify, pNum( "a" ) + con( 0 ), rTerm( "a" ) );
		addPatternReplace( simplify, pNum( "a" ) - con( 0 ), rTerm( "a" ) );
		addPatternReplace( simplify, con( 1 ) * pNum( "a" ), rTerm( "a" ) );
		addPatternReplace( simplify, pNum( "a" ) ** con( 1 ), rTerm( "a" ) );
		addPatternReplace(
			simplify,
			pNonconNum( "a" ) + pTerm( "a" ),
			con( 2 ) * rTerm( "a" )
		);
		addPatternReplace(
			simplify,
			pNum( "a" ) + pNonconNum( "b" ) + pTerm( "b" ),
			rTerm( "a" ) + con( 2 ) * rTerm( "b" )
		);
		addPatternReplace(
			simplify,
			pNum( "a" ) * pNonconNum( "b" ) * pTerm( "b" ),
			rTerm( "a" ) * rTerm( "b" ) ** con( 2 )
		);
		addPatternReplace(
			simplify,
			pCNum( "a" ) * pNonconNum( "b" ) + pTerm( "b" ),
			rTerm( { con( unCon( it.a ) + 1 ) } ) * rTerm( "b" )
		);
		addPatternReplace(
			simplify,
			pNonconNum( "a" ) + pCNum( "b" ) * pTerm( "a" ),
			rTerm( { con( unCon( it.b ) + 1 ) } ) * rTerm( "a" )
		);
		addPatternReplace(
			simplify,
			pNum( "a" ) * (pNum( "b" ) + pNum( "c" )),
			rTerm( "a" ) * rTerm( "b" ) + rTerm( "a" ) * rTerm( "c" )
		);
		addPatternReplace(
			simplify,
			pNum( "a" ) * (pNum( "b" ) - pNum( "c" )),
			rTerm( "a" ) * rTerm( "b" ) - rTerm( "a" ) * rTerm( "c" )
		);
		addPatternReplace(
			simplify,
			pCNum( "a" ) * pNum( "b" ) + pCNum( "c" ) * pNum( "b" ),
			rTerm( { con( unCon( it.a ) + unCon( it.c ) ) } ) * rTerm( "b" )
		);
		addPatternReplace(
			simplify,
			sDiff( pCNum( "a" ), pINum( "x" ) ),
			con( 0 )
		);
		addPatternReplace(
			simplify,
			sDiff( pINum( "y" ), pINum( "x" ) ),
			rIf( { it.y == it.x }, con( 1 ), con( 0 ) )
		);
		addPatternReplace(
			simplify,
			sDiff( pNum( "a" ) + pNum( "b" ), pINum( "x" ) ),
			sDiff( rTerm( "a" ), rTerm( "x" ) )
			+ sDiff( rTerm( "b" ), rTerm( "x" ) )
		);
		addPatternReplace(
			simplify,
			sDiff( pNum( "a" ) - pNum( "b" ), pINum( "x" ) ),
			sDiff( rTerm( "a" ), rTerm( "x" ) )
			- sDiff( rTerm( "b" ), rTerm( "x" ) )
		);
		addPatternReplace(
			simplify,
			sDiff( pNum( "a" ) * pNum( "b" ), pINum( "x" ) ),
			sDiff( rTerm( "a" ), rTerm( "x" ) ) * rTerm( "b" )
			+ rTerm( "a" ) * sDiff( rTerm( "b" ), rTerm( "x" ) )
		);
		addPatternReplace(
			simplify,
			sDiff( pNum( "a" ) / pNum( "b" ), pINum( "x" ) ),
			(
				sDiff( rTerm( "a" ), rTerm( "x" ) ) * rTerm( "b" )
				- rTerm( "a" ) * sDiff( rTerm( "b" ), rTerm( "x" ) )
			)
			/ (rTerm( "b" ) ** con( 2 ))
		);
		addPatternReplace(
			simplify,
			sDiff( pINum( "x" ) ** pNum( "a" ), pINum( "x" ) ),
			rTerm( "a" ) * rTerm( "x" ) ** (rTerm( "a" ) - con( 1 ))
		);
		
		
		def diffExprToStringEval = new CumulativeExpressionEvaluator();
		
		diffExprToStringEval.setBehavior(
			filter( OverloadableOperators.Plus ),
			[ diffExprToStringEval, diffExprToStringEval ],
			withArgs( inCon { a, b -> "($a + $b)" } )
		);
		diffExprToStringEval.setBehavior(
			filter( OverloadableOperators.Minus ),
			[ diffExprToStringEval, diffExprToStringEval ],
			withArgs( inCon { a, b -> "($a - $b)" } )
		);
		diffExprToStringEval.setBehavior(
			filter( OverloadableOperators.Multiply ),
			[ diffExprToStringEval, diffExprToStringEval ],
			withArgs( inCon { a, b -> "($a * $b)" } )
		);
		diffExprToStringEval.setBehavior(
			filter( OverloadableOperators.Div ),
			[ diffExprToStringEval, diffExprToStringEval ],
			withArgs( inCon { a, b -> "($a / $b)" } )
		);
		diffExprToStringEval.setBehavior(
			filter( OverloadableOperators.Power ),
			[ diffExprToStringEval, diffExprToStringEval ],
			withArgs( inCon { a, b -> "($a ** $b)" } )
		);
		diffExprToStringEval.setBehavior(
			filter( diffOp ),
			[ diffExprToStringEval, diffExprToStringEval ],
			withArgs( inCon { a, b -> "diff( $a, $b )" } )
		);
		diffExprToStringEval.setBehavior( {
			def match = firstMatchFor( pINum( "a" ), it );
			
			if ( match == null )
				return null;
			
			return nameOfINum( match[ "a" ] );
		} );
		diffExprToStringEval.setBehavior( {
			def match = firstMatchFor( pCNum( "a" ), it );
			
			if ( match == null )
				return null;
			
			return match[ "a" ];
		} );
		
		def diffExprToString =
			{ unCon( diffExprToStringEval( it ) ).toString() };
		
		
		def iterateToFixedPoint = { initialValue, closure ->
			
			def thisValue = initialValue;
			
			while ( true )
			{
				def nextValue = closure( thisValue );
				
				if ( thisValue == nextValue )
					return thisValue;
				
				thisValue = nextValue;
			}
		};
		
		def completelySimplify = { expression ->
			
			return iterateToFixedPoint(
				expression,
				{
					def result = simplify( it );
					return ( (result == null) ? it : result );
				}
			);
		};
		
		assertEquals(
			diffExprToString( completelySimplify( con( 1 ) + con( 1 ) ) ),
			"2"
		);
		assertEquals(
			diffExprToString( completelySimplify(
				con( 1 ) + iNum( "x" )
			) ),
			"(x + 1)"
		);
		assertEquals(
			diffExprToString( completelySimplify(
				iNum( "x" ) + iNum( "x" ) + iNum( "x" )
			) ),
			"(3 * x)"
		);
		assertEquals(
			diffExprToString( completelySimplify(
				sDiff( con( 1 ) / iNum( "x" ), iNum( "x" ) )
			) ),
			"(-1 / (x ** 2))"
		);
		assertEquals(
			diffExprToString( completelySimplify( sDiff(
				iNum( "x" ) ** con( 2 ) * (con( 3 ) * iNum( "x" ) - con( 1 )),
				iNum( "x" )
			) ) ),
			"(((6 * (x ** 2)) - (2 * x)) + (3 * (x ** 2)))"
		);
	}
}