package org.codehaus.groovy.science

// A toolbox used internally in order to make Groovy method calls from inside
// Java.
//
// TODO: Determine whether it's possible and preferable to do this some other
// way.
final class MakeshiftGroovyDispatcher
{
	static boolean isCase( Object caseValue, Object switchValue )
	{
		return ( switchValue in caseValue );
	}
}