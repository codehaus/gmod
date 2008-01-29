import groovy.swing.j2d.*
import groovy.xml.StreamingMarkupBuilder
import javax.imageio.ImageIO

class GfxDoc {
	static void main(args) {
	    def gr = new GraphicsRenderer()

	    new File("target/doc/images").mkdirs()
	    new File("target/doc/html").mkdirs()

		shapes.GraphicsSuite.suite.each { op ->
		   def name = op.name
		   name = name[name.lastIndexOf('.')+1..-1]
		   def page = {
		      html {
		         title "GraphicsBuilder - Shapes - $name"
		         body {
		            h3( name )
		            p op.description
		            h3 "Properties"
		            p {
		               table {
		                  tr {
		                     th "Property"
		                     th "Type"
		                     th "Default"
		                     th "Notes"
		                  }
		                  op.propertyTable.each { propertyName, props ->
		                     tr {
		                        td propertyName
		                        td props.type
		                        props.value != null ? td{tt{b(props.value)}} : td(props.value)
		                        td props.notes ?: ""
		                     }
		                  }
		               }
		            }
		            h3 "Examples"
		            op.examples.eachWithIndex { example, index ->
		               p {
		                  pre( example.code )
		               }
		               p {
		                  img( src: "../images/${op.name}_${index}.png" )
		               }
		            }
		         }
		      }
		   }

		   def html = new StreamingMarkupBuilder().bind( page )
		   new File( "target/doc/html", "${op.name}.html" ).text = html

		   op.examples.eachWithIndex { example, index ->
		      def image = gr.render( example.width, example.height ){
		         rect( width: example.width, height: example.height, fill: 'white' )
		         gr.gb.group( example.code )
		      }
		      def file = new File("target/doc/images","${op.name}_${index}.png")
		      ImageIO.write( image, "png", file )
		   }
		}
	}
}