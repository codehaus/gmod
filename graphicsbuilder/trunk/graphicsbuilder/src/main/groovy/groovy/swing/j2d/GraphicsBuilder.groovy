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
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.Shape
import java.awt.Rectangle
import java.awt.Component
import java.awt.geom.Area
import java.awt.image.AffineTransformOp

import groovy.swing.factory.BindFactory
import groovy.swing.factory.ModelFactory
import groovy.swing.j2d.impl.*
import groovy.swing.j2d.factory.*
import groovy.swing.j2d.operations.*

import groovy.swing.SwingBuilder

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsBuilder extends FactoryBuilderSupport {
    private boolean building = false
    private GroovyShell shell

    public GraphicsBuilder() {
        GraphicsBuilder.extendShapes()
        GraphicsBuilder.extendColor()
        registerOperations()
    }

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
          Color.metaClass.derive << { Map props ->
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
        //registerFactory( "operation", new OperationFactory() )
        //registerFactory( "outline", new OutlineFactory() )
        registerGraphicsOperationBeanFactory( "renderingHint", RenderingHintGraphicsOperation, true )
        registerFactory( "shape", new ShapeFactory() )
        registerFactory( "bind", new BindFactory() )
        addAttributeDelegate( BindFactory.&bindingAttributeDelegate )
        registerFactory( "image", new ImageFactory() )
        registerFactory( "stroke", new StrokeFactory() )
        registerFactory( "color", new ColorFactory() )
        registerFactory( "clip", new ClipFactory() )
        registerFactory( "\$swing", new SwingFactory() )

        //
        // shapes
        //
        registerGraphicsOperationBeanFactory( "arc", ArcGraphicsOperation )
        registerGraphicsOperationBeanFactory( "circle", CircleGraphicsOperation )
        registerGraphicsOperationBeanFactory( "ellipse", EllipseGraphicsOperation )
        registerGraphicsOperationBeanFactory( "polygon", PolygonGraphicsOperation )
        registerGraphicsOperationBeanFactory( "rect", RectGraphicsOperation )
        registerGraphicsOperationBeanFactory( "text", TextGraphicsOperation )

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
        // paint
        //
        registerGraphicsOperationBeanFactory( "gradientPaint", GradientPaintGraphicsOperation, true )
        registerGraphicsOperationBeanFactory( "multiPaint", MultiPaintGraphicsOperation )
        registerFactory( "paint", new PaintFactory() )
        registerGraphicsOperationBeanFactory( "texturePaint", TexturePaintGraphicsOperation, true )
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
          case "bicubic": interpolation = AffineTransformOP.TYPE_BICUBIC; break;
          case "bilinear": interpolation = AffineTransformOP.TYPE_BILINEAR; break;
          case "nearest": interpolation = AffineTransformOP.TYPE_NEAREST_NEIGHBOR; break;
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
       if( x && y ){
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