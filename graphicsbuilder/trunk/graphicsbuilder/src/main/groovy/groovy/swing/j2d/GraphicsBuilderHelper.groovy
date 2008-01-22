/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package groovy.swing.j2d

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Shape
import java.awt.geom.Area

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsBuilderHelper {
    public static void extendShapes() {
       def shapeMethods = Shape.metaClass.methods
       def methodMap = [
          'plus':'add',
          'minus':'subtract',
          'and':'intersect',
          'xor':'exclusiveOr'
       ]
       boolean updated = false
       methodMap.each { op, method ->
          if( !shapeMethods.name.find{ it == op } ){
             Shape.metaClass."$op" << { Shape other ->
                def area = new Area(delegate)
                area."$method"( new Area(other) )
                return area
             }
             updated = true
          }
          if( updated ){
             ExpandoMetaClass.enableGlobally()
          }
       }
    }

    public static void extendColor() {
       def colorMethods = Color.metaClass.methods
       if( !colorMethods.name.find{ it == "derive" } ){
          Color.metaClass.derive = { Map props ->
             def red = props.red != null ? props.red: delegate.red
             def green = props.green != null ? props.green: delegate.green
             def blue = props.blue != null ? props.blue: delegate.blue
             def alpha = props.alpha != null ? props.alpha: delegate.alpha
             return new Color( (red > 1 ? red/255: red) as float,
                               (green > 1 ? green/255: green) as float,
                               (blue > 1 ? blue/255: blue) as float,
                               (alpha > 1 ? alpha/255: alpha) as float )
          }
       }
       if( !colorMethods.name.find{ it == "rgb" } ){
          Color.metaClass.rgb = {
             return delegate.getRGB()
          }
       }
    }

    public static void extendBasicStroke(){
       def strokeMethods = BasicStroke.metaClass.methods
       if( !strokeMethods.name.find{ it == "derive" } ){
          BasicStroke.metaClass.derive << { Map props ->
             def width = props.width != null ? props.width : delegate.lineWidth
             def cap = props.cap != null ? props.cap : delegate.endCap
             def join = props.join != null ? props.join : delegate.lineJoin
             def miterlimit = props.miterlimit != null ? props.miterlimit : delegate.miterLimit
             def dash = props.dash != null ? props.dash : delegate.dashArray
             def dashphase = props.dashphase != null ? props.dashphase : delegate.dashPhase
             if( dash != null && dashphase != null ){
                return new BasicStroke( width as float,
                                        GraphicsBuilderHelper.getCapValue(cap),
                                        GraphicsBuilderHelper.getJoinValue(join),
                                        miterlimit as float,
                                        GraphicsBuilderHelper.getDashValue(dash),
                                        dashphase )
             }else{
                return new BasicStroke( width as float,
                                        GraphicsBuilderHelper.getCapValue(cap),
                                        GraphicsBuilderHelper.getJoinValue(join),
                                        miterlimit as float )
             }
          }
       }
    }

    public static int getCapValue( cap ) {
       if( cap == null ) return BasicStroke.CAP_SQUARE
       if( cap instanceof Number ){
           return cap
       }else if( cap instanceof String ){
           if( "butt".compareToIgnoreCase( cap ) == 0 ){
               return BasicStroke.CAP_BUTT
           }else if( "round".compareToIgnoreCase( cap ) == 0 ){
              return BasicStroke.CAP_ROUND
           }else if( "square".compareToIgnoreCase( cap ) == 0 ){
              return BasicStroke.CAP_SQUARE
           }
           throw new IllegalArgumentException( "'cap=$cap' is not one of [butt,round,square]" )
       }
       throw new IllegalArgumentException( "'cap' value is not a String nor an int" )
   }

   public static float[] getDashValue( dash ) {
       if( dash == null ) return null
       if( dash instanceof float[] ) return dash
       float[] array = new float[dash.size()]
       array.eachWithIndex { value, index ->
           if( value instanceof Number ){
               array[index] = value
           }else{
               throw new IllegalArgumentException( "dash[${index}] is not a Number" );
           }
       }
       return array
   }

   public static int getJoinValue( join ) {
       if( join == null ) return BasicStroke.JOIN_MITER
       if( join instanceof Number ){
           return join
       }else if( join instanceof String ){
           if( "bevel".compareToIgnoreCase( join ) == 0 ){
               return BasicStroke.JOIN_BEVEL
           }else if( "round".compareToIgnoreCase( join ) == 0 ){
              return BasicStroke.JOIN_ROUND
           }else if( "miter".compareToIgnoreCase( join ) == 0 ){
              return BasicStroke.JOIN_MITER
           }
           throw new IllegalArgumentException( "'join=$join' is not one of [bevel,miter,round]" )
       }
       throw new IllegalArgumentException( "'join' value is not a String nor an int" )
   }
}