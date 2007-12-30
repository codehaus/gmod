package groovy.swing.j2d.svg

import javax.xml.parsers.SAXParserFactory
import org.xml.sax.*

class TestSvg2GroovyHandler extends GroovyTestCase {
    /*
	void testReadRect(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/rect.svg" )
	}

	void testReadRoundRect(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/roundrect.svg" )
	}

	void testReadCircle(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/circle.svg" )
	}

	void testReadEllipse(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/ellipse.svg" )
	}

	void testReadLine(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/line.svg" )
	}

	void testReadPolyline(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/polyline.svg" )
	}

	void testReadPolygon(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/polygon.svg" )
	}

	void testReadText(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/text.svg" )
	}

	void testReadPathTrangle(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/path-triangle.svg" )
	}

	void testReadPathBezier(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/path-bezier.svg" )
	}

	void testReadPathQuad(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/path-quad.svg" )
	}

	void testReadPathArcAbs(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/path-arcabs.svg" )
	}

	void testReadPathArcs(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/path-arcs.svg" )
	}

	void testReadPathJavaLog(){
	   def writer = readSvgFile( "/groovy/swing/j2d/svg/java_logo.svg" )
	   new File("java_logo.groovy").text = writer.toString()
	}
	*/

	void testEmpty(){
	   // empty
	}

	private Writer readSvgFile( filename ) {
	   def writer = new StringWriter()
	   def handler = new Svg2GroovyHandler(writer)
	   def reader = SAXParserFactory.newInstance().newSAXParser().xMLReader
	   reader.contentHandler = handler
	   //def is = new FileInputStream( "c:\\tmp\\groovy\\Java_Logo.svg" )
	   def resources = new File("./src/test/resources")
	   def is = new FileInputStream( new File(resources,filename) )
	   reader.parse( new InputSource(is) )
	   is.close()
       return writer
	}
}