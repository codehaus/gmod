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

package groovy.swing.j2d.operations.paints

import java.awt.Color
import java.awt.Paint
import java.awt.LinearGradientPaint
import java.awt.geom.Point2D
import java.awt.MultipleGradientPaint.*

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.GradientStop
import groovy.swing.j2d.operations.PaintProvider
import groovy.swing.j2d.operations.MultipleGradientPaintProvider
import groovy.swing.j2d.operations.Transformable
import groovy.swing.j2d.operations.TransformationGroup
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class LinearGradientPaintGraphicsOperation extends AbstractLinearGradientPaintGraphicsOperation implements
     MultipleGradientPaintProvider, Transformable {
   public static optional = super.optional + ['linkTo']
   protected static DEFAULT_CYCLE_VALUE = 'nocycle'

   private def stops = []
   TransformationGroup transformationGroup

   def linkTo

   LinearGradientPaintGraphicsOperation() {
      super( "linearGradient" )
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

   public void propertyChange( PropertyChangeEvent event ){
      if( stops.contains(event.source) ){
         firePropertyChange( new ExtPropertyChangeEvent(this,event) )
      }else{
         super.propertyChange( event )
      }
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

   protected Paint makePaint( x1, y1, x2, y2 ){
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
         return new LinearGradientPaint( new Point2D.Double(x1,y1),
                                         new Point2D.Double(x2,y2),
                                         fractions,
                                         colors,
                                         getCycleMethod(),
                                         ColorSpaceType.SRGB,
                                         transformationGroup.getConcatenatedTransform() )
      }else{
         return new LinearGradientPaint( new Point2D.Double(x1,y1),
                                         new Point2D.Double(x2,y2),
                                         fractions,
                                         colors,
                                         getCycleMethod() )
      }
   }

   private def getCycleMethod() {
      if( !cycle ){
         return CycleMethod.NO_CYCLE
      }else if( cycle instanceof CycleMethod ){
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