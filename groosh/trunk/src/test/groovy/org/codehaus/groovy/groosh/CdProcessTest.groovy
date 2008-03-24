package org.codehaus.groovy.groosh;


class CdProcessTest extends GroovyTestCase {

	def gsh = new groosh.Groosh()
	
	void testVaildCd() {
		def env = gsh.getCurrentEnvironment()
		def oldPwd = env.get("PWD")
		gsh.cd("/")
		assert "/" == gsh.getCurrentExecDir().toString()
		def pwd = gsh.pwd().getText()
		assert "/\n" == pwd
		assert "/" == env.get("PWD")
		assert oldPwd == env.get("OLDPWD")
	}
	
	void testInvalidCd() {
		shouldFail {gsh.cd("asdfjaldfj")}
	}
	
	void testEmptyCd() {
		gsh.cd()
		def pwd = gsh.pwd().getText()
		def userdir = System.getProperty("user.dir") + "\n"
		assert userdir == pwd
	}
	
	void testCdMinus() {
		def startPwd = gsh.pwd().getText()
		new File("test").mkdir();
		gsh.cd("test")
		gsh.cd("-")
		def pwd = gsh.pwd().getText()
		assert startPwd == pwd
	}

}
