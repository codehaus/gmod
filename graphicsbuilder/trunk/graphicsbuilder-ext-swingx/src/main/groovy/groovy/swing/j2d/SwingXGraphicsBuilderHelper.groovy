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

import groovy.swing.j2d.factory.FilterFactory
import groovy.swing.j2d.factory.BlendCompositeFactory
import groovy.swing.j2d.operations.filters.swingx.*
import groovy.swing.j2d.operations.shapes.MorphGraphicsOperation
import org.jdesktop.swingx.graphics.BlendComposite

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SwingXGraphicsBuilderHelper {
   public static void registerOperations( GraphicsBuilder builder ) {
      builder.registerGraphicsOperationBeanFactory( "morph", MorphGraphicsOperation )
      builder.registerFactory( "blendComposite", new BlendCompositeFactory() )

      // swingx
      builder.registerFactory( "colorTint", new FilterFactory(ColorTintFilterProvider) )
      builder.registerFactory( "dropShadow", new FilterFactory(DropShadowFilterProvider) )
      builder.registerFactory( "fastBlur", new FilterFactory(FastBlurFilterProvider) )
      builder.registerFactory( "reflection", new FilterFactory(ReflectionFilterProvider) )
      builder.registerFactory( "sgaussianBlur", new FilterFactory(SgaussianBlurFilterProvider) )
      builder.registerFactory( "stackBlur", new FilterFactory(StackBlurFilterProvider) )

      // blendComposite
      builder.blendAverage = BlendComposite.BlendingMode.AVERAGE
      builder.blendMultiply = BlendComposite.BlendingMode.MULTIPLY
      builder.blendScreen = BlendComposite.BlendingMode.SCREEN
      builder.blendDarken = BlendComposite.BlendingMode.DARKEN
      builder.blendLighten = BlendComposite.BlendingMode.LIGHTEN
      builder.blendOverlay = BlendComposite.BlendingMode.OVERLAY
      builder.blendHardLight = BlendComposite.BlendingMode.HARD_LIGHT
      builder.blendSoftLight = BlendComposite.BlendingMode.SOFT_LIGHT
      builder.blendDifference = BlendComposite.BlendingMode.DIFFERENCE
      builder.blendNegation = BlendComposite.BlendingMode.NEGATION
      builder.blendExclusion = BlendComposite.BlendingMode.EXCLUSION
      builder.blendColorDodge = BlendComposite.BlendingMode.COLOR_DODGE
      builder.blendInverseColorDodge = BlendComposite.BlendingMode.INVERSE_COLOR_DODGE
      builder.blendSoftDodge = BlendComposite.BlendingMode.SOFT_DODGE
      builder.blendColorBurn = BlendComposite.BlendingMode.COLOR_BURN
      builder.blendInverseColorBurn = BlendComposite.BlendingMode.INVERSE_COLOR_BURN
      builder.blendSoftBurn = BlendComposite.BlendingMode.SOFT_BURN
      builder.blendReflect = BlendComposite.BlendingMode.REFLECT
      builder.blendGlow = BlendComposite.BlendingMode.GLOW
      builder.blendFreeze = BlendComposite.BlendingMode.FREEZE
      builder.blendHeat = BlendComposite.BlendingMode.HEAT
      builder.blendAdd = BlendComposite.BlendingMode.ADD
      builder.blendSubtract = BlendComposite.BlendingMode.SUBTRACT
      builder.blendStamp = BlendComposite.BlendingMode.STAMP
      builder.blendRed = BlendComposite.BlendingMode.RED
      builder.blendGreen = BlendComposite.BlendingMode.GREEN
      builder.blendBlue = BlendComposite.BlendingMode.BLUE
      builder.blendHue = BlendComposite.BlendingMode.HUE
      builder.blendSaturation = BlendComposite.BlendingMode.SATURATION
      builder.blendColor = BlendComposite.BlendingMode.COLOR
      builder.blendLuminosity = BlendComposite.BlendingMode.LUMINOSITY

      // shortcuts
      builder.addShortcut( 'morph', 'start', 'a' )
      builder.addShortcut( 'morph', 'end', 'e' )
      builder.addShortcut( 'morph', 'morph', 'm' )

      ['dropShadow','fastBlur','sgaussianBlur','stackBlur'].each { nodeName ->
         builder.addShortcut( nodeName, 'radius', 'r' )
      }
      
      builder.addShortcut( 'dropShadow', 'radius', 'r' )
      builder.addShortcut( 'dropShadow', 'opacity', 'o' )
      builder.addShortcut( 'dropShadow', 'color', 'c' )
   }
}
