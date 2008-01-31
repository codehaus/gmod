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
      // filters
      //
      builder.registerGraphicsOperationBeanFactory( "grayscaleColormap", GrayscaleColormap )
      builder.registerGraphicsOperationBeanFactory( "linearColormap", LinearColormap )
      builder.registerGraphicsOperationBeanFactory( "spectrumColormap", SpectrumColormap )
      builder.registerGraphicsOperationBeanFactory( "splineColormap", SplineColormap )

      // blur
      builder.registerFactory( "average", new FilterFactory(AverageFilterProvider) )
      builder.registerFactory( "blur", new FilterFactory(BlurFilterProvider) )
      builder.registerFactory( "boxBlur", new FilterFactory(BoxBlurFilterProvider) )
      builder.registerFactory( "convolve", new FilterFactory(ConvolveFilterProvider) )
      builder.registerFactory( "embossEdges", new FilterFactory(EmbossEdgesFilterProvider) )

      // colors
      builder.registerFactory( "contrast", new FilterFactory(ContrastFilterProvider) )
      builder.registerFactory( "invert", new FilterFactory(InvertFilterProvider) )

      // distort
      builder.registerFactory( "circleDistort", new FilterFactory(CircleFilterProvider) )
      builder.registerFactory( "curl", new FilterFactory(CurlFilterProvider) )
      builder.registerFactory( "diffuse", new FilterFactory(DiffuseFilterProvider) )
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
   }
}