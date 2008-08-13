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
//       (Since this was initially written, I have implemented support for
//       pattern expressions in {@code CumulativeExpressionValidator} and
//       {@code CumulativeExpressionEvaluator}, and this example takes advantage
//       of those methods.)
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
		
		def pCase = { condition, name = null ->
			
			return pTerm(
				name,
				{ candidate ->
					
					if ( candidate == null )
						return null;
					
					if ( !(candidate in condition) )
						return null;
					
					if ( name == null )
						return [:];
					
					return [ (name): candidate ];
				}
			);
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
		
		def rIf = { condition, ifTrue, ifFalse = { null } ->
			
			return rTerm( { matchResult -> replacementFor(
				matchResult,
				condition( matchResult ) ? ifTrue : ifFalse
			) } );
		};
		
		def numberContext = new CumulativeExpressionValidator( false );
		
		def pNum = { name = null -> pCase( numberContext, name ) };
		
		numberContext.allowAlso( pCase( { (
			(it.operator in ConstantOperator)
			&&
			(it.operator.value in Number)
			&&
			it.argumentList.isEmpty()
		) } ) );
		numberContext.allowAlso( expr( identifierOp, con( Number ), pCon() ) );
		numberContext.allowAlso( pNum() + pNum() );
		numberContext.allowAlso( pNum() - pNum() );
		numberContext.allowAlso( pNum() * pNum() );
		numberContext.allowAlso( pNum() / pNum() );
		numberContext.allowAlso( pNum() ** pNum() );
		
		def pNonconNum = { name -> pCase(
			{ (
				(it in numberContext)
				&&
				!matchesExistFor( pCNum( "a" ), it )
			) },
			name
		) };
		
		
		def diffOp = new Object();
		def D =
			{ expression, variable -> expr( diffOp, expression, variable ) };
		
		
		def simplify = new CumulativeExpressionEvaluator();
		
		def aCon = pCNum( "a" );
		def bCon = pCNum( "b" );
		def cCon = pCNum( "c" );
		def aNoncon = pNonconNum( "a" );
		def bNoncon = pNonconNum( "b" );
		def a = pNum( "a" );
		def b = pNum( "b" );
		def c = pNum( "c" );
		def x = pINum( "x" );
		def y = pINum( "y" );
		def c0 = con( 0 );
		def c1 = con( 1 );
		def c2 = con( 2 );
		def c3 = con( 3 );
		
		simplify.setBehaviorAnywhere(
			aCon + bCon,
			rTerm( { con( unCon( it.a ) + unCon( it.b ) ) } )
		);
		simplify.setBehaviorAnywhere(
			aCon - bCon,
			rTerm( { con( unCon( it.a ) - unCon( it.b ) ) } )
		);
		simplify.setBehaviorAnywhere(
			aCon * bCon,
			rTerm( { con( unCon( it.a ) * unCon( it.b ) ) } )
		);
		simplify.setBehaviorAnywhere(
			aCon / bCon,
			rTerm( { con( unCon( it.a ) / unCon( it.b ) ) } )
		);
		simplify.setBehaviorAnywhere(
			aCon ** bCon,
			rTerm( { con( unCon( it.a ) ** unCon( it.b ) ) } )
		);
		simplify.setBehaviorAnywhere( aCon + bNoncon, b + a );
		simplify.setBehaviorAnywhere( aNoncon * bCon, b * a );
		simplify.setBehaviorAnywhere( a + (b + c), a + b + c );
		simplify.setBehaviorAnywhere( a * (b * c), a * b * c );
		simplify.setBehaviorAnywhere( a / b / c, a / (b * c) );
		simplify.setBehaviorAnywhere( a / (b / c), a * c / b );
		simplify.setBehaviorAnywhere( (a + b) * c, c * (a + b) );
		simplify.setBehaviorAnywhere( (a - b) * c, c * (a - b) );
		simplify.setBehaviorAnywhere( c0 * a, c0 );
		simplify.setBehaviorAnywhere( a + c0, a );
		simplify.setBehaviorAnywhere( a - c0, a );
		simplify.setBehaviorAnywhere( c1 * a, a );
		simplify.setBehaviorAnywhere( a ** c1, a );
		simplify.setBehaviorAnywhere( aNoncon + a, c2 * a );
		simplify.setBehaviorAnywhere( a + bNoncon + b, a + c2 * b );
		simplify.setBehaviorAnywhere( a * bNoncon * b, a * b ** c2 );
		simplify.setBehaviorAnywhere(
			aCon * bNoncon + b,
			rTerm( { con( unCon( it.a ) + 1 ) } ) * b
		);
		simplify.setBehaviorAnywhere(
			aNoncon + bCon * a,
			rTerm( { con( unCon( it.b ) + 1 ) } ) * a
		);
		simplify.setBehaviorAnywhere( a * (b + c), a * b + a * c );
		simplify.setBehaviorAnywhere( a * (b - c), a * b - a * c );
		simplify.setBehaviorAnywhere(
			aCon * b + cCon * b,
			rTerm( { con( unCon( it.a ) + unCon( it.c ) ) } ) * b
		);
		simplify.setBehaviorAnywhere( D( aCon, x ), c0 );
		simplify.setBehaviorAnywhere(
			D( y, x ),
			rIf( { it.y == it.x }, c1, c0 )
		);
		simplify.setBehaviorAnywhere( D( a + b, x ), D( a, x ) + D( b, x ) );
		simplify.setBehaviorAnywhere( D( a - b, x ), D( a, x ) - D( b, x ) );
		simplify.setBehaviorAnywhere(
			D( a * b, x ),
			D( a, x ) * b + a * D( b, x )
		);
		simplify.setBehaviorAnywhere(
			D( a / b, x ),
			(D( a, x ) * b - a * D( b, x )) / (b ** c2)
		);
		simplify.setBehaviorAnywhere( D( x ** a, x ), a * x ** (a - c1) );
		
		
		def diffExprToStringEval = new CumulativeExpressionEvaluator();
		
		def substringTerm = { name -> pTerm(
			name,
			{
				def result = diffExprToStringEval( it );
				
				if ( result == null )
					return null;
				
				return [ (name): unCon( result ) ];
			}
		) };
		
		def substringA = substringTerm( "a" );
		def substringB = substringTerm( "b" );
		
		diffExprToStringEval.setBehavior(
			substringA + substringB,
			rTerm( { con( "($it.a + $it.b)" ) } )
		);
		diffExprToStringEval.setBehavior(
			substringA - substringB,
			rTerm( { con( "($it.a - $it.b)" ) } )
		);
		diffExprToStringEval.setBehavior(
			substringA * substringB,
			rTerm( { con( "($it.a * $it.b)" ) } )
		);
		diffExprToStringEval.setBehavior(
			substringA / substringB,
			rTerm( { con( "($it.a / $it.b)" ) } )
		);
		diffExprToStringEval.setBehavior(
			substringA ** substringB,
			rTerm( { con( "($it.a ** $it.b)" ) } )
		);
		diffExprToStringEval.setBehavior(
			D( substringA, substringB ),
			rTerm( { con( "D( $it.a, $it.b )" ) } )
		);
		diffExprToStringEval.setBehavior(
			pINum( "a" ),
			rTerm( { nameOfINum( it.a ) } )
		);
		diffExprToStringEval.setBehavior(
			pCNum( "a" ),
			rTerm( { con( unCon( it.a ).toString() ) } )
		);
		
		def diffExprToString = { unCon( diffExprToStringEval( it ) ) };
		
		
		def simpToString =
			{ diffExprToString( simplify.evaluateRepeatedly( it ) ) };
		
		def t = iNum( "t" );
		
		assertEquals( simpToString( c1 + c1 ), "2" );
		assertEquals( simpToString( c1 + t ), "(t + 1)" );
		assertEquals( simpToString( t + t + t ), "(3 * t)" );
		assertEquals( simpToString( D( c1 / t, t ) ), "(-1 / (t ** 2))" );
		assertEquals(
			simpToString( D( t ** c2 * (c3 * t - c1), t ) ),
			"(((6 * (t ** 2)) - (2 * t)) + (3 * (t ** 2)))"
		);
	}
}