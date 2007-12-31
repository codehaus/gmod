package groovy.swing.j2d.svg

import org.xml.sax.*
import org.xml.sax.helpers.DefaultHandler

public class Svg2GroovyHandler extends GfxSAXHandler {
   private IndentPrinter out
   private Map textNode = null
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
            out.println("group {")
            out.incrementIndent()
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
               "fill": this.&colorAttributeHandler,
               "stroke": { p, v -> " borderColor: ${getColorValue(v)},"},
               "stroke-width": { p, v -> " borderWidth: $v,"},
            ],["opacity"])
            out.println(") {")
            handleTransformations( attrs )
            handleFont( attrs )
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
               "fill": this.&colorAttributeHandler,
               "stroke": { p, v -> " borderColor: ${getColorValue(v)},"},
               "stroke-width": { p, v -> " borderWidth: $v,"},
            ], ["x","y","width","height","opacity"] )
            if( attrs.getValue("transform") ){
               out.println(") {")
               handleTransformations( attrs )
               out.printIndent()
               out.println("}")
            }else{
               out.println(")")
            }
         }
      ]
      factories.circle = [
         start: { attrs ->
            out.print("circle(")
            handleAttributes( attrs, [
               "fill": this.&colorAttributeHandler,
               "stroke": { p, v -> " borderColor: ${getColorValue(v)},"},
               "stroke-width": { p, v -> " borderWidth: $v,"},
               "r": { p, v -> " radius: $v,"},
            ], ["cx","cy","opacity"] )
            if( attrs.getValue("transform") ){
               out.println(") {")
               handleTransformations( attrs )
               out.printIndent()
               out.println("}")
            }else{
               out.println(")")
            }
         }
      ]
      factories.ellipse = [
         start: { attrs ->
            out.print("ellipse(")
            handleAttributes( attrs, [
               "fill": this.&colorAttributeHandler,
               "stroke": { p, v -> " borderColor: ${getColorValue(v)},"},
               "stroke-width": { p, v -> " borderWidth: $v,"},
               "rx": { p, v -> " radiusx: $v,"},
               "ry": { p, v -> " radiusy: $v,"},
            ], ["cx","cy","opacity"] )
            if( attrs.getValue("transform") ){
               out.println(") {")
               handleTransformations( attrs )
               out.printIndent()
               out.println("}")
            }else{
               out.println(")")
            }
         }
      ]
      factories.line = [
         start: { attrs ->
            out.print("line(")
            handleAttributes( attrs, [
               "stroke": { p, v -> " borderColor: ${getColorValue(v)},"},
               "stroke-width": { p, v -> " borderWidth: $v,"},
            ], ["x1","x2","y1","y2","opacity"] )
            if( attrs.getValue("transform") ){
               out.println(") {")
               handleTransformations( attrs )
               out.printIndent()
               out.println("}")
            }else{
               out.println(")")
            }
         }
      ]
      factories.polyline = [
         start: { attrs ->
            out.print("polyline(")
            handleAttributes( attrs, [
               "stroke": { p, v -> " borderColor: ${getColorValue(v)},"},
               "stroke-width": { p, v -> " borderWidth: $v,"},
               "points": { p, v -> " points: [${v.replaceAll('\\s+',',')}],"},
            ],["opacity"])
            if( attrs.getValue("transform") ){
               out.println(") {")
               handleTransformations( attrs )
               out.printIndent()
               out.println("}")
            }else{
               out.println(")")
            }
         }
      ]
      factories.polygon = [
         start: { attrs ->
            out.print("polygon(")
            handleAttributes( attrs, [
               "fill": this.&colorAttributeHandler,
               "stroke": { p, v -> " borderColor: ${getColorValue(v)},"},
               "stroke-width": { p, v -> " borderWidth: $v,"},
               "points": { p, v -> " points: [${v.replaceAll('\\s+',',')}],"},
            ],["opacity"])
            if( attrs.getValue("transform") ){
               out.println(") {")
               handleTransformations( attrs )
               out.printIndent()
               out.println("}")
            }else{
               out.println(")")
            }
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
               "fill": this.&colorAttributeHandler,
               "stroke": { p, v -> " borderColor: ${getColorValue(v)},"},
               "stroke-width": { p, v -> " borderWidth: $v,"},
            ],["x","y","opacity"])
            if( textNode.attrs.getValue("transform") ){
               out.println(") {")
               handleTransformations( textNode.attrs )
               out.printIndent()
               out.println("}")
            }else{
               out.println(")")
            }
            textNode = null
         }
      ]
      factories.path = [
         start: { attrs ->
            out.print("xpath(")
            out.incrementIndent()
            handleAttributes( attrs, [
               "fill": this.&colorAttributeHandler,
               "fill-rule": { p, v -> " winding: '$v',"},
               "stroke": { p, v -> " borderColor: ${getColorValue(v)},"},
               "stroke-width": { p, v -> " borderWidth: $v,"},
            ], ["opacity"])
            out.println("){")
            handlePathInfo( attrs.getValue("d") )
            handleTransformations( attrs )
         },
         end: { ->
            out.decrementIndent()
            out.printIndent()
            out.println("}")
         }
      ]
   }

   private String defaultAttributeHandler( String property, value ){
      return " $property: $value,"
   }

   private String colorAttributeHandler( String property, value ){
      if( value =~ /url/ ) return ""
      return " $property: ${getColorValue(value)},"
   }

   private String getColorValue( String value ){
      if( value == "none" ) return "false"
      if( !value.startsWith("#") && !(value =~ /[0-9a-fA-F]{6}/) ){
         return "color('$value')"
      }
      def offset = value.startsWith("#") ? 1 : 0
      return "color(red: "+
             Integer.parseInt(value[(0+offset)..(1+offset)],16) +", green: "+
             Integer.parseInt(value[(2+offset)..(3+offset)],16) +", blue: "+
             Integer.parseInt(value[(4+offset)..(5+offset)],16) +")"
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
         }
      }
      if( str ){
         out.print(str[0..-2]+" ")
      }
   }

   private void handleTransformations( Attributes attrs ){
      def transform = attrs.getValue("transform")
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

   private String data = null
   private int offset = 0
   private int max = 0
   private def x
   private def y
   private def cx
   private def cy

   private void handlePathInfo( String data ){
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
      def match = data[offset..-1] =~ /([+|-]?[0-9]+[\.[0-9]+]*)/
      def str = match[0][0]
      offset += str.length()
      println "'$str'"
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