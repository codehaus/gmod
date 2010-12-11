package groovy.nio.file

import java.nio.file.Path

import org.codehaus.groovy.runtime.DefaultGroovyMethods as DGM
import spock.lang.*

class PathTest extends Specification {
	static def SOME_TEXT = "some text"
	
	def "get text from a file"() {	
		given:
		File tempFile = File.createTempFile("Path_GetText", ".txt")
		tempFile.deleteOnExit()
		DGM.write(tempFile, SOME_TEXT)
		Path path = tempFile.toPath()

		expect:
		use(PathCategory) {
			path.text == SOME_TEXT
		}
	}
}