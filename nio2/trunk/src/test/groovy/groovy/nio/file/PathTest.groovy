package groovy.nio.file

import java.nio.file.Path

import spock.lang.*

class PathTest extends GroovyTestCase {
	static def SOME_TEXT = "some text"
	
	public void testGetTextFromAFile() {	
		File tempFile = File.createTempFile("Path_GetText", ".txt")
		tempFile.deleteOnExit()
		tempFile.write(SOME_TEXT)
		Path path = tempFile.toPath()

		use(PathCategory) {
			assert path.text == SOME_TEXT
		}
	}
}