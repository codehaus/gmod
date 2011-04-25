package groovy.nio.file

import java.nio.file.Path

import spock.lang.*

class PathTest extends GroovyTestCase {
	static def LINE_1 = "a line"
	static def LINE_2 = "another line"
	static def SOME_TEXT = LINE_1+"\n"+LINE_2
	
	Path path
    Path path2
	
	public void setUp() {
        path = File.createTempFile("Path", ".txt").toPath()
        path2 = File.createTempFile("Path2", ".txt").toPath()
            
		use(PathCategory) {
            path.deleteOnExit()
			path.write(SOME_TEXT) // all tests test Path.write

            path2.deleteOnExit()
		}
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
	
	public void testEachLine() {
		def actual = []
		use(PathCategory) {
			path.eachLine { actual << it }
		}	
		assert actual == [ LINE_1, LINE_2 ]
	}
	
	public void testReadLines() {
		use(PathCategory) {
			assert path.readLines() == [ LINE_1, LINE_2 ]
		}	
	}
	
	public void testAppend() {
		use(PathCategory) {
			path.append(LINE_1)
			assert path.text == SOME_TEXT+LINE_1
		}	
	}

    public void testNewOutputStreamAndNewInputStream() {
        use(PathCategory) {
            def out = path2.newOutputStream()
            out << path.newInputStream() 
            out.close()

            assert path2.text == path.text
        }
    }
}
