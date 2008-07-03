/*
 * Copyright 2007-2008 the original author or authors.
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

import groovy.swing.j2d.factory.*
import groovy.swing.j2d.operations.filters.alpha.*
import groovy.swing.j2d.operations.filters.binary.*
import groovy.swing.j2d.operations.filters.blur.*
import groovy.swing.j2d.operations.filters.colors.*
import groovy.swing.j2d.operations.filters.distort.*
import groovy.swing.j2d.operations.filters.effects.*
import groovy.swing.j2d.operations.filters.keying.*
import groovy.swing.j2d.operations.filters.lights.*
import groovy.swing.j2d.operations.filters.render.*
import groovy.swing.j2d.operations.filters.stylize.*
import groovy.swing.j2d.operations.filters.texture.*
import groovy.swing.j2d.operations.filters.transform.*
import groovy.swing.j2d.operations.filters.transitions.*
import com.jhlabs.image.*
import com.jhlabs.image.LightFilter.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class JHlabsGraphicsBuilderPlugin {
   public static void registerOperations( GraphicsBuilder builder ) {
      //
      // colormaps
      //
      builder.registerFactory( "knot", new KnotFactory() )
      builder.registerGraphicsOperationBeanFactory( "gradientColormap", Gradient )
      builder.registerFactory( "grayscaleColormap", new ColormapFactory(GrayscaleColormap) )
      builder.registerFactory( "linearColormap", new LinearColormapFactory() )
      builder.registerFactory( "spectrumColormap", new ColormapFactory(SpectrumColormap) )
      builder.registerFactory( "splineColormap", new ColormapFactory(SplineColormap,false) )

      // alpha
      builder.registerFactory( "erodeAlpha", new FilterFactory(ErodeAlphaFilterProvider) )
      builder.registerFactory( "invertAlpha", new FilterFactory(InvertAlphaFilterProvider) )
      builder.registerFactory( "premultiply", new FilterFactory(PremultiplyFilterProvider) )
      builder.registerFactory( "unpremultiply", new FilterFactory(UnpremultiplyFilterProvider) )

      // binary
      builder.registerFactory( "dilate", new FilterFactory(DilateFilterProvider,false) )
      builder.registerFactory( "erode", new FilterFactory(ErodeFilterProvider,false) )
      builder.registerFactory( "life", new FilterFactory(LifeFilterProvider,false) )
      builder.registerFactory( "outline", new FilterFactory(OutlineFilterProvider,false) )
      builder.registerFactory( "skeletonize", new FilterFactory(SkeletonizeFilterProvider,false) )

      // blur
      builder.registerFactory( "average", new FilterFactory(AverageFilterProvider) )
      builder.registerFactory( "blur", new FilterFactory(BlurFilterProvider) )
      builder.registerFactory( "boxBlur", new FilterFactory(BoxBlurFilterProvider) )
      builder.registerFactory( "convolve", new FilterFactory(ConvolveFilterProvider) )
      builder.registerFactory( "despeckle", new FilterFactory(DespeckleFilterProvider) )
      builder.registerFactory( "detectEdges", new FilterFactory(DetectEdgesFilterProvider) )
      builder.registerFactory( "embossEdges", new FilterFactory(EmbossEdgesFilterProvider) )
      builder.registerFactory( "fastMotionBlur", new FilterFactory(FastMotionBlurFilterProvider) )
      builder.registerFactory( "gaussianBlur", new FilterFactory(GaussianBlurFilterProvider) )
      builder.registerFactory( "glow", new FilterFactory(GlowFilterProvider) )
      builder.registerFactory( "lensBlur", new FilterFactory(LensBlurFilterProvider) )
      builder.registerFactory( "maximum", new FilterFactory(MaximumFilterProvider) )
      builder.registerFactory( "median", new FilterFactory(MedianFilterProvider) )
      builder.registerFactory( "minimum", new FilterFactory(MinimumFilterProvider) )
      builder.registerFactory( "motionBlur", new FilterFactory(MotionBlurFilterProvider) )
      builder.registerFactory( "sharpen", new FilterFactory(SharpenFilterProvider) )
      builder.registerFactory( "smartBlur", new FilterFactory(SmartBlurFilterProvider) )
      builder.registerFactory( "smooth", new FilterFactory(SmoothFilterProvider) )
      builder.registerFactory( "unsharp", new FilterFactory(UnsharpFilterProvider) )
      builder.registerFactory( "variableBlur", new FilterFactory(VariableBlurFilterProvider) )

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
      builder.registerFactory( "grayOut", new FilterFactory(GrayOutFilterProvider) )
      builder.registerFactory( "grayscale", new FilterFactory(GrayscaleFilterProvider) )
      builder.registerFactory( "hsbAdjust", new FilterFactory(HSBAdjustFilterProvider) )
      builder.registerFactory( "invert", new FilterFactory(InvertFilterProvider) )
      builder.registerFactory( "levels", new FilterFactory(LevelsFilterProvider) )
      builder.registerFactory( "lookup", new FilterFactory(LookupFilterProvider,false) )
      builder.registerFactory( "maskColor", new FilterFactory(MaskFilterProvider) )
      builder.registerFactory( "posterize", new FilterFactory(PosterizeFilterProvider) )
      builder.registerFactory( "quantize", new FilterFactory(QuantizeFilterProvider) )
      builder.registerFactory( "rescaleColors", new FilterFactory(RescaleColorsFilterProvider) )
      builder.registerFactory( "rgbAdjust", new FilterFactory(RGBAdjustFilterProvider) )
      builder.registerFactory( "saturation", new FilterFactory(SaturationFilterProvider) )
      builder.registerFactory( "solarize", new FilterFactory(SolarizeFilterProvider) )
      builder.registerFactory( "transparency", new FilterFactory(TransparencyFilterProvider) )

      // distort
      builder.registerFactory( "circleDistort", new FilterFactory(CircleFilterProvider) )
      builder.registerFactory( "curl", new FilterFactory(CurlFilterProvider) )
      builder.registerFactory( "diffuse", new FilterFactory(DiffuseFilterProvider) )
      builder.registerFactory( "displace", new FilterFactory(DisplaceFilterProvider) )
      builder.registerFactory( "inFieldWarpLine", new FieldWarpLineFactory(false) )
      builder.registerFactory( "outFieldWarpLine", new FieldWarpLineFactory(true) )
      builder.registerFactory( "fieldWarp", new FilterFactory(FieldWarpFilterProvider,false) )
      builder.registerFactory( "kaleidoscope", new FilterFactory(KaleidoscopeFilterProvider) )
      builder.registerFactory( "mapCoordinates", new FilterFactory(MapCoordinatesFilterProvider) )
      builder.registerFactory( "marble", new FilterFactory(MarbleFilterProvider) )
      builder.registerFactory( "meshWarp", new FilterFactory(MeshWarpFilterProvider) )
      builder.registerFactory( "warpGrid", new WarpGridFactory() )
      builder.registerFactory( "perspective", new FilterFactory(PerspectiveFilterProvider) )
      builder.registerFactory( "pinch", new FilterFactory(PinchFilterProvider) )
      builder.registerFactory( "polar", new FilterFactory(PolarFilterProvider) )
      builder.registerFactory( "ripple", new FilterFactory(RippleFilterProvider) )
      builder.registerFactory( "sphereDistort", new FilterFactory(SphereFilterProvider) )
      builder.registerFactory( "swim", new FilterFactory(SwimFilterProvider) )
      builder.registerFactory( "twirl", new FilterFactory(TwirlFilterProvider) )
      builder.registerFactory( "water", new FilterFactory(WaterFilterProvider) )

      // effects
      builder.registerFactory( "composite", new FilterFactory(CompositeFilterProvider) )
      builder.registerFactory( "feedback", new FilterFactory(FeedbackFilterProvider) )
      builder.registerFactory( "glint", new FilterFactory(GlintFilterProvider,false) )
      builder.registerFactory( "interpolate", new FilterFactory(InterpolateFilterProvider,false) )
      //builder.registerFactory( "iterated", new FilterFactory(IteratedFilterProvider,false) )
      builder.registerFactory( "mirror", new FilterFactory(MirrorFilterProvider) )
      builder.registerFactory( "smear", new FilterFactory(SmearFilterProvider,false) )

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

      // render
      builder.registerFactory( "scratch", new FilterFactory(ScratchFilterProvider) )

      // stylize
      builder.registerFactory( "contour", new FilterFactory(ContourFilterProvider) )
      builder.registerFactory( "dissolve", new FilterFactory(DissolveFilterProvider) )
      builder.registerFactory( "crystallize", new FilterFactory(CrystallizeFilterProvider) )
      builder.registerFactory( "emboss", new FilterFactory(EmbossFilterProvider) )
      builder.registerFactory( "flare", new FilterFactory(FlareFilterProvider) )
      builder.registerFactory( "flush3D", new FilterFactory(Flush3DFilterProvider) )
      builder.registerFactory( "halftone", new FilterFactory(HalftoneFilterProvider) )
      builder.registerFactory( "javaLnF", new FilterFactory(JavaLnFFilterProvider) )
      builder.registerFactory( "lightRays", new FilterFactory(LightRaysFilterProvider,false) )
      builder.registerFactory( "mosaic", new FilterFactory(MosaicFilterProvider) )
      builder.registerFactory( "noise", new FilterFactory(NoiseFilterProvider) )
      builder.registerFactory( "oil", new FilterFactory(OilFilterProvider) )
      builder.registerFactory( "pointillize", new FilterFactory(PointillizeFilterProvider) )
      builder.registerFactory( "shapeBurst", new FilterFactory(ShapeBurstFilterProvider,false) )
      builder.registerFactory( "shade", new FilterFactory(ShadeFilterProvider) )
      builder.registerFactory( "shadow", new FilterFactory(ShadowFilterProvider) )
      builder.registerFactory( "shine", new FilterFactory(ShineFilterProvider) )
      builder.registerFactory( "stamp", new FilterFactory(StampFilterProvider) )
      builder.registerFactory( "threshold", new FilterFactory(ThresholdFilterProvider) )

      // texture
      builder.registerFactory( "brushedMetal", new FilterFactory(BrushedMetalFilterProvider) )
      builder.registerFactory( "cellular", new FilterFactory(CellularFilterProvider,false) )
      builder.registerFactory( "check", new FilterFactory(CheckFilterProvider) )
      builder.registerFactory( "caustics", new FilterFactory(CausticsFilterProvider) )
      def fbm = new FilterFactory(FBMFilterProvider,false)
      builder.registerFactory( "fractalBrownianMotion", fbm )
      builder.registerFactory( "fbm", fbm )
      builder.registerFactory( "fourColorFill", new FilterFactory(FourColorFillFilterProvider) )
      builder.registerFactory( "marbleTexture", new FilterFactory(MarbleTextureFilterProvider,false) )
      builder.registerFactory( "noiseTexture", new FilterFactory(NoiseTextureFilterProvider,false) )
      builder.registerFactory( "plasma", new FilterFactory(PlasmaFilterProvider,false) )
      builder.registerFactory( "quilt", new FilterFactory(QuiltFilterProvider,false) )
      //builder.registerFactory( "sky", new FilterFactory(SkyFilterProvider) )
      builder.registerFactory( "sparkle", new FilterFactory(SparkleFilterProvider) )
      builder.registerFactory( "weave", new FilterFactory(WeaveFilterProvider) )
      builder.registerFactory( "wood", new FilterFactory(WoodFilterProvider,false) )

      // transform
      builder.registerFactory( "flip", new FilterFactory(FlipFilterProvider) )

      // transitions
      builder.registerFactory( "gradientWipe", new FilterFactory(GradientWipeFilterProvider) )
      builder.registerFactory( "shatter", new FilterFactory(ShatterFilterProvider) )

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

      // smear
      builder.smearCrosses = SmearFilter.CROSSES
      builder.smearLines = SmearFilter.LINES
      builder.smearCircles = SmearFilter.CIRCLES
      builder.smearSquares = SmearFilter.SQUARES

      // knot
      builder.knotRgb = Gradient.RGB
      builder.knotHueCV = Gradient.HUE_CW
      builder.knotHueCCW = Gradient.HUE_CCW
      builder.knotLinear = Gradient.LINEAR
      builder.knotSpline = Gradient.SPLINE
      builder.knotCircleUp = Gradient.CIRCLE_UP
      builder.knotCircleDown = Gradient.CIRCLE_DOWN
      builder.knotConstant = Gradient.CONSTANT

      // noise
      builder.noiseGaussian = NoiseFilter.GAUSSIAN
      builder.noiseUniform = NoiseFilter.UNIFORM

      // polar
      builder.rectToPolar = PolarFilter.RECT_TO_POLAR
      builder.polarToRect = PolarFilter.POLAR_TO_RECT
      builder.invertInCircle = PolarFilter.INVERT_IN_CIRCLE

      ['erodeAlpha','boxBlur','gaussianBlur','glow','lensBlur','unsharp','variableBlur',
       'circleDistort','curl','kaleidoscope','pinch','sphereDistort','twirl','water',
       'flare','shine','sparkle','stamp','brushedMetal'].each { nodeName ->
         builder.addShortcut( nodeName, 'radius', 'r' )
      }

      ['fastMotionBlur','fmb','circleDistort','kaleidoscope','pinch','sphereDistort',
       'twirl','water','feedback','shatter','ambientLight','lightRays'].each { nodeName ->
         builder.addShortcut( nodeName, 'centreX', 'cx' )
         builder.addShortcut( nodeName, 'centreY', 'cy' )
      }

      ['boxBlur','smartBlur','variableBlur'].each { nodeName ->
         builder.addShortcut( nodeName, 'hRadius', 'hr' )
         builder.addShortcut( nodeName, 'vRadius', 'vr' )
      }

      ['transparency'].each { nodeName ->
         builder.addShortcut( nodeName, 'opacity', 'o' )
      }

      ['stamp','threshold'].each { nodeName ->
         builder.addShortcut( nodeName, 'color1', 'c1' )
         builder.addShortcut( nodeName, 'color2', 'c2' )
      }

      builder.addShortcut( 'curl', 'width', 'w' )
      builder.addShortcut( 'curl', 'height', 'h' )
      builder.addShortcut( 'scratch', 'width', 'w' )
      builder.addShortcut( 'mirror', 'centreY', 'cy' )
      builder.addShortcut( 'mirror', 'opacity', 'o' )
      
      ['ambientLight'].each { nodeName ->
         builder.addShortcut( nodeName, 'color', 'c' )
      }
   }
}
