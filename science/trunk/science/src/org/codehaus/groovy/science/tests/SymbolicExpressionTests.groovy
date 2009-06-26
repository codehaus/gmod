package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.OverloadableOperators
import org.codehaus.groovy.science.SymbolicExpression

import static org.codehaus.groovy.science.SymbolicExpression.*


class SymbolicExpressionTests extends GroovyTestCase
{
	void testBadExpression()
	{
		// Make several failed attempts to construct an expression.
		
		this.shouldFail(
			NullPointerException.class,
			{ new SymbolicExpression( null, null ) }
		);
		
		this.shouldFail(
			NullPointerException.class,
			{ new SymbolicExpression( "dummy", null ) }
		);
		
		this.shouldFail(
			NullPointerException.class,
			{ new SymbolicExpression( "dummy", null, null ) }
		);
		
		this.shouldFail(
			NullPointerException.class,
			{ new SymbolicExpression( "dummy", [ null, null ] ) }
		);
		
		this.shouldFail( NullPointerException.class, { expr( null ) } );
		
		this.shouldFail( NullPointerException.class, { expr( null, null ) } );
		
		this.shouldFail(
			NullPointerException.class,
			{ expr( "dummy", null ) }
		);
		
		this.shouldFail(
			NullPointerException.class,
			{ expr( "dummy", null, null ) }
		);
		
		this.shouldFail(
			NullPointerException.class,
			{ expr( "dummy", [ null, null ] ) }
		);
	}
	
	void testNullaryExpression()
	{
		// Make a simple expression that uses a nullary operator.
		
		def nullaryOperator = "dummy";
		def dummy = new SymbolicExpression( nullaryOperator, [] );
		
		assertEquals( dummy, dummy );
		assertEquals( dummy, expr( "dummy" ) );
		assertEquals( dummy, expr( nullaryOperator ) );
		assertEquals( dummy, expr( nullaryOperator, [] ) );
		assertToString( dummy, "<< dummy: [] >>" );
		assertSame( dummy.getOperator(), nullaryOperator );
		assertEquals( dummy.getArgumentList(), [] );
	}
	
	void testCompoundExpression()
	{
		// Make a somewhat more complicated expression by combining a simple one
		// with itself using the {@code +} operator.
		
		def nullaryOperator = "dummy";
		def dummy = new SymbolicExpression( nullaryOperator, [] );
		def sum = dummy + dummy + dummy;
		
		assertEquals( sum, sum );
		assertToString(
			sum,
			"<< Plus: [" +
				"<< Plus: [<< dummy: [] >>, << dummy: [] >>] >>" +
				", " +
				"<< dummy: [] >>" +
			"] >>"
		);
		assertSame( sum.getOperator(), OverloadableOperators.Plus );
		assertEquals( sum.getArgumentList(), [ dummy + dummy, dummy ] );
	}
	
	void testExpressionOperatorOverloading()
	{
		// Try out every single one of the overloaded operators on
		// {@code SymbolicExpression}.
		
		def x = new SymbolicExpression( "x", [] );
		def y = new SymbolicExpression( "y", [] );
		def z = new SymbolicExpression( "z", [] );
		
		
		def operatorList =
			new ArrayList( Arrays.asList( OverloadableOperators.values() ) );
		
		
		assertEquals( 
			x + y,
			new SymbolicExpression( OverloadableOperators.Plus, [x, y] )
		);
		operatorList.remove( OverloadableOperators.Plus );
		
		assertEquals(
			x - y,
			new SymbolicExpression( OverloadableOperators.Minus, [x, y] )
		);
		operatorList.remove( OverloadableOperators.Minus );
		
		assertEquals(
			x * y,
			new SymbolicExpression( OverloadableOperators.Multiply, [x, y] )
		);
		operatorList.remove( OverloadableOperators.Multiply );
		
		assertEquals(
			x ** y,
			new SymbolicExpression( OverloadableOperators.Power, [x, y] )
		);
		operatorList.remove( OverloadableOperators.Power );
		
		assertEquals(
			x / y,
			new SymbolicExpression( OverloadableOperators.Div, [x, y] )
		);
		operatorList.remove( OverloadableOperators.Div );
		
		assertEquals(
			x % y,
			new SymbolicExpression( OverloadableOperators.Mod, [x, y] )
		);
		operatorList.remove( OverloadableOperators.Mod );
		
		assertEquals(
			x | y,
			new SymbolicExpression( OverloadableOperators.Or, [x, y] )
		);
		operatorList.remove( OverloadableOperators.Or );
		
		assertEquals(
			x & y,
			new SymbolicExpression( OverloadableOperators.And, [x, y] )
		);
		operatorList.remove( OverloadableOperators.And );
		
		assertEquals(
			x ^ y,
			new SymbolicExpression( OverloadableOperators.Xor, [x, y] )
		);
		operatorList.remove( OverloadableOperators.Xor );
		
		assertEquals(
			x[ y ],
			new SymbolicExpression( OverloadableOperators.GetAt, [x, y] )
		);
		operatorList.remove( OverloadableOperators.GetAt );
		
		assertEquals(
			x << y,
			new SymbolicExpression( OverloadableOperators.LeftShift, [x, y] )
		);
		operatorList.remove( OverloadableOperators.LeftShift );
		
		assertEquals(
			x >> y,
			new SymbolicExpression( OverloadableOperators.RightShift, [x, y] )
		);
		operatorList.remove( OverloadableOperators.RightShift );
		
		assertEquals(
			~x,
			new SymbolicExpression( OverloadableOperators.BitwiseNegate, [x] )
		);
		operatorList.remove( OverloadableOperators.BitwiseNegate );
		
		assertEquals(
			-x,
			new SymbolicExpression( OverloadableOperators.Negative, [x] )
		);
		operatorList.remove( OverloadableOperators.Negative );
		
		assertEquals(
			+x,
			new SymbolicExpression( OverloadableOperators.Positive, [x] )
		);
		operatorList.remove( OverloadableOperators.Positive );
		
		
		assert operatorList.isEmpty();
	}
	
	void testCompoundExpressionTraversal()
	{
		// Traverse a somewhat complicated expression and render it in a custom
		// string presentation.
		
		def nullaryOperator = "dummy";
		def dummy = new SymbolicExpression( nullaryOperator, [] );
		def sum = dummy + dummy + dummy;
		
		
		// This closure performs a recursive traversal of an expression.
		def customExpressionToString;
		customExpressionToString = { expression ->
			
			def argumentList = expression.getArgumentList();
			
			// If an expression has no arguments, represent it as its operator's
			// {@code toString} value.
			if ( argumentList.size() == 0 )
			{
				return expression.getOperator().toString();
			}
			
			// If an expression is a binary addition, represent it using
			// something like {@code "x + y"}.
			if (
				expression.getOperator().is( OverloadableOperators.Plus )
				&&
				(argumentList.size() == 2)
			)
			{
				return (
					customExpressionToString( argumentList[ 0 ] )
					+ " + "
					+ customExpressionToString( argumentList[ 1 ] )
				);
			}
			
			return expression.toString();
		}
		
		
		assertEquals(
			customExpressionToString( sum ),
			"dummy + dummy + dummy"
		);
	}
}