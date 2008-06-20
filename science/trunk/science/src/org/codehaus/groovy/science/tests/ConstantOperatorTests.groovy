package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.ConstantOperator
import org.codehaus.groovy.science.SymbolicExpression


class ConstantOperatorTests extends GroovyTestCase
{
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
}