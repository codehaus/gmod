package groovy.nio.file

import java.nio.file.Path

import spock.lang.*

class PathTest extends GroovyTestCase {
	static def SOME_TEXT = "some text"
	
	Path path
	
	public void setUp() {
		File tempFile = File.createTempFile("Path_GetText", ".txt")
		tempFile.deleteOnExit()
		tempFile.write(SOME_TEXT)
		path = tempFile.toPath()
	}
	
	public void testGetText() {	
		use(PathCategory) {
			assert path.text == SOME_TEXT
		}
	}
	
	public void testSize() {
		use(PathCategory) {
			assert path.size() == SOME_TEXT.size()
		}
	}
}