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

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.Shape
import java.awt.Rectangle
import java.awt.Component
import java.awt.geom.Area

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
        addAttributeDelegate({ builder, node, attributes ->
           def id = attributes.remove("id")
           if( id && node ){
               builder.setVariable( id, node )
           }
        })

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
        registerGraphicsOperationBeanFactory( "line", LineGraphicsOperation, false )
        registerGraphicsOperationBeanFactory( "cubicCurve", CubicCurveGraphicsOperation, false )
        registerGraphicsOperationBeanFactory( "polyline", PolylineGraphicsOperation, false )
        registerGraphicsOperationBeanFactory( "quadCurve", QuadCurveGraphicsOperation, false )

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
        registerFactory( "freeze", new TransformationFactory(FreezeTransformation) )

        //
        // paint
        //
        registerGraphicsOperationBeanFactory( "gradientPaint", GradientPaintGraphicsOperation, true )
        registerFactory( "paint", new PaintFactory() )
        registerGraphicsOperationBeanFactory( "texturePaint", TexturePaintGraphicsOperation, true )
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
}