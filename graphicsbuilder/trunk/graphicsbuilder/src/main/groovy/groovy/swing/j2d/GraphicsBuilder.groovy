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

import java.awt.AlphaComposite
import java.awt.Rectangle
import java.awt.Component
import java.awt.image.AffineTransformOp

import groovy.swing.factory.BindFactory
import groovy.swing.factory.ModelFactory
import groovy.swing.j2d.factory.*
import groovy.swing.j2d.operations.misc.*
import groovy.swing.j2d.operations.outlines.*
import groovy.swing.j2d.operations.paints.*
import groovy.swing.j2d.operations.shapes.*
import groovy.swing.j2d.operations.shapes.path.*
import groovy.swing.j2d.operations.strokes.*
import groovy.swing.j2d.operations.transformations.*

import groovy.swing.SwingBuilder

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsBuilder extends FactoryBuilderSupport {
    private boolean building = false
    private GroovyShell shell

    public GraphicsBuilder( boolean registerExtensions = true ) {
        GraphicsBuilderHelper.extendShapes()
        GraphicsBuilderHelper.extendColor()
        GraphicsBuilderHelper.extendBasicStroke()
        registerOperations()

        if( registerExtensions ){
           def helpers = ["Jdk6GraphicsBuilderHelper",
                          "SwingXGraphicsBuilderHelper",
                          "BatikGraphicsBuilderHelper"]
           helpers.each { helper ->
              try{
                 Class helperClass = Class.forName("groovy.swing.j2d.${helper}")
                 helperClass.registerOperations( this )
              }catch( Exception e ){
                 System.err.println("GraphicsRenderer: couldn't register ${helper}")
              }
           }
        }
    }

    public def swingView( SwingBuilder builder = new SwingBuilder(), Closure closure ) {
        builder.addAttributeDelegate(this.&swingAttributeDelegate)
        builder.addPostNodeCompletionDelegate(this.&swingPostNodeCompletionDelegate)

        def go = null
        def proxyBuilderRef = getProxyBuilder()
        setProxyBuilder( builder )
        try {
            def container = builder.panel( closure )
            setProxyBuilder( proxyBuilderRef )
            go = invokeMethod( "\$swing", new SwingGraphicsOperation( container ) )
        } finally {
            setProxyBuilder( proxyBuilderRef )
        }

        return go
    }

    private registerGraphicsOperationBeanFactory( String name, Class beanClass ){
        registerFactory( name, new GraphicsOperationBeanFactory(beanClass,false) )
    }

    private registerGraphicsOperationBeanFactory( String name, Class beanClass, boolean leaf ){
        registerFactory( name, new GraphicsOperationBeanFactory(beanClass,leaf) )
    }

    private void registerOperations() {
        addAttributeDelegate(this.&idAttributeDelegate)
        addAttributeDelegate(this.&interpolationAttributeDelegate)
        addAttributeDelegate(this.&alphaCompositeAttributeDelegate)

        registerFactory( "draw", new DrawFactory() )
        registerFactory( "font", new FontFactory() )
        registerGraphicsOperationBeanFactory( "group", GroupGraphicsOperation )
        registerGraphicsOperationBeanFactory( "renderingHint", RenderingHintGraphicsOperation, true )
        registerFactory( "shape", new ShapeFactory() )
        registerFactory( "bind", new BindFactory() )
        addAttributeDelegate( BindFactory.&bindingAttributeDelegate )
        registerFactory( "image", new ImageFactory() )
        registerFactory( "color", new ColorFactory() )
        registerFactory( "clip", new ClipFactory() )
        registerFactory( "antialias", new AntialiasFactory() )
        registerFactory( "\$swing", new SwingFactory() )
        registerFactory( "alphaComposite", new AlphaCompositeFactory() )

        //
        // shapes
        //
        registerGraphicsOperationBeanFactory( "arc", ArcGraphicsOperation )
        registerGraphicsOperationBeanFactory( "circle", CircleGraphicsOperation )
        registerGraphicsOperationBeanFactory( "ellipse", EllipseGraphicsOperation )
        registerGraphicsOperationBeanFactory( "polygon", PolygonGraphicsOperation )
        registerGraphicsOperationBeanFactory( "rect", RectGraphicsOperation )
        registerGraphicsOperationBeanFactory( "text", TextGraphicsOperation )
        registerGraphicsOperationBeanFactory( "donut", DonutGraphicsOperation )
        registerGraphicsOperationBeanFactory( "triangle", TriangleGraphicsOperation )
        registerGraphicsOperationBeanFactory( "regularPolygon", RegularPolygonGraphicsOperation )
        registerGraphicsOperationBeanFactory( "rays", RaysGraphicsOperation )
        registerGraphicsOperationBeanFactory( "arrow", ArrowGraphicsOperation )
        registerGraphicsOperationBeanFactory( "pin", RoundPinGraphicsOperation )
        registerGraphicsOperationBeanFactory( "cross", CrossGraphicsOperation )
        registerGraphicsOperationBeanFactory( "star", StarGraphicsOperation )
        registerGraphicsOperationBeanFactory( "roundRect", MultiRoundRectangleGraphicsOperation )
        //registerGraphicsOperationBeanFactory( "glyph", GlyphGraphicsOperation )

        //
        // paths
        //
        registerGraphicsOperationBeanFactory( "path", PathGraphicsOperation )
        registerFactory( "moveTo", new PathOperationFactory( MoveToPathOperation) )
        registerFactory( "lineTo", new PathOperationFactory( LineToPathOperation) )
        registerFactory( "quadTo", new PathOperationFactory( QuadToPathOperation) )
        registerFactory( "curveTo", new PathOperationFactory( CurveToPathOperation) )
        registerFactory( "hline", new PathOperationFactory( HLinePathOperation) )
        registerFactory( "vline", new PathOperationFactory( VLinePathOperation) )
        registerFactory( "shapeTo", new PathOperationFactory( ShapePathOperation) )
        registerFactory( "close", new PathOperationFactory( ClosePathOperation) )

        //
        // outlines
        //
        registerGraphicsOperationBeanFactory( "line", LineGraphicsOperation )
        registerGraphicsOperationBeanFactory( "cubicCurve", CubicCurveGraphicsOperation )
        registerGraphicsOperationBeanFactory( "polyline", PolylineGraphicsOperation )
        registerGraphicsOperationBeanFactory( "quadCurve", QuadCurveGraphicsOperation )

        //
        // area operations
        //
        registerFactory( "add", new AreaGraphicsOperationFactory("add","add") )
        registerFactory( "subtract", new AreaGraphicsOperationFactory("subtract","subtract") )
        registerFactory( "intersect", new AreaGraphicsOperationFactory("intersect","intersect") )
        registerFactory( "xor", new AreaGraphicsOperationFactory("xor","exclusiveOr") )

        //
        // transformations
        //
        registerFactory( "transformations", new TransformationGroupFactory() )
        registerFactory( "rotate", new TransformationFactory(RotateTransformation) )
        registerFactory( "scale", new TransformationFactory(ScaleTransformation) )
        registerFactory( "skew", new TransformationFactory(SkewTransformation) )
        registerFactory( "translate", new TransformationFactory(TranslateTransformation) )
        registerFactory( "matrix", new TransformationFactory(MatrixTransformation) )
        registerFactory( "freeze", new TransformationFactory(FreezeTransformation) )

        //
        // paints
        //
        registerFactory( "borderPaint", new BorderPaintFactory() )
        registerGraphicsOperationBeanFactory( "gradientPaint", GradientPaintGraphicsOperation, true )
        registerGraphicsOperationBeanFactory( "multiPaint", MultiPaintGraphicsOperation )
        registerFactory( "paint", new PaintFactory() )
        registerGraphicsOperationBeanFactory( "texturePaint", TexturePaintGraphicsOperation, true )
        registerFactory( "colorPaint", new ColorPaintFactory() )

        //
        // strokes
        //
        registerFactory( "stroke", new StrokeFactory() )
        registerFactory( "basicStroke", new StrokesFactory(BasicStrokeGraphicsOperation, true) )
        registerFactory( "compositeStroke", new StrokesFactory(CompositeStrokeGraphicsOperation) )
        registerFactory( "compoundStroke", new StrokesFactory(CompoundStrokeGraphicsOperation) )
        registerFactory( "textStroke", new StrokesFactory(TextStrokeGraphicsOperation, true) )
        registerFactory( "shapeStroke", new StrokesFactory(ShapeStrokeGraphicsOperation) )
        registerFactory( "wobbleStroke", new StrokesFactory(WobbleStrokeGraphicsOperation, true) )
        registerFactory( "zigzagStroke", new StrokesFactory(ZigzagStrokeGraphicsOperation) )

        //
        // filters
        //
        registerFactory( "filters", new FilterGroupFactory() )

        // variables
        variables['on'] = true
        variables['yes'] = true
        variables['off'] = false
        variables['no'] = false

        variables['alphaClear'] = AlphaComposite.CLEAR
        variables['alphaDst'] = AlphaComposite.DST
        variables['alphaDstAtop'] = AlphaComposite.DST_ATOP
        variables['alphaDstIn'] = AlphaComposite.DST_IN
        variables['alphaDstOut'] = AlphaComposite.DST_OUT
        variables['alphaDstOver'] = AlphaComposite.DST_OVER
        variables['alphaSrc'] = AlphaComposite.SRC
        variables['alphaSrcAtop'] = AlphaComposite.SRC_ATOP
        variables['alphaSrcIn'] = AlphaComposite.SRC_IN
        variables['alphaSrcOut'] = AlphaComposite.SRC_OUT
        variables['alphaSrcOver'] = AlphaComposite.SRC_OVER
        variables['alphaXor'] = AlphaComposite.XOR
    }

    private void idAttributeDelegate( FactoryBuilderSupport builder, Object node, Map attributes ){
       def id = attributes.remove("id")
       if( id && node ){
           builder.setVariable( id, node )
       }
    }

    private void interpolationAttributeDelegate( FactoryBuilderSupport builder, Object node, Map attributes ){
       def interpolation = attributes.remove("interpolation")
       switch( interpolation ){
          case "bicubic": interpolation = AffineTransformOp.TYPE_BICUBIC; break;
          case "bilinear": interpolation = AffineTransformOp.TYPE_BILINEAR; break;
          case "nearest": interpolation = AffineTransformOp.TYPE_NEAREST_NEIGHBOR; break;
       }
       if( interpolation != null ) node.interpolation = interpolation
    }

    private void alphaCompositeAttributeDelegate( FactoryBuilderSupport builder, Object node, Map attributes ){
       def alphaComposite = attributes.remove("alphaComposite")
       if( alphaComposite ){
          if( alphaComposite instanceof AlphaComposite ){
             node.alphaComposite = alphaComposite
          }else if( alphaComposite instanceof Map ){
             def rule = getAlphaCompositeRule(alphaComposite.op)
             def alpha = alphaComposite.alpha
             if( alpha != null ){
                node.alphaComposite = AlphaComposite.getInstance(rule,alpha as float)
             }else{
                node.alphaComposite = AlphaComposite.getInstance(rule)
             }
          }
       }
    }

    private void swingAttributeDelegate( FactoryBuilderSupport fbs, Object node, Map attrs ) {
       fbs.context.x = attrs.remove("x")
       fbs.context.y = attrs.remove("y")
       ['foreground','background'].each { prop ->
          def value = attrs.remove(prop)
          if( value ){
             if( node.metaClass.hasProperty(node,prop) ){
                node."$prop" = ColorCache.getInstance().getColor(value)
             }
          }
       }
       if( attrs.id ) setVariable( attrs.id, node )
    }

    private void swingPostNodeCompletionDelegate( FactoryBuilderSupport fbs, Object parent, Object node ) {
       def x = fbs.context.x
       def y = fbs.context.y
       if( x != null && y != null ){
           def size = node.preferredSize
           node.bounds = [x,y,size.width as int,size.height as int] as Rectangle
       }
    }

    private def getAlphaCompositeRule( value ){
       if( value == null ) {
          return AlphaComposite.SRC_OVER
       }else if( value instanceof Number ){
          return rule as int
       }else if( value instanceof String ){
          return AlphaComposite.@"${value.toUpperCase()}"
       }
       return AlphaComposite.SRC_OVER
    }
}
