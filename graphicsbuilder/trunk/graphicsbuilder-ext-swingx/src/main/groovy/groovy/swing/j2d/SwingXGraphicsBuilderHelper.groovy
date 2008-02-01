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
import groovy.swing.j2d.factory.LightFactory
import groovy.swing.j2d.factory.TimingFrameworkFactory
import groovy.swing.j2d.operations.filters.alpha.*
import groovy.swing.j2d.operations.filters.binary.*
import groovy.swing.j2d.operations.filters.blur.*
import groovy.swing.j2d.operations.filters.colors.*
import groovy.swing.j2d.operations.filters.distort.*
import groovy.swing.j2d.operations.filters.effects.*
import groovy.swing.j2d.operations.filters.keying.*
import groovy.swing.j2d.operations.filters.lights.*
import groovy.swing.j2d.operations.filters.stylize.*
import groovy.swing.j2d.operations.filters.texture.*
import groovy.swing.j2d.operations.filters.transform.*
import groovy.swing.j2d.operations.shapes.MorphGraphicsOperation
import com.jhlabs.image.*
import com.jhlabs.image.LightFilter.*
import com.jhlabs.image.CellularFilterimport com.jhlabs.image.TransformFilter
/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class SwingXGraphicsBuilderHelper {
   public static void registerOperations( GraphicsBuilder builder ) {
      builder.registerGraphicsOperationBeanFactory( "morph", MorphGraphicsOperation )
      builder.registerFactory( "animate", new TimingFrameworkFactory() )

      //
      // colormaps
      //
      builder.registerGraphicsOperationBeanFactory( "grayscaleColormap", GrayscaleColormap )
      builder.registerGraphicsOperationBeanFactory( "linearColormap", LinearColormap )
      builder.registerGraphicsOperationBeanFactory( "spectrumColormap", SpectrumColormap )
      builder.registerGraphicsOperationBeanFactory( "splineColormap", SplineColormap )

      // alpha
      builder.registerFactory( "erodeAlpha", new FilterFactory(ErodeAlphaFilterProvider) )

      // binary
      builder.registerFactory( "dilate", new FilterFactory(DilateFilterProvider) )
      builder.registerFactory( "erode", new FilterFactory(ErodeFilterProvider) )

      // blur
      builder.registerFactory( "average", new FilterFactory(AverageFilterProvider) )
      builder.registerFactory( "blur", new FilterFactory(BlurFilterProvider) )
      builder.registerFactory( "boxBlur", new FilterFactory(BoxBlurFilterProvider) )
      builder.registerFactory( "convolve", new FilterFactory(ConvolveFilterProvider) )
      builder.registerFactory( "despeckle", new FilterFactory(DespeckleFilterProvider) )
      builder.registerFactory( "detectEdges", new FilterFactory(DetectEdgesFilterProvider) )
      builder.registerFactory( "embossEdges", new FilterFactory(EmbossEdgesFilterProvider) )

      // colors
      builder.registerFactory( "contrast", new FilterFactory(ContrastFilterProvider) )
      builder.registerFactory( "diffusion", new FilterFactory(DiffusionFilterProvider) )
      builder.registerFactory( "dither", new FilterFactory(DitherFilterProvider) )
      builder.registerFactory( "equalize", new FilterFactory(EqualizeFilterProvider) )
      builder.registerFactory( "exposure", new FilterFactory(ExposureFilterProvider) )
      builder.registerFactory( "fade", new FilterFactory(FadeFilterProvider) )
      builder.registerFactory( "invert", new FilterFactory(InvertFilterProvider) )

      // distort
      builder.registerFactory( "circleDistort", new FilterFactory(CircleFilterProvider) )
      builder.registerFactory( "curl", new FilterFactory(CurlFilterProvider) )
      builder.registerFactory( "diffuse", new FilterFactory(DiffuseFilterProvider) )
      builder.registerFactory( "displace", new FilterFactory(DisplaceFilterProvider) )
      builder.registerFactory( "kaleidoscope", new FilterFactory(KaleidoscopeFilterProvider) )
      builder.registerFactory( "marble", new FilterFactory(MarbleFilterProvider) )
      builder.registerFactory( "ripple", new FilterFactory(RippleFilterProvider) )
      builder.registerFactory( "water", new FilterFactory(WaterFilterProvider) )

      // effects
      builder.registerFactory( "mirror", new FilterFactory(MirrorFilterProvider) )

      // keying
      builder.registerFactory( "chromaKey", new FilterFactory(ChromaKeyFilterProvider) )

      // lights
      builder.registerGraphicsOperationBeanFactory( "material", LightFilter.Material )
      builder.registerFactory( "ambientLight", new LightFactory(LightFilter.AmbientLight) )
      builder.registerFactory( "pointLight", new LightFactory(LightFilter.PointLight) )
      builder.registerFactory( "distantLight", new LightFactory(LightFilter.DistantLight) )
      builder.registerFactory( "spotLight", new LightFactory(LightFilter.SpotLight) )
      builder.registerFactory( "lights", new FilterFactory(LightsFilterProvider,false) )
      builder.registerFactory( "chrome", new FilterFactory(ChromeFilterProvider,false) )

      // stylize
      builder.registerFactory( "contour", new FilterFactory(ContourFilterProvider) )
      builder.registerFactory( "dissolve", new FilterFactory(DissolveFilterProvider) )
      builder.registerFactory( "dropShadow", new FilterFactory(ShadowFilterProvider) )
      builder.registerFactory( "crystallize", new FilterFactory(CrystallizeFilterProvider) )
      builder.registerFactory( "emboss", new FilterFactory(EmbossFilterProvider) )
      builder.registerFactory( "mosaic", new FilterFactory(MosaicFilterProvider) )
      builder.registerFactory( "pointillize", new FilterFactory(PointillizeFilterProvider) )
      builder.registerFactory( "shapeBurst", new FilterFactory(ShapeBurstFilterProvider) )

      // texture
      builder.registerFactory( "cellular", new FilterFactory(CellularFilterProvider) )
      builder.registerFactory( "check", new FilterFactory(CheckFilterProvider) )
      builder.registerFactory( "caustics", new FilterFactory(CausticsFilterProvider) )
      builder.registerFactory( "brushedMetal", new FilterFactory(BrushedMetalFilterProvider) )
      builder.registerFactory( "weave", new FilterFactory(WeaveFilterProvider) )
      builder.registerFactory( "wood", new FilterFactory(WoodFilterProvider) )

      // transform
      builder.registerFactory( "flip", new FilterFactory(FlipFilterProvider) )

      // -- VARIABLES

      // transform
      builder.zeroEdgeAction = TransformFilter.ZERO
      builder.clampEdgeAction = TransformFilter.CLAMP
      builder.warpEdgeAction = TransformFilter.WRAP
      builder.nearestInterpolation = TransformFilter.NEAREST_NEIGHBOUR
      builder.bilinearInterpolation = TransformFilter.BILINEAR

      // flips
      builder.flipH = FlipFilter.FLIP_H
      builder.flipV = FlipFilter.FLIP_V
      builder.flipHV = FlipFilter.FLIP_HV
      builder.flip90CW = FlipFilter.FLIP_90CW
      builder.flip90CCW = FlipFilter.FLIP_90CCW
      builder.flip180 = FlipFilter.FLIP_180

      // shapeBurst
      builder.linearBurst = ShapeFilter.LINEAR
      builder.circleUpBurst = ShapeFilter.CIRCLE_UP
      builder.upBurst = ShapeFilter.CIRCLE_UP
      builder.circleDownBurst = ShapeFilter.CIRCLE_DOWN
      builder.downBurst = ShapeFilter.CIRCLE_DOWN
      builder.smoothBurst = ShapeFilter.SMOOTH

      // ripple
      builder.sineRipple = RippleFilter.SINE
      builder.sawtoothRipple = RippleFilter.SAWTOOTH
      builder.triangleRipple = RippleFilter.TRIANGLE
      builder.noiseRipple = RippleFilter.NOISE

      // cellular
      builder.randomCell = CellularFilter.RANDOM
      builder.squareCell = CellularFilter.SQUARE
      builder.hexagonalCell = CellularFilter.HEXAGONAL
      builder.octagonalCell = CellularFilter.OCTAGONAL
      builder.triangularCell = CellularFilter.TRIANGULAR

      // detectEdges matrices
      builder.robertsV = EdgeFilter.ROBERTS_V
      builder.robertsH = EdgeFilter.ROBERTS_H
      builder.prewittV = EdgeFilter.PREWITT_V
      builder.prewittH = EdgeFilter.PREWITT_H
      builder.sobelV = EdgeFilter.SOBEL_V
      builder.sobelH = EdgeFilter.SOBEL_H
      builder.freiChenV = EdgeFilter.FREI_CHEN_V
      builder.freChenH = EdgeFilter.FREI_CHEN_H

      // dither matrices
      builder.ditherMagic2x2Matrix = DitherFilter.ditherMagic2x2Matrix
      builder.ditherMagic4x4Matrix = DitherFilter.ditherMagic4x4Matrix
      builder.ditherOrdered4x4Matrix = DitherFilter.ditherOrdered4x4Matrix
      builder.ditherLines4x4Matrix = DitherFilter.ditherLines4x4Matrix
      builder.dither90Halftone6x6Matrix = DitherFilter.dither90Halftone6x6Matrix
      builder.ditherOrdered6x6Matrix = DitherFilter.ditherOrdered6x6Matrix
      builder.ditherOrdered8x8Matrix = DitherFilter.ditherOrdered8x8Matrix
      builder.ditherCluster3Matrix = DitherFilter.ditherCluster3Matrix
      builder.ditherCluster4Matrix = DitherFilter.ditherCluster4Matrix
      builder.ditherCluster8Matrix = DitherFilter.ditherCluster8Matrix
   }
}