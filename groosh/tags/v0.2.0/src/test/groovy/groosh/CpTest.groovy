package groosh;


class CpTest extends GroovyTestCase {

	def gsh = new groosh.Groosh()
	
	void testCp() {
		def dir = new File("testcdtmpdir").mkdir()
		gsh.ls().toFile("testcdtmpfile.txt")
		def out = gsh.cp("testcdtmpfile.txt","testcdtmpdir").toStdOut()
		new File("testcdtmpdir/testcdtmpfile.txt").delete()
		new File("testcdtmpdir").delete()
		new File("testcdtmpfile.txt").delete()
		assert out == null
	}
	
	void testCpWithSpaces() {
		def dir = new File("test cd tmp dir").mkdir()
		gsh.ls().toFile("test cd tmp file.txt")
		def out = gsh.cp("test cd tmp file.txt","test cd tmp dir").toStdOut()
		new File("test cd tmp dir/test cd tmp file.txt").delete()
		new File("test cd tmp dir").delete()
		new File("test cd tmp file.txt").delete()
		assert out == null		
	}

}
