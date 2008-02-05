package groovy.swing.j2d.svg

import org.xml.sax.*
import org.xml.sax.helpers.DefaultHandler

public class Svg2GroovyHandler extends GfxSAXHandler {
   private IndentPrinter out
   private Map textNode = null
   private boolean insideShape = false
   private Map factories = [:]

   public Svg2GroovyHandler() {
      this(new IndentPrinter())
   }

   public Svg2GroovyHandler( PrintWriter writer ){
      this(new IndentPrinter(writer))
   }

   public Svg2GroovyHandler( Writer writer ){
      this( new IndentPrinter(new PrintWriter(writer)) )
   }

   public Svg2GroovyHandler( IndentPrinter out ){
      this.out = out
      registerFactories()
   }

   protected void handleNodeStart( String name, Attributes attrs ){
      if( factories[name] ){
         out.printIndent()
         factories[name]?.start(attrs)
      }
   }

   protected void handleNodeEnd( String name ){
      if( factories[name]?.end ) factories[name]?.end()
   }

   protected void handleText( String text ){
      if( textNode ){
         textNode.text = text.trim()
      }
   }

   private void registerFactories(){
      factories.svg = [
         start: { attrs ->
            out.println("group( borderColor: false ) {")
            out.incrementIndent()
            def width = attrs.getValue("width")
            def height = attrs.getValue("height")
            if( width && height ){
               out.printIndent()
               out.println("// width = [$width]")
               out.printIndent()
               out.println("// height = [$height]")
            }
            def viewBox = attrs.getValue("viewBox")
            if( viewBox ){
               out.printIndent()
               out.println("// viewBox = [$viewBox]")
            }
            out.printIndent()
            out.println("antialias on")
         },
         end: { ->
            out.decrementIndent()
            out.printIndent()
            out.println("}")
         }
      ]
      factories.g = [
         start: { attrs ->
            out.print("group(")
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "fill": this.&fillAttributeHandler,
               "color": this.&borderColorAttributeHandler,
            ],["opacity"])
            out.println(") {")
            handleFont( attrs )
            handleStroke( attrs )
            handleTransformations( attrs )
            out.incrementIndent()
         },
         end: { ->
            out.decrementIndent()
            out.printIndent()
            out.println("}")
         }
      ]
      factories.rect = [
         start: { attrs ->
            insideShape = true
            out.print("rect(")
            def rx = attrs.getValue("rx")
            def ry = attrs.getValue("ry")
            if( rx && ry ){
               out.print(" arcWidth: $rx, arcHeight: $ry,")
            }else if( rx ){
               out.print(" arcWidth: $rx, arcHeight: $rx,")
            }else if( ry ){
               out.print(" arcWidth: $ry, arcHeight: $ry,")
            }
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "fill": this.&fillAttributeHandler,
               "color": this.&borderColorAttributeHandler,
            ], ["x","y","width","height","opacity"] )
            out.println(") {")
            handleStroke( attrs )
            handleTransformations( attrs )
            out.printIndent()
            out.println("}")
         },
         end: { ->
            insideShape = false
         }
      ]
      factories.circle = [
         start: { attrs ->
            insideShape = true
            out.print("circle(")
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "fill": this.&fillAttributeHandler,
               "color": this.&borderColorAttributeHandler,
               "r": { p, v -> " radius: ${normalize(v)},"},
            ], ["cx","cy","opacity"] )
            out.println(") {")
            handleStroke( attrs )
            handleTransformations( attrs )
            out.printIndent()
            out.println("}")
         },
         end: { ->
            insideShape = false
         }
      ]
      factories.ellipse = [
         start: { attrs ->
            insideShape = true
            out.print("ellipse(")
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "fill": this.&fillAttributeHandler,
               "color": this.&borderColorAttributeHandler,
               "rx": { p, v -> " radiusx: ${normalize(v)},"},
               "ry": { p, v -> " radiusy: ${normalize(v)},"},
            ], ["cx","cy","opacity"] )
            out.println(") {")
            handleStroke( attrs )
            handleTransformations( attrs )
            out.printIndent()
            out.println("}")
         },
         end: { ->
            insideShape = false
         }
      ]
      factories.line = [
         start: { attrs ->
            out.print("line(")
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "color": this.&borderColorAttributeHandler,
            ], ["x1","x2","y1","y2","opacity"] )
            out.println(") {")
            handleStroke( attrs )
            handleTransformations( attrs )
            out.printIndent()
            out.println("}")
         }
      ]
      factories.polyline = [
         start: { attrs ->
            out.print("polyline(")
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "color": this.&borderColorAttributeHandler,
               "points": { p, v -> " points: [${v.replaceAll('\\s+',',')}],"},
            ],["opacity"])
            out.println(") {")
            handleStroke( attrs )
            handleTransformations( attrs )
            out.printIndent()
            out.println("}")
         }
      ]
      factories.polygon = [
         start: { attrs ->
            insideShape = true
            out.print("polygon(")
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "fill": this.&fillAttributeHandler,
               "color": this.&borderColorAttributeHandler,
               "points": { p, v -> " points: [${v.replaceAll('\\s+',',')}],"},
            ],["opacity"])
            out.println(") {")
            handleStroke( attrs )
            handleTransformations( attrs )
            out.printIndent()
            out.println("}")
         },
         end: { ->
            insideShape = false
         }
      ]
      factories.text = [
         start: { attrs ->
            textNode = [attrs:attrs]
         },
         end: { ->
            out.print("text(")
            if( textNode.text ){
               out.print(" text: '${textNode.text}',")
            }
            handleAttributes( textNode.attrs, [
               "id": this.&idAttributeHandler,
               "fill": this.&fillAttributeHandler,
               "color": this.&borderColorAttributeHandler,
            ],["x","y","opacity"])
            out.println(") {")
            handleStroke( textNode.attrs )
            handleTransformations( textNode.attrs )
            out.printIndent()
            out.println("}")
            textNode = null
         }
      ]
      factories.path = [
         start: { attrs ->
            insideShape = true
            out.print("xpath(")
            out.incrementIndent()
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "fill-rule": { p, v -> " winding: '$v',"},
               "fill": this.&fillAttributeHandler,
               "color": this.&borderColorAttributeHandler,
            ], ["opacity"])
            out.println("){")
            handleStroke( attrs )
            handlePathInfo( attrs.getValue("d") )
            handleTransformations( attrs )
         },
         end: { ->
            insideShape = false
            out.decrementIndent()
            out.printIndent()
            out.println("}")
         }
      ]
      factories.linearGradient = [
         start: { attrs ->
            out.print("linearGradient(")
            out.incrementIndent()
            if( !insideShape ){
               out.print(" asPaint: true,")
            }
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "spreadMethod": { p, v -> " cycle: '$v',"},
               "xlink:href": { p ,v -> " linkTo: ${v[1..-1]},"},
            ], ["x1","y1","x2","y2"])
            out.println("){")
            handleTransformations( attrs )
         },
         end: { ->
            out.decrementIndent()
            out.printIndent()
            out.println("}")
         }
      ]
      factories.radialGradient = [
         start: { attrs ->
            out.print("radialGradient(")
            out.incrementIndent()
            if( !insideShape ){
               out.print(" asPaint: true,")
            }
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "spreadMethod": { p, v -> " cycle: '$v',"},
               "r": { p, v -> " radius: ${normalize(v)},"},
               "xlink:href": { p ,v -> " linkTo: ${v[1..-1]},"},
            ], ["cx","cy","fx","fy"])
            out.println("){")
            handleTransformations( attrs )
         },
         end: { ->
            out.decrementIndent()
            out.printIndent()
            out.println("}")
         }
      ]
      factories.stop = [
         start: { attrs ->
            out.print("stop(")
            handleAttributes( attrs, [
               "id": this.&idAttributeHandler,
               "stop-color": { p, v -> " color: ${getColorValue(v)},"},
               "stop-opacity": { p, v -> " opacity: ${normalize(v)},"},
               "offset": { p, v ->
                  v = v.endsWith("%") ? (v[0..-2].toInteger())/100 : v
                  " offset: $v,"
               }
            ] )
            out.println(")")
         }
      ]
   }

   private String defaultAttributeHandler( String property, value ){
      return " $property: ${normalize(value)},"
   }

   private String idAttributeHandler( String property, value ){
      return " $property: '$value',"
   }

   private String borderColorAttributeHandler( String property, value ){
      return " borderColor: ${getColorValue(value)},"
   }

   private String colorAttributeHandler( String property, value ){
      return " color: ${getColorValue(value)},"
   }

   private String fillAttributeHandler( String property, value ){
      return " fill: ${getColorValue(value)},"
   }

   private String borderWidthAttributeHandler( String property, value ){
      return " borderWidth: ${normalize(value)},"
   }

   private String normalize( number ){
      if( number =~ /[0-9]/ ){
         number = number.startsWith(".")? "0$number": number
         number = number.endsWith("px") || number.endsWith("cm") ? number[0..-3] : number
      }
      return number
   }

   private String getColorValue( String value ){
      //if( value == "none" ) return "false"
      if( value.startsWith("url(#") ){
         return value[5..-2]
      }
      return "color('$value')"
   }

   private void handleAttributes( Attributes attrs, Map mappings ){
      handleAttributes( attrs, mappings, [] )
   }

   private void handleAttributes( Attributes attrs, Map mappings, List defaultMappings ){
      def str = ""
      for( index in (0..<attrs.length) ){
         def attrname = attrs.getQName(index)
         if( defaultMappings.contains(attrname) ){
            str += defaultAttributeHandler(attrname,attrs.getValue(index))
         }else if( mappings[attrname] ){
            str += mappings[attrname](attrname,attrs.getValue(index))
         }else if( attrname == "style" ){
            attrs.getValue(index).split(";").each { pair ->
               def values = pair.split(":")
               if( defaultMappings.contains(values[0]) ){
                  str += defaultAttributeHandler(values[0],values[1])
               }else if( mappings[values[0]] ){
                  str += mappings[values[0]](values[0],values[1])
               }
            }
         }
      }
      if( str ){
         out.print(str[0..-2]+" ")
      }
   }

   private void handleTransformations( Attributes attrs ){
      def transform = attrs.getValue("transform")
      if( !transform ) transform = attrs.getValue("gradientTransform")
      if( !transform ) return
      out.incrementIndent()
      out.printIndent()
      out.println("transformations {")
      out.incrementIndent()
      transform.split(/\)/).each { s ->
         def t = s.split(/\(/)
         def op = t[0].trim()
         def values = t[1].replaceAll(","," ").split(" ")
         switch( op ){
            case 'translate':
            case 'scale':
               out.printIndent()
               out.println "$op( x: ${values[0]}, y: ${values.size()==1?0:values[1]} )"
               break;
            case 'rotate':
               out.printIndent()
               if( values.size() == 1 ){
                  out.println "$op( angle: ${values[0]} )"
               }else{
                  out.println "$op( angle: ${values[0]}, x: ${values[1]}, y: ${values[2]} )"
               }
               break;
            case 'skewX':
               out.printIndent()
               out.println "skew( x: ${values[0]} )"
               break;
            case 'skewY':
               out.printIndent()
               out.println "skew( y: ${values[0]} )"
               break;
            case 'matrix':
               out.printIndent()
               out.print "matrix( m00: ${values[0]}, m10: ${values[1]},"
               out.print " m01: ${values[2]}, m11: ${values[3]},"
               out.println " m02: ${values[4]}, m12: ${values[5]} )"
               break;
         }
      }
      out.decrementIndent()
      out.printIndent()
      out.println("}")
      out.decrementIndent()
   }

   private void handleFont( Attributes attrs ){
      def face = attrs.getValue("font-family")
      def size = attrs.getValue("font-size")
      def style = attrs.getValue("font-weight")
      if( face ){
         out.incrementIndent()
         out.printIndent()
         out.print("font( face: '$face'")
         if( size ) out.print(", size: $size")
         if( style ) out.print(", style: '$style'")
         out.println(" )")
         out.decrementIndent()
      }
   }

   private void handleStroke( Attributes attrs ){
      def s = attrs.getValue("stroke")
      def sw = attrs.getValue("stroke-width")
      def sc = attrs.getValue("stroke-linecap")
      def sj = attrs.getValue("stroke-linejoin")
      def sm = attrs.getValue("stroke-miterlimit")
      def sd = attrs.getValue("stroke-dasharray")
      def sdo = attrs.getValue("stroke-dashoffset")
      def style = attrs.getValue("style")

      if( s || sw || sc || sj || sm || sd || sdo ||
            (style && style =~ /stroke/ ) ){
         out.incrementIndent()
         out.printIndent()
         out.print("basicStroke(")
         handleAttributes( attrs, [
            "stroke": this.&colorAttributeHandler,
            "stroke-width": { p, v -> " width: ${normalize(v)},"},
            "stroke-linecap": { p, v -> " cap: '$v',"},
            "stroke-linejoin": { p, v -> " join: '$v',"},
            "stroke-miterlimit": { p, v -> " miterlimit: '$v',"},
            "stroke-dasharray": { p, v ->
               if( v == 'none' ) return ""
               return " dash: [$v]},"
            },
            "stroke-dashoffset": { p, v -> " dashphase: ${normalize(v)},"},
         ] )
         out.println(")")
         out.decrementIndent()
      }
   }

   private String data = null
   private int offset = 0
   private int max = 0
   private def x
   private def y
   private def cx
   private def cy

   private void handlePathInfo( String data ){
      if( !data ) return
      if( data[0] != "M" && data[0] != "m" ){
         throw new IllegalArgumentException("path.d does not start with M or m")
      }

      this.data = data
      this.offset = 1
      this.max = data.length()
      this.x = 0
      this.y = 0
      this.cx = 0
      this.cy = 0

      out.incrementIndent()
      moveToAbs()
      while( offset < max ){
         switch( readOp() ){
            case "M": moveToAbs(); break;
            case "m": moveToRel(); break;
            case "H": hlineAbs(); break;
            case "h": hlineRel(); break;
            case "V": vlineAbs(); break;
            case "v": vlineRel(); break;
            case "L": lineToAbs(); break;
            case "l": lineToRel(); break;
            case "C": curveToAbs(); break;
            case "c": curveToRel(); break;
            case "S": smoothCurveToAbs(); break;
            case "s": smoothCurveToRel(); break;
            case "Q": quadToAbs(); break;
            case "q": quadToRel(); break;
            case "T": smoothQuadToAbs(); break;
            case "t": smoothQuadToRel(); break;
            case "A": arcToAbs(); break;
            case "a": arcToRel(); break;
            case "Z":
            case "z": closePath()
                      // fall through
            default:
               offset += 1
               break
         }
      }
      out.decrementIndent()
   }

   def readNumber = { ->
      while( offset < max && !(data[offset] =~ /[0-9\.\-\+]/) ){ offset +=1 }
      if( offset >= max ) return ""
      def match = data[offset..-1] =~ /[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?/
      def str = match[0][0]
      offset += str.length()
      return str as BigDecimal
   }

   def readOp = { ->
      def str = ""
      while( offset < max && !(data[offset] =~ /[achlmqstvzACHLMQSTVZ]/) ){ offset +=1 }
      return offset < max ? data[offset] : ""
   }

   def readWs = { ->
      while( offset < max && data[offset] =~ /\s|,/ ){ offset +=1 }
   }

   def repeat = { op ->
      readWs()
      while( offset < max && data[offset] =~ /[0-9\.\-\+]/ ){
         op()
      }
   }

   def moveToAbs = { ->
      cx = x = readNumber()
      cy = y = readNumber()
      out.printIndent()
      out.println("xmoveTo( x: $x, y: $y )")
   }
   def moveToRel = { ->
      x += readNumber()
      y += readNumber()
      out.printIndent()
      out.println("xmoveTo( x: $x, y: $y )")
      cx = x
      cy = y
   }

   def lineToAbs = { ->
      cx = x = readNumber()
      cy = y = readNumber()
      out.printIndent()
      out.println("xlineTo( x: $x, y: $y )")
      repeat( lineToAbs )
   }
   def lineToRel = { ->
      x += readNumber()
      y += readNumber()
      out.printIndent()
      out.println("xlineTo( x: $x, y: $y )")
      repeat( lineToRel )
      cx = x
      cy = y
   }

   def hlineAbs = { ->
      cx = x = readNumber()
      out.printIndent()
      out.println("xhline( x: $x )")
      repeat( hlineAbs )
   }
   def hlineRel = { ->
      x += readNumber()
      out.printIndent()
      out.println("xhline( x: $x )")
      repeat( hlineRel )
      cx = x
   }

   def vlineAbs = { ->
      cy = y = readNumber()
      out.printIndent()
      out.println("xvline( y: $y )")
      repeat( vlineAbs )
   }
   def vlineRel = { ->
      y += readNumber()
      out.printIndent()
      out.println("xvline( y: $y )")
      repeat( vlineRel )
      cy = y
   }

   def closePath = { ->
      out.printIndent()
      out.println("xclose()")
      cx = x
      cy = y
   }

   def curveToAbs = { ->
      def x1 = readNumber()
      def y1 = readNumber()
      cx = readNumber()
      cy = readNumber()
      x = readNumber()
      y = readNumber()
      out.printIndent()
      out.println("xcurveTo( x1: $x1, y1: $y1, x2: $cx, y2: $cy, x3: $x, y3: $y )")
      repeat( curveToAbs )
   }
   def curveToRel = { ->
      def x1 = x + readNumber()
      def y1 = y + readNumber()
      cx = x + readNumber()
      cy = y + readNumber()
      x += readNumber()
      y += readNumber()
      out.printIndent()
      out.println("xcurveTo( x1: $x1, y1: $y1, x2: $cx, y2: $cy, x3: $x, y3: $y )")
      repeat( curveToRel )
   }

   def smoothCurveToAbs = { ->
      def x1 = x * 2 - cx
      def y1 = y * 2 - cy
      cx = readNumber()
      cy = readNumber()
      x = readNumber()
      y = readNumber()
      out.printIndent()
      out.println("xcurveTo( x1: $x1, y1: $y1, x2: $cx, y2: $cy, x3: $x, y3: $y )")
      repeat( smoothCurveToAbs )
   }
   def smoothCurveToRel = { ->
      def x1 = x * 2 - cx
      def y1 = y * 2 - cy
      cx = x + readNumber()
      cy = y + readNumber()
      x += readNumber()
      y += readNumber()
      out.printIndent()
      out.println("xcurveTo( x1: $x1, y1: $y1, x2: $cx, y2: $cy, x3: $x, y3: $y )")
      repeat( smoothCurveToRel )
   }

   def quadToAbs = { ->
      cx = readNumber()
      cy = readNumber()
      x = readNumber()
      y = readNumber()
      out.printIndent()
      out.println("xquadTo( x1: $cx, y1: $cy, x2: $x, y2: $y )")
      repeat( quadToAbs )
   }
   def quadToRel = { ->
      cx = x + readNumber()
      cy = y + readNumber()
      x += readNumber()
      y += readNumber()
      out.printIndent()
      out.println("xquadTo( x1: $cx, y1: $cy, x2: $x, y2: $y )")
      repeat( quadToRel )
   }

   def smoothQuadToAbs = { ->
      cx = x * 2 - cx
      cy = y * 2 - cy
      x = readNumber()
      y = readNumber()
      out.printIndent()
      out.println("xquadTo( x1: $cx, y1: $cy, x2: $x, y2: $y )")
      repeat( smoothQuadToAbs )
   }
   def smoothQuadToRel = { ->
      cx = x * 2 - cx
      cy = y * 2 - cy
      x += readNumber()
      y += readNumber()
      out.printIndent()
      out.println("xquadTo( x1: $cx, y1: $cy, x2: $x, y2: $y )")
      repeat( smoothQuadToRel )
   }

   def arcToAbs = { ->
      def rx = readNumber()
      def ry = readNumber()
      def a = readNumber()
      def l = readNumber() == 1 ? true : false
      def s = readNumber() == 1 ? true : false
      cx = x = readNumber()
      cy = y = readNumber()
      out.printIndent()
      out.println("xarcTo( rx: $rx, ry: $ry, angle: $a, x: $x, y: $y, largeArc: $l, sweep: $s )")
      repeat( arcToAbs )
   }
   def arcToRel = { ->
      def rx = readNumber()
      def ry = readNumber()
      def a = readNumber()
      def l = readNumber() == 1 ? true : false
      def s = readNumber() == 1 ? true : false
      x += readNumber()
      y += readNumber()
      out.printIndent()
      out.println("xarcTo( rx: $rx, ry: $ry, angle: $a, x: $x, y: $y, largeArc: $l, sweep: $s )")
      cx = x1
      cy = y1
      repeat( arcToRel )
   }
}
