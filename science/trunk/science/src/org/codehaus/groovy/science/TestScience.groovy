package org.codehaus.groovy.science


class TestScience extends GroovyTestCase
{
	void testBadExpression()
	{
		// Make several failed attempts to construct an expression.
		
		this.shouldFail(
			NullPointerException,
			{ new SymbolicExpression( null, null ) }
		);
		this.shouldFail(
			NullPointerException,
			{ new SymbolicExpression( "dummy", null ) }
		);
		this.shouldFail(
			NullPointerException,
			{ new SymbolicExpression( "dummy", null, null ) }
		);
		this.shouldFail(
			NullPointerException,
			{ new SymbolicExpression( "dummy", [null, null] ) }
		);
	}
	
	void testNullaryExpression()
	{
		// Make a simple expression that uses a nullary operator.
		
		def nullaryOperator = "dummy";
		def dummy = new SymbolicExpression( nullaryOperator, [] );
		
		assertEquals( dummy, dummy );
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
			x[ y ] = z,
			new SymbolicExpression( OverloadableOperators.PutAt, [x, y, z] )
		);
		operatorList.remove( OverloadableOperators.PutAt );
		
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
	
	void testValidator()
	{
		// Make a {@code CumulativeExpressionEvaluator}, and run several
		// expressions through it to make sure that they work.
		
		def nullaryOperator = "x";
		def dummy = new SymbolicExpression( nullaryOperator, [] );
		
		def validator = new CumulativeExpressionValidator();
		
		
		validator.allowOnly( { false } );
		
		validator.allowAlso( { (
			it.getOperator().is( nullaryOperator )
			&&
			it.getArgumentList().isEmpty()
		) } );
		
		validator.allowAlso( { (
			it.getOperator().is( OverloadableOperators.Plus )
			&&
			(it.getArgumentList().size() == 2)
		) } );
		
		
		assert   validator.validates(
			new SymbolicExpression( nullaryOperator, [] )
		);
		assert  !validator.validates(
			new SymbolicExpression( nullaryOperator, [dummy] )
		);
		assert  !validator.validates(
			new SymbolicExpression( nullaryOperator, [dummy, dummy] )
		);
		
		assert  !validator.validates(
			new SymbolicExpression( OverloadableOperators.Plus, [] )
		);
		assert  !validator.validates(
			new SymbolicExpression( OverloadableOperators.Plus, [dummy] )
		);
		assert   validator.validates(
			new SymbolicExpression( OverloadableOperators.Plus, [dummy, dummy] )
		);
		assert  !validator.validates(
			new SymbolicExpression(
				OverloadableOperators.Plus,
				[dummy, dummy, dummy]
			)
		);
		
		assert   validator.validates( dummy );
		assert   validator.validates( dummy + dummy );
		assert   validator.validates( dummy + dummy + dummy );
	}
	
	void testConstantOperator()
	{
		// Make sure that {@code ConstantOperator} works properly.
		
		shouldFail( NullPointerException, { new ConstantOperator( null ) } );
		
		def c = { new SymbolicExpression( new ConstantOperator( it ), [] ) };
		
		def threepio = c( [ 3, "P", 0 ] );
		
		assertEquals( threepio, threepio );
		assertEquals( threepio, c( [ 3, "P", 0 ] ) );
		assertToString( threepio, "<< (Constant: [3, P, 0]): [] >>" );
	}
	
	void testIdentifierOperator()
	{
		// Make sure that {@code IdentifierOperator} works properly.
		
		shouldFail( NullPointerException, { new IdentifierOperator( null ) } );
		shouldFail(
			NullPointerException,
			{ new IdentifierOperator( null, "x" ) }
		);
		shouldFail(
			NullPointerException,
			{ new IdentifierOperator( Object.class, null ) }
		);
		
		assertEquals(
			new IdentifierOperator( "x" ),
			new IdentifierOperator( Object, "x" )
		);
		
		def r = {
			new SymbolicExpression(
				new IdentifierOperator( Number.class, it ), []
			)
		};
		
		def x = r( "x" );
		
		assertEquals( x, x );
		assertEquals( x, r( "x" ) );
		assertToString( x, "<< (Identifier: class java.lang.Number x): [] >>" );
	}
}