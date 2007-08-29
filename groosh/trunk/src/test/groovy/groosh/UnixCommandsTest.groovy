//  Groosh -- Provides a shell-like capability for handling external processes
//
//  Copyright © 2007 Alexander Egger
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software distributed under the License is
//  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//  implied. See the License for the specific language governing permissions and limitations under the
//  License.

package groosh

/**
 * 
 * @author Alexander Egger
 *
 */
class UnixCommandsTest extends GroovyTestCase {

	def gsh = new groosh.Groosh()
	
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

}
