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

import groovy.util.FactoryBuilderSupport

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.Shape
import java.awt.image.ImageObserver

import groovy.swing.j2d.impl.*
import groovy.swing.j2d.factory.*
import groovy.swing.j2d.operations.*

import groovy.swing.SwingBuilder
//import groovy.swing.SwingBuilderAdapter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsBuilder extends FactoryBuilderSupport {
    public static final String OPERATION_NAME = "_OPERATION_NAME_"

    private boolean building = false
    private List operations
    private Map variables = [:]

    public GraphicsBuilder() {
        registerOperations()
    }

    public GraphicsOperation build( Closure closure ) {
        if( building ){
            throw new IllegalStateException( "Can't nest build() calls" )
        }
        building = true
        operations = []
        closure.setDelegate( this )
        GraphicsOperation go
        try {
            closure.call()
            go = new BuiltGraphicsOperation( operations, variables )
            go.verify()
        }finally {
            operations = []
            variables.clear()
            building = false
        }
        return go
    }

    public List getOperations() {
        return operations;
    }

    public Object getProperty( String name ) {
        Object operation = variables.get( name )
        if( operation == null ){
            return super.getProperty( name )
        }
        return operation
    }

    public def swingView( Closure closure ) {
        return swingView( new SwingBuilder(), closure )
    }

    public def swingView( SwingBuilder builder, Closure closure ) {
        def proxyBuilder = getProxyBuilder()
        //def adapter = new SwingBuilderAdapter( builder )
        //setProxyBuilder( adapter )
        def container = null
        try {
            /*
            closure.setDelegate( adapter )
            container = adapter.panel {
               closure.call()
            }
            */
            def go = new SwingGraphicsOperation( container )
            def parent = getCurrent()
            if( parent instanceof GroupingGraphicsOperation || parent instanceof ContextualGraphicsOperation ){
                parent.addOperation( go )
            }else{
                operations.add( go )
            }
        } finally {
            setProxyBuilder( proxyBuilder )
        }
        return container
    }

    protected void postIstantiate( Object name, Map attributes, Object node ) {
        Map context = getContext()
        String operationName = context.get( OPERATION_NAME )
        if( operationName != null && node != null ){
            variables.put( operationName, node )
        }
    }

    protected void preInstantiate( Object name, Map attributes, Object value ) {
        Map context = getContext()
        context.put( OPERATION_NAME, attributes.remove( "id" ) )
    }

    private registerGraphicsOperationBeanFactory( String name, Class beanClass ){
        registerFactory( name, new GraphicsOperationBeanFactory(beanClass,false) )
    }

    private registerGraphicsOperationBeanFactory( String name, Class beanClass, boolean leaf ){
        registerFactory( name, new GraphicsOperationBeanFactory(beanClass,leaf) )
    }

    private void registerOperations() {
        registerGraphicsOperationBeanFactory( "arc", ArcGraphicsOperation )
        registerGraphicsOperationBeanFactory( "circle", CircleGraphicsOperation )
        registerFactory( "color", new ColorFactory() )
        registerGraphicsOperationBeanFactory( "cubicCurve", CubicCurveGraphicsOperation )
        registerGraphicsOperationBeanFactory( "draw", DrawGraphicsOperation )
        registerGraphicsOperationBeanFactory( "ellipse", EllipseGraphicsOperation )
        registerFactory( "font", new FontFactory() )
        registerGraphicsOperationBeanFactory( "gradientPaint", GradientPaintGraphicsOperation, true )
        registerGraphicsOperationBeanFactory( "group", GroupingGraphicsOperation )
        registerGraphicsOperationBeanFactory( "image", ImageGraphicsOperation )
        registerGraphicsOperationBeanFactory( "line", LineGraphicsOperation )
        registerFactory( "operation", new OperationFactory() )
        registerFactory( "paint", new PaintFactory() )
        registerGraphicsOperationBeanFactory( "polygon", PolygonGraphicsOperation )
        registerGraphicsOperationBeanFactory( "polyline", PolylineGraphicsOperation )
        registerGraphicsOperationBeanFactory( "quadCurve", QuadCurveGraphicsOperation )
        registerFactory( "rect", new RectFactory() )
        registerFactory( "stroke", new StrokeFactory() )
        registerGraphicsOperationBeanFactory( "text", TextGraphicsOperation )
        registerGraphicsOperationBeanFactory( "texturePaint", TexturePaintGraphicsOperation, true )
        registerGraphicsOperationBeanFactory( "renderingHint", RenderingHintGraphicsOperation, true )

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
        registerGraphicsOperationBeanFactory( "transformations", TransformationsGraphicsOperation )
        registerGraphicsOperationBeanFactory( "rotate", RotateGraphicsOperation )
        registerGraphicsOperationBeanFactory( "scale", ScaleGraphicsOperation )
        registerGraphicsOperationBeanFactory( "skew", SkewGraphicsOperation )
        registerGraphicsOperationBeanFactory( "translate", TranslateGraphicsOperation )

        //
        // binding
        //
        registerFactory( "bind", new BindFactory() )
    }
}