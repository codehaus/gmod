package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.ConstantOperator
import org.codehaus.groovy.science.CumulativeExpressionEvaluator
import org.codehaus.groovy.science.OverloadableOperators
import org.codehaus.groovy.science.SymbolicExpression

import static org.codehaus.groovy.science.ConstantOperator.*
import static org.codehaus.groovy.science.CumulativeExpressionEvaluator.*
import static org.codehaus.groovy.science.SymbolicExpression.*


class CumulativeExpressionEvaluatorTests extends GroovyTestCase
{
	void testOperatorTranslator()
	{
		// Demonstrate the use of {@code CumulativeExpressionEvaluator} to make
		// a simple tool that translates some of the syntactical operators in
		// {@code OverloadableOperators} to operators that have more
		// domain-specific meanings.
		
		
		// Define a simple constant that will translate to itself.
		def piOp = new ConstantOperator( Math.PI );
		def pi = expr( piOp );
		
		// Lay out which operators will be translated to which other operators.
		def sumOp         = "sum";
		def differenceOp  = "difference";
		def productOp     = "product";
		def quotientOp    = "quotient";
		def operatorTranslations = [
			(OverloadableOperators.Plus):      sumOp,
			(OverloadableOperators.Minus):     differenceOp,
			(OverloadableOperators.Multiply):  productOp,
			(OverloadableOperators.Div):       quotientOp
		];
		
		
		// Make a closure implementation of an operator translator.
		
		def algebraicExpression1;
		
		def handlers = [:];
		
		handlers.put( piOp, {
			if ( !it.isEmpty() )
				return null;
			
			return pi;
		} );
		
		operatorTranslations.each { key, value ->
			handlers.put( key, { arguments ->
				if ( arguments.size() != 2 )
					return null;
				
				def newArguments = arguments.collect( algebraicExpression1 );
				
				if ( newArguments.any { it == null } )
					return null;
				
				return expr( value, newArguments );
			} );
		};
		
		algebraicExpression1 = {
			def handler = handlers[ it.getOperator() ];
			
			if ( handler == null )
				return null;
			
			return handler( it.getArgumentList() );
		};
		
		
		// Now make an identical operator translator using
		// {@code CumulativeExpressionEvaluator}.
		
		def algebraicExpression2 = new CumulativeExpressionEvaluator();
		
		algebraicExpression2.setBehaviorCase( pi, { it } );
		
		algebraicExpression2.setBehavior(
			{ operatorTranslations[ it ] },
			[ algebraicExpression2, algebraicExpression2 ],
			{ it }
		);
		
		
		// Make sure that both tools yield the same translations.
		
		def testExpression = pi + pi - pi * pi / pi;
		def goalExpression = expr(
			differenceOp,
			expr( sumOp, pi, pi ),
			expr( quotientOp, expr( productOp, pi, pi ), pi )
		);
		
		assertFalse( testExpression == goalExpression );
		assertEquals( algebraicExpression1( testExpression ), goalExpression );
		assertEquals( algebraicExpression2( testExpression ), goalExpression );
	}
	
	void testExpressionRenderer()
	{
		// Demonstrate the use of {@code CumulativeExpressionEvaluator} to make
		// a simple tool that renders an expression using a custom
		// {@code String} representation.
		
		
		// Define a simple constant for use in building sample expressions.
		def piOp = new ConstantOperator( Math.PI );
		def pi = expr( piOp );
		
		// Specify the {@code String} renderings of each of the possible
		// operators.
		def operatorToString = [
			(OverloadableOperators.Plus)      : "+",
			(OverloadableOperators.Minus)     : "-",
			(OverloadableOperators.Multiply)  : "*",
			(OverloadableOperators.Div)       : "/"
		];
		
		
		// Make a closure implementation of an expression renderer.
		
		def expressionToString1;
		
		def handlers = [:];
		
		handlers.put( piOp, { "pi" } );
		
		operatorToString.each { key, value ->
			handlers.put( key, {
				if ( it.size() != 2 )
					return null;
				
				return (
					"("
					+ expressionToString1( it[ 0 ] )
					+ " $value "
					+ expressionToString1( it[ 1 ] )
					+ ")"
				);
			} )
		}
		
		expressionToString1 = {
			def handler = handlers[ it.getOperator() ];
			
			if ( handler == null )
				return null;
			
			return handler( it.getArgumentList() );
		};
		
		
		// Now make an identical expression renderer using
		// {@code CumulativeExpressionEvaluator}.
		
		def expressionToStringEvaluator = new CumulativeExpressionEvaluator();
	
		expressionToStringEvaluator.setBehaviorCase( pi, { con( "pi" ) } );
		
		operatorToString.each { key, value ->
			expressionToStringEvaluator.setBehavior(
				filter( key ),
				[
					expressionToStringEvaluator,
					expressionToStringEvaluator
				],
				withArgs( inCon { a, b -> "($a $value $b)" } )
			);
		}
		
		def expressionToString2 = {
			unCon( expressionToStringEvaluator( it ) )
		};
		
		
		// Make sure that both tools yield the same renderings.
		
		def testExpression = pi + pi - pi * pi / pi;
		def goalString = "((pi + pi) - ((pi * pi) / pi))";
		
		assertEquals( expressionToString1( testExpression ), goalString );
		assertEquals( expressionToString2( testExpression ), goalString );
	}
	
	void testConstantFolder()
	{
		// Demonstrate the use of {@code CumulativeExpressionEvaluator} to
		// evaluate any expression involving only constant values by
		// interpreting all {@code OverloadableOperators} as if they meant
		// exactly what they mean in Groovy.
		
		def constantFolder = new CumulativeExpressionEvaluator();
		
		constantFolder.setBehaviorCase( ConstantOperator.class, [], { it } );
		
		def operatorBehaviors = [
			(OverloadableOperators.Plus):           { a, b -> a + b },
			(OverloadableOperators.Minus):          { a, b -> a - b },
			(OverloadableOperators.Multiply):       { a, b -> a * b },
			(OverloadableOperators.Power):          { a, b -> a ** b },
			(OverloadableOperators.Div):            { a, b -> a / b },
			(OverloadableOperators.Mod):            { a, b -> a % b },
			(OverloadableOperators.Or):             { a, b -> a | b },
			(OverloadableOperators.And):            { a, b -> a & b },
			(OverloadableOperators.Xor):            { a, b -> a ^ b },
			(OverloadableOperators.GetAt):          { a, b -> a[ b ] },
			(OverloadableOperators.PutAt):          { a, b, c -> a[ b ] = c },
			(OverloadableOperators.LeftShift):      { a, b -> a << b },
			(OverloadableOperators.RightShift):     { a, b -> a >> b },
			(OverloadableOperators.BitwiseNegate):  { a -> ~a },
			(OverloadableOperators.Negative):       { a -> -a },
			(OverloadableOperators.Positive):       { a -> +a }
		];
		
		operatorBehaviors.each { key, value ->
			constantFolder.setBehavior(
		    	filter( key ),
		    	[ constantFolder ] * value.getMaximumNumberOfParameters(),
		    	withArgs( inCon( value ) )
		    );
		}
		
		
		assertEquals(
			unCon( constantFolder(
				con( 1 )
			) ),
			1
		);
		
		assertEquals(
			unCon( constantFolder(
				con( 1 ) + con( 2 ) - con( 3 ) * con( 4 ) / con( 5 )
			) ),
			0.6
		);
		
		def co2 = { con( con( it ) ) };
		
		assertEquals(
			unCon( constantFolder(
				co2( 1 ) + co2( 2 ) - co2( 3 ) * co2( 4 ) / co2( 5 )
			) ),
			con( 1 ) + con( 2 ) - con( 3 ) * con( 4 ) / con( 5 )
		);
	}
}