package org.codehaus.groovy.science.tests


import org.codehaus.groovy.science.IdentifierOperator
import org.codehaus.groovy.science.SymbolicExpression


class IdentifierOperatorTests extends GroovyTestCase
{
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