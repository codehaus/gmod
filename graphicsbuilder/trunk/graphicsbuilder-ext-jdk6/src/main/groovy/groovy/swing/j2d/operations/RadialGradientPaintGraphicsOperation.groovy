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

package groovy.swing.j2d.operations

import groovy.swing.j2d.GraphicsContext

import java.awt.Color
import java.awt.Paint
import java.awt.RadialGradientPaint
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.MultipleGradientPaint.*
import groovy.swing.j2d.PaintProvider
import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.Transformable
import groovy.swing.j2d.impl.TransformationGroup
import groovy.swing.j2d.impl.AbstractPaintingGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class RadialGradientPaintGraphicsOperation extends AbstractPaintingGraphicsOperation implements
     MultipleGradientPaintProvider, Transformable {
   public static required = ['cx','cy','fx','fy','radius']
   public static optional = super.optional + ['cycle','absolute','linkTo']

   private def stops = []
   TransformationGroup transformationGroup

   // properties
   def cx
   def cy
   def fx
   def fy
   def radius
   def cycle = 'nocycle'
   def absolute = false
   def linkTo

   RadialGradientPaintGraphicsOperation() {
      super( "radialGradient" )
   }

   public List getStops(){
      return Collections.unmodifiableList(stops)
   }

   public void addStop( GradientStop stop ) {
      if( !stop ) return
      boolean replaced = false
      int size = stops.size()
      for( index in (0..<size) ){
         if( stops[index].offset == stop.offset ){
            stops[index] = stop
            replaced = true
            break
         }
      }
      if( !replaced ) stops.add( stop )
      stop.addPropertyChangeListener( this )
   }

   public PaintProvider asCopy() {
      PaintProvider copy = super.asCopy()
      stops.each { stop ->
         copy.addStop( stop.copy() )
      }
      if( transformationGroup ){
         transformationGroup.transformations.each { t ->
            copy.transformationGroup = new TransformationGroup()
            def transformation = t.copy()
            transformation.removePropertyChangeListener(this)
            copy.transformationGroup.addTransformation( transformation )
         }
      }
      return copy
   }

   void setProperty( String property, Object value ) {
      if( property == "linkTo" && value instanceof MultipleGradientPaintProvider ){
         value.stops.each { stop ->
            addStop( stop )
         }
      }
      super.setProperty( property, value )
   }

   public Paint getPaint( GraphicsContext context, Rectangle2D bounds ) {
      fx = fx == null ? cx: fx
      fy = fy == null ? cy: fy

      //if( absolute ){
         return makePaint( cx as float,
                           cy as float,
                           fx as float,
                           fy as float )
      /*}else{
         return makePaint( (cx + bounds.x) as float,
                           (cy + bounds.y) as float,
                           (fx + bounds.x) as float,
                           (fy + bounds.y) as float )
      }*/
   }

   public void setTransformationGroup( TransformationGroup transformationGroup ){
      if( transformationGroup ) {
         if( this.transformationGroup ){
            this.transformationGroup.removePropertyChangeListener( this )
         }
         this.transformationGroup = transformationGroup
         this.transformationGroup.addPropertyChangeListener( this )
      }
   }

   public TransformationGroup getTransformationGroup() {
      transformationGroup
   }

   private RadialGradientPaint makePaint( cx, cy, fx, fy ){
      stops = stops.sort { a, b -> a.offset <=> b.offset }
      int n = stops.size()
      float[] fractions = new float[n]
      Color[] colors = new Color[n]
      n.times { i ->
         GradientStop stop = stops[i]
         fractions[i] = stop.offset
         colors[i] = stop.color
         if( stop.opacity != null ){
            colors[i] = colors[i].derive(alpha:stop.opacity)
         }
      }

      if( transformationGroup && !transformationGroup.isEmpty() ){
         return new RadialGradientPaint( new Point2D.Float(cx as float,cy as float),
                                         radius as float,
                                         new Point2D.Float(fx as float,fy as float),
                                         fractions,
                                         colors,
                                         getCycleMethod(),
                                         ColorSpaceType.SRGB,
                                         transformationGroup.getConcatenatedTransform() )
      }else{
         return new RadialGradientPaint( cx as float,
                                         cy as float,
                                         radius as float,
                                         fx as float,
                                         fy as float,
                                         fractions,
                                         colors,
                                         getCycleMethod() )
      }
   }

   private def getCycleMethod() {
      if( cycle instanceof CycleMethod ){
         return cycle
      }else if( cycle instanceof String ){
         if( "nocycle".compareToIgnoreCase( cycle ) == 0 || "pad".compareToIgnoreCase( cycle ) == 0 ){
            return CycleMethod.NO_CYCLE
         }else if( "reflect".compareToIgnoreCase( cycle ) == 0 ){
            return CycleMethod.REFLECT
         }else if( "repeat".compareToIgnoreCase( cycle ) == 0 ){
            return CycleMethod.REPEAT
         }else{
            throw new IllegalStateException( "'cycle=" + cycle
                  + "' is not one of [nocycle,pad,reflect,repeat]" )
         }
      }
      throw new IllegalStateException( "'cycle' value is not a String nor a CycleMethod" );
   }
}