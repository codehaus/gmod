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
import groovy.swing.j2d.factory.FieldWarpLineFactory
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
      builder.registerGraphicsOperationBeanFactory( "gradientColormap", Gradient )
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
      builder.registerFactory( "gaussianBlur", new FilterFactory(GaussianBlurFilterProvider) )
      builder.registerFactory( "glow", new FilterFactory(GlowFilterProvider) )

      // colors
      builder.registerFactory( "contrast", new FilterFactory(ContrastFilterProvider) )
      builder.registerFactory( "diffusion", new FilterFactory(DiffusionFilterProvider) )
      builder.registerFactory( "dither", new FilterFactory(DitherFilterProvider) )
      builder.registerFactory( "equalize", new FilterFactory(EqualizeFilterProvider) )
      builder.registerFactory( "exposure", new FilterFactory(ExposureFilterProvider) )
      builder.registerFactory( "fade", new FilterFactory(FadeFilterProvider) )
      builder.registerFactory( "fill", new FilterFactory(FillFilterProvider) )
      builder.registerFactory( "gain", new FilterFactory(GainFilterProvider) )
      builder.registerFactory( "gamma", new FilterFactory(GammaFilterProvider) )
      builder.registerFactory( "invert", new FilterFactory(InvertFilterProvider) )

      // distort
      builder.registerFactory( "circleDistort", new FilterFactory(CircleFilterProvider) )
      builder.registerFactory( "curl", new FilterFactory(CurlFilterProvider) )
      builder.registerFactory( "diffuse", new FilterFactory(DiffuseFilterProvider) )
      builder.registerFactory( "displace", new FilterFactory(DisplaceFilterProvider) )
      builder.registerFactory( "inFieldWarpLine", new FieldWarpLineFactory(false) )
      builder.registerFactory( "outFieldWarpLine", new FieldWarpLineFactory(true) )
      builder.registerFactory( "fieldWarp", new FilterFactory(FieldWarpFilterProvider,false) )
      builder.registerFactory( "kaleidoscope", new FilterFactory(KaleidoscopeFilterProvider) )
      builder.registerFactory( "marble", new FilterFactory(MarbleFilterProvider) )
      builder.registerFactory( "ripple", new FilterFactory(RippleFilterProvider) )
      builder.registerFactory( "water", new FilterFactory(WaterFilterProvider) )

      // effects
      builder.registerFactory( "feedback", new FilterFactory(FeedbackFilterProvider) )
      builder.registerFactory( "glint", new FilterFactory(GlintFilterProvider) )
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
      builder.registerFactory( "flare", new FilterFactory(FlareFilterProvider) )
      builder.registerFactory( "flush3D", new FilterFactory(Flush3DFilterProvider) )
      builder.registerFactory( "mosaic", new FilterFactory(MosaicFilterProvider) )
      builder.registerFactory( "pointillize", new FilterFactory(PointillizeFilterProvider) )
      builder.registerFactory( "shapeBurst", new FilterFactory(ShapeBurstFilterProvider) )

      // texture
      builder.registerFactory( "brushedMetal", new FilterFactory(BrushedMetalFilterProvider) )
      builder.registerFactory( "cellular", new FilterFactory(CellularFilterProvider) )
      builder.registerFactory( "check", new FilterFactory(CheckFilterProvider) )
      builder.registerFactory( "caustics", new FilterFactory(CausticsFilterProvider) )
      def fbm = new FilterFactory(FBMFilterProvider)
      builder.registerFactory( "fractalBrownianMotion", fbm )
      builder.registerFactory( "fbm", fbm )
      builder.registerFactory( "fourColorFill", new FilterFactory(FourColorFillFilterProvider) )
      builder.registerFactory( "weave", new FilterFactory(WeaveFilterProvider) )
      builder.registerFactory( "wood", new FilterFactory(WoodFilterProvider) )

      // transform
      builder.registerFactory( "flip", new FilterFactory(FlipFilterProvider) )

      // -- VARIABLES

      // light
      builder.colorsFromImage = LightFilter.COLORS_FROM_IMAGE
      builder.colorsConstant = LightFilter.COLORS_CONSTANT
      builder.bumpsFromBevel = LightFilter.BUMPS_FROM_BEVEL
      builder.bumpsFromImage = LightFilter.BUMPS_FROM_IMAGE
      builder.bumpsFromImageAlpha = LightFilter.BUMPS_FROM_IMAGE_ALPHA
      builder.bumpsFromMap = LightFilter.BUMPS_FROM_MAP

      // transform
      builder.edgeActionZero = TransformFilter.ZERO
      builder.edgeActionClamp = TransformFilter.CLAMP
      builder.edgeActionWrap = TransformFilter.WRAP
      builder.interpolationNearest = TransformFilter.NEAREST_NEIGHBOUR
      builder.interpolationBilinear = TransformFilter.BILINEAR

      // flips
      builder.flipH = FlipFilter.FLIP_H
      builder.flipV = FlipFilter.FLIP_V
      builder.flipHV = FlipFilter.FLIP_HV
      builder.flip90CW = FlipFilter.FLIP_90CW
      builder.flip90CCW = FlipFilter.FLIP_90CCW
      builder.flip180 = FlipFilter.FLIP_180

      // shapeBurst
      builder.burstLinear = ShapeFilter.LINEAR
      builder.burstCircleUp = ShapeFilter.CIRCLE_UP
      builder.burstUp = ShapeFilter.CIRCLE_UP
      builder.burstCircleDown = ShapeFilter.CIRCLE_DOWN
      builder.burstDown = ShapeFilter.CIRCLE_DOWN
      builder.burstSmooth = ShapeFilter.SMOOTH

      // ripple
      builder.rippleSine = RippleFilter.SINE
      builder.rippleSawtooth = RippleFilter.SAWTOOTH
      builder.rippleTriangle = RippleFilter.TRIANGLE
      builder.rippleNoise = RippleFilter.NOISE

      // cellular
      builder.cellRandom = CellularFilter.RANDOM
      builder.cellSquare = CellularFilter.SQUARE
      builder.cellHexagonal = CellularFilter.HEXAGONAL
      builder.cellOctagonal = CellularFilter.OCTAGONAL
      builder.cellTriangular = CellularFilter.TRIANGULAR

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

      // fbm
      builder.basisCellular = FBMFilter.CELLULAR
      builder.basisNoise = FBMFilter.NOISE
      builder.basisRidged = FBMFilter.RIDGED
      builder.basisSCNoise = FBMFilter.SCNOISE
      builder.basisVLNoise = FBMFilter.VLNOISE

      // pixelUtils
      builder.pixelReplace = PixelUtils.REPLACE
      builder.pixelNormal = PixelUtils.NORMAL
      builder.pixelMin = PixelUtils.MIN
      builder.pixelMax = PixelUtils.MAX
      builder.pixelAdd = PixelUtils.ADD
      builder.pixelSubtract = PixelUtils.SUBTRACT
      builder.pixelDifference = PixelUtils.DIFFERENCE
      builder.pixelMultiply = PixelUtils.MULTIPLY
      builder.pixelHue = PixelUtils.HUE
      builder.pixelSaturation = PixelUtils.SATURATION
      builder.pixelValue = PixelUtils.VALUE
      builder.pixelColor = PixelUtils.COLOR
      builder.pixelScreen = PixelUtils.SCREEN
      builder.pixelAverage = PixelUtils.AVERAGE
      builder.pixelOverlay = PixelUtils.OVERLAY
      builder.pixelClear = PixelUtils.CLEAR
      builder.pixelExchange = PixelUtils.EXCHANGE
      builder.pixelDissolve = PixelUtils.DISSOLVE
      builder.pixelDstIn = PixelUtils.DST_IN
      builder.pixelAlpha = PixelUtils.ALPHA
      builder.pixelAlphaToGray = PixelUtils.ALPHA_TO_GRAY
   }
}