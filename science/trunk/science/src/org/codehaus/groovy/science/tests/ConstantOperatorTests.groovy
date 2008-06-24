package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.ConstantOperator
import org.codehaus.groovy.science.SymbolicExpression

import static org.codehaus.groovy.science.ConstantOperator.*


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
	
	void testCon()
	{
		// Make sure that {@code con}, {@code unCon}, and {@code inCon} work
		// properly.
		
		def dummy = new SymbolicExpression( "dummy", [] );
		
		
		shouldFail( NullPointerException, { con( null ) } );
		shouldFail( NullPointerException, { unCon( null ) } );
		shouldFail( NullPointerException, { inCon( null ) } );
		
		shouldFail( IllegalArgumentException, { unCon( dummy ); } );
		
		assertEquals(
			con( [ 3, "P", 0 ] ),
			new SymbolicExpression( new ConstantOperator( [ 3, "P", 0 ] ), [] )
		);
		
		assertEquals( unCon( con( [ 3, "P", 0 ] ) ), [ 3, "P", 0 ] );
		
		assertEquals(
			inCon( { a, b -> a + b } )( con( 1 ), con( 2 ) ),
			con( 3 )
		);
		
		shouldFail(
			IllegalArgumentException,
			{ inCon( { a, b -> a + b } )( dummy, dummy ) }
		);
	}
}