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
     MultipleGradientPaintProvider {
   public static required = ['cx','cy','fx','fy','radius']
   public static optional = super.optional + ['cycle','absolute']

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

   RadialGradientPaintGraphicsOperation() {
      super( "radialGradient" )
   }

   public void addStop( GradientStop stop ) {
      if( !stop ) return
      stops.add( stop )
      stop.addPropertyChangeListener( this )
   }

   public PaintProvider asCopy() {
      PaintProvider copy = super.asCopy()
      stops.each { stop ->
         copy.addStop( stop.copy() )
      }
      copy
   }

   public Paint getPaint( GraphicsContext context, Rectangle2D bounds ) {
      fx = fx == null ? cx: fx
      fy = fy == null ? cy: fy

      if( absolute ){
         return makePaint( cx as float,
                           cy as float,
                           fx as float,
                           fy as float )
      }else{
         return makePaint( (cx + bounds.x) as float,
                           (cy + bounds.y) as float,
                           (fx + bounds.x) as float,
                           (fy + bounds.y) as float )
      }
   }

   private RadialGradientPaint makePaint( cx, cy, fx, fy ){
      int n = stops.size()
      float[] fractions = new float[n]
      Color[] colors = new Color[n]
      n.times { i ->
         GradientStop stop = stops[i]
         fractions[i] = stop.offset
         colors[i] = stop.color
      }

      if( transformationGroup && !transformationGroup.isEmpty() ){
         return new RadialGradientPaint( cx as float,
                                         cy as float,
                                         radius as float,
                                         fx as float,
                                         fy as float,
                                         fractions,
                                         colors,
                                         getCycleMethod(),
                                         ColorSpaceType.SRGB,
                                         transformationGroup.getContcatenatedTransform() )
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

   private def getCycleMethod() {
      if( cycle instanceof CycleMethod ){
         return cycle
      }else if( cycle instanceof String ){
         if( "nocycle".compareToIgnoreCase( cycle ) == 0 ){
            return CycleMethod.NO_CYCLE
         }else if( "reflect".compareToIgnoreCase( cycle ) == 0 ){
            return CycleMethod.REFLECT
         }else if( "repeat".compareToIgnoreCase( cycle ) == 0 ){
            return CycleMethod.REPEAT
         }else{
            throw new IllegalStateException( "'cycle=" + cycle
                  + "' is not one of [nocycle,reflect,repeat]" )
         }
      }
      throw new IllegalStateException( "'cycle' value is not a String nor a CycleMethod" );
   }
}