package org.codehaus.groovy.science


class TestScience extends GroovyTestCase
{
	void testClosureOperator()
	{
		shouldFailWithCause(
			NullPointerException.class,
			{ new ClosureOperator( null ) }
		);
		
		
		def badOperator =
			new ClosureOperator( { "This is not a boolean value." } );
		
		assertEquals( badOperator, badOperator );
		shouldFailWithCause(
			NullPointerException.class,
			{ badOperator.accepts( null ) }
		);
		shouldFailWithCause(
			ClassCastException.class,
			{ badOperator.accepts( [] ) }
		);
		
		
		// Make a nullary operator.
		def nullaryOperator =
			new ClosureOperator( "dummy", { it.size() == 0 } );
		
		assertEquals( nullaryOperator, nullaryOperator );
		assertToString( nullaryOperator, "dummy" );
		shouldFailWithCause(
			NullPointerException.class,
			{ nullaryOperator.accepts( null ) }
		);
		assert   nullaryOperator.accepts( [] );
		assert  !nullaryOperator.accepts( [ null ] );
		
		
		// Make a binary {@code +} operator.
		def plusOperator =
			new ClosureOperator( "plus", { it.size() == 2 } );
		
		assertEquals( plusOperator, plusOperator );
		assertToString( plusOperator, "plus" );
		shouldFailWithCause(
			NullPointerException.class,
			{ plusOperator.accepts( null ) }
		);
		assert  !plusOperator.accepts( [] );
		assert  !plusOperator.accepts( [ null ] );
		assert   plusOperator.accepts( [ null, null ] );
		assert  !plusOperator.accepts( [ null, null, null ] );
		
		
		shouldFailWithCause(
			NullPointerException.class,
			{ new SymbolicExpression( nullaryOperator, null ) }
		)
		
		shouldFailWithCause(
			IllegalArgumentException.class,
			{ new SymbolicExpression( nullaryOperator, [ null ] ) }
		)
		
		
		// Make a simple expression using the nullary operator.
		def dummy = new SymbolicExpression( nullaryOperator, [] );
		
		assertEquals( dummy, dummy );
		assertToString( dummy, "<< dummy: [] >>" );
		assertSame( dummy.getOperator(), nullaryOperator );
		assertEquals( dummy.getArgumentList(), [] );
		
		
		// Overload {@code +} so that we can use our {@code +} operator in a
		// less verbose way.
		SymbolicExpression.metaClass.plus <<
		{
			new SymbolicExpression( plusOperator, [ delegate, it ] )
		};
		
		
		// Finally, make a somewhat more complicated expression.
		def sum = dummy + dummy + dummy;
		
		assertEquals( sum, sum );
		assertToString(
			sum,
			"<< plus: [" +
				"<< plus: [<< dummy: [] >>, << dummy: [] >>] >>" +
				", " +
				"<< dummy: [] >>" +
			"] >>"
		);
		assertSame( sum.getOperator(), plusOperator );
		assertEquals( sum.getArgumentList(), [ dummy + dummy, dummy ] );
	}
}