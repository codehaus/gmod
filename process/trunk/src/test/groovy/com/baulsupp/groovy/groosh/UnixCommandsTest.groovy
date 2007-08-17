package com.baulsupp.groovy.groosh

class UnixCommandsTest extends GroovyTestCase {

	def gsh = new com.baulsupp.groovy.groosh.Groosh()
	
	def blaResult = 
		"a\n" +
		"b\n" +
		"ba\n" +
		"c\n" +
		"d\n"
		
	def eachLineResult = 
		"*a\n" +
		"*b\n" +
		"*ba\n" +
		"*c\n" +
		"*d\n"

	def dictResult = 
		"Alexia\n" +
		"alexias\n" +
		"Alexia's\n" +
		"dyslexia\n" +
		"dyslexia's\n" 

	void testBasicCat() {
		def out = gsh._cat('src/test/resources/blah.txt').toStringOut()
		assert blaResult == out
	}

	void testCatToFile() {
	    def tmpFile = File.createTempFile("groovyTest",".txt")
		gsh.cat('src/test/resources/blah.txt').toFile(tmpFile)
		assert blaResult == tmpFile.getText()
	}
	
	void testDict() {
		def out = gsh.cat('src/test/resources/words').pipeTo(gsh._grep('lexia')).toStringOut();
		assert dictResult == out
	}

	void testEachLine() {
		def cat = gsh.cat('src/test/resources/blah.txt');
		def lines = gsh.each_line { line,w -> 
		  w.write("*");
		  w.write(line);
		  w.write("\n");
		};

		cat.pipeTo(lines);
		def out = lines.toStringOut();
		
		assert eachLineResult == out
	}

	void testFindGrid() {
		def f = gsh.find('examples', '-name', '*.groovy', '-ls')
		def total = 0;
		def lines = gsh.grid { values,w ->
		  def x = values[2,4,6,10]; 
		  def s = x.join('	');
		  w.println(s);
		  total += Integer.parseInt(values[6]);
		};

		f.pipeTo(lines);
		def out = lines.toStringOut();

		assert 1558 == total

	}

}
