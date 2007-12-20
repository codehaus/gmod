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
import java.awt.Component

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
        registerOperations()
    }

    /*
    public def swingView( SwingBuilder builder = new SwingBuilder(), Closure closure ) {
        builder.addAttributeDelegate({ fbs, node, attrs ->
            fbs.context.x = attrs.remove("x")
            fbs.context.y = attrs.remove("y")
        })
        builder.addPostNodeCompletionDelegate({ fbs, parent, node ->
            def x = fbs.context.x
            def y = fbs.context.y
            if( x && y ){
                def size = node.preferredSize
                node.bounds = [x,y,size.width as int,size.height as int] as java.awt.Rectangle
            }
        })

        def proxyBuilderRef = getProxyBuilder()
        setProxyBuilder( builder )
        def go = null
        try {
            def container = builder.panel( closure )
            go = new SwingGraphicsOperation( container )
        } finally {
            setProxyBuilder( proxyBuilderRef )
        }

        def parent = getCurrent()
        if( parent != null ){
           setParent( parent, go )
        }
        nodeCompleted( parent, go )
        return postNodeCompletion( parent, go )
    }
    */

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
        registerGraphicsOperationBeanFactory( "transformations", TransformationGroup )
        registerGraphicsOperationBeanFactory( "rotate", RotateTransformation, false )
        registerGraphicsOperationBeanFactory( "scale", ScaleTransformation, false )
        registerGraphicsOperationBeanFactory( "skew", SkewTransformation, false )
        registerGraphicsOperationBeanFactory( "translate", TranslateTransformation, false )
        
        //
        // paint
        //
        registerGraphicsOperationBeanFactory( "gradientPaint", GradientPaintGraphicsOperation, true )
        registerFactory( "paint", new PaintFactory() )

        /*
        registerGraphicsOperationBeanFactory( "clip", ClipGraphicsOperation, true )
        registerFactory( "color", new ColorFactory() )
        registerGraphicsOperationBeanFactory( "image", ImageGraphicsOperation )
        registerFactory( "stroke", new StrokeFactory() )
        registerGraphicsOperationBeanFactory( "texturePaint", TexturePaintGraphicsOperation, true )
        */
    }
}