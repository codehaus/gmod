package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.ConstantOperator
import org.codehaus.groovy.science.CumulativeExpressionValidator
import org.codehaus.groovy.science.OverloadableOperators
import org.codehaus.groovy.science.SymbolicExpression

import static org.codehaus.groovy.science.ConstantOperator.*
import static org.codehaus.groovy.science.SymbolicExpression.*


class CumulativeExpressionValidatorTests extends GroovyTestCase
{
	void testValidator()
	{
		// Make a {@code CumulativeExpressionValidator}, and run several
		// expressions through it to make sure that they work.
		//
		// TODO: Put in some more tests. Not all of the constructors and
		// methods are represented here.
		
		def nullaryOperator = "x";
		def dummy = new SymbolicExpression( nullaryOperator, [] );
		
		def validator = new CumulativeExpressionValidator( false );
				
		validator |= dummy;
		
		validator |= { (
			it.getOperator().is( OverloadableOperators.Plus )
			&&
			(it.getArgumentList().size() == 2)
		) };
		
		
		assert   validator.validates(
			new SymbolicExpression( nullaryOperator, [] )
		);
		assert  !validator.validates(
			new SymbolicExpression( nullaryOperator, [ dummy ] )
		);
		assert  !validator.validates(
			new SymbolicExpression( nullaryOperator, [ dummy, dummy ] )
		);
		
		assert  !(~validator).validates(
			new SymbolicExpression( nullaryOperator, [] )
		);
		assert   (~validator).validates(
			new SymbolicExpression( nullaryOperator, [ dummy ] )
		);
		
		assert  !validator.validates(
			new SymbolicExpression( OverloadableOperators.Plus, [] )
		);
		assert  !validator.validates(
			new SymbolicExpression( OverloadableOperators.Plus, [ dummy ] )
		);
		assert   validator.validates(
			new SymbolicExpression(
				OverloadableOperators.Plus,
				[ dummy, dummy ]
			)
		);
		assert  !validator.validates(
			new SymbolicExpression(
				OverloadableOperators.Plus,
				[ dummy, dummy, dummy ]
			)
		);
		
		assert   validator.validates( dummy );
		assert   validator.validates( dummy + dummy );
		assert   validator.validates( dummy + dummy + dummy );
	}
	
	void testBasicAlgebraRepresentation()
	{
		// Put together some example algebraic expressions and make sure they
		// fit in real number or boolean contexts, as appropriate.
		//
		// TODO: Put in more actual tests. Only a few parts of the interface are
		// actually required here, and they are essentially all that is used.
		
		
		// First, define the ways that expressions are to be constructed.
		def additionOp        = OverloadableOperators.Plus;
		def subtractionOp     = OverloadableOperators.Minus;
		def multiplicationOp  = OverloadableOperators.Multiply;
		def divisionOp        = OverloadableOperators.Div;
		def exponentiationOp  = OverloadableOperators.Power;
		def negativeOp        = OverloadableOperators.Negative;
		def disjunctionOp     = OverloadableOperators.Or;
		def implicationOp     = OverloadableOperators.RightShift;
		
		def equalityOp = "equality";
		def equality = { first, second ->
			new SymbolicExpression( equalityOp, [ first, second ] )
		};
		
		def identifierOp = new Object();
		
		// shortcut for representing real-valued variables
		def real = {
			con( expr( identifierOp, con( Number ), con( it ) ) );
		};
		
		// shortcut for representing boolean-valued variables
		def bool = {
			con( expr( identifierOp, con( Boolean ), con( it ) ) );
		};
		
		// shortcut for representing constants
		def con = { new SymbolicExpression( new ConstantOperator( it ), [] ) };
		
		
		// Next, define the semantics of the expressions that will be
		// constructed.
		def numberContext = new CumulativeExpressionValidator( false );
		def booleanContext = new CumulativeExpressionValidator( false );
		
		numberContext.allowAlso(
			[
				additionOp,
				subtractionOp,
				multiplicationOp,
				divisionOp,
				exponentiationOp
			],
			[ numberContext, numberContext ]
		);
		numberContext.allowAlso( negativeOp, [ numberContext ] );
		booleanContext.allowAlso(
			[
				disjunctionOp,
				implicationOp,
				equalityOp
			],
			[ booleanContext, booleanContext ]
		);
		booleanContext.allowAlso( equalityOp, [ numberContext, numberContext ] );
		
		numberContext.allowAlso(
			{ (
				(it instanceof ConstantOperator)
				&&
				(it.value instanceof SymbolicExpression)
				&&
				(it.value.operator == identifierOp)
				&&
				(it.value.argumentList.size() == 2)
				&&
				(
					it.value.argumentList[ 0 ].operator
					instanceof ConstantOperator
				)
				&&
				(it.value.argumentList[ 0 ].operator.value == Number)
			) },
			[]
		);
		booleanContext.allowAlso(
			{ (
				(it instanceof ConstantOperator)
				&&
				(it.value instanceof SymbolicExpression)
				&&
				(it.value.operator == identifierOp)
				&&
				(it.value.argumentList.size() == 2)
				&&
				(
					it.value.argumentList[ 0 ].operator
					instanceof ConstantOperator
				)
				&&
				(it.value.argumentList[ 0 ].operator.value == Boolean)
			) },
			[]
		);
		
		numberContext.allowAlso(
			{ (
				(it instanceof ConstantOperator)
				&&
				(it.getValue() instanceof Number)
			) },
			[]
		);
		booleanContext.allowAlso(
			{ (
				(it instanceof ConstantOperator)
				&&
				(it.getValue() instanceof Boolean)
			) },
			[]
		);
		
		
		// Finally, make sure that some sample expressions do or do not fit the
		// defined semantics.
		
		def a = real( "a" );
		def b = real( "b" );
		def c = real( "c" );
		def x = real( "x" );
		
		assert (
			(-b - (b ** con( 2 ) - con( 4 ) * a * c) ** con( 0.5 ))
			/ (con( 2 ) * a)
			in numberContext
		);
		assert (
			equality( a * x ** con( 2 ) + b * x + c, con( 0 ) )
			>>
			(
    			equality(
    				x,
    				(-b + (b ** con( 2 ) - con( 4 ) * a * c) ** con( 0.5 ))
    				/ (con( 2 ) * a)
    			)
    			|
    			equality(
    				x,
    				(-b - (b ** con( 2 ) - con( 4 ) * a * c) ** con( 0.5 ))
    				/ (con( 2 ) * a)
    			)
    		)
			in booleanContext
		);
		assertFalse( con( 3 ) + con( true ) in numberContext );
		assertFalse( con( 3 ) + con( true ) in booleanContext );
		
		
		def p = bool( "p" );
		def q = bool( "q" );
		def r = bool( "r" );
		
		assert (
			equality(
				equality( q, r ),
				equality( (p >> q) >> r, (p >> r) >> q )
			)
			in booleanContext
		);
	}
}