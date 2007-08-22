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

package groovy.swing.j2d;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.swing.j2d.factory.AbstractGraphicsOperationFactory;
import groovy.swing.j2d.factory.ColorFactory;
import groovy.swing.j2d.factory.ContextualGraphicsOperationFactory;
import groovy.swing.j2d.factory.FontFactory;
import groovy.swing.j2d.factory.LineFactory;
import groovy.swing.j2d.factory.MethodInvokingGraphicsOperationWithSingleValueFactory;
import groovy.swing.j2d.factory.OperationFactory;
import groovy.swing.j2d.factory.RectFactory;
import groovy.swing.j2d.factory.StringFactory;
import groovy.swing.j2d.impl.VerifiableGraphicsOperation;
import groovy.swing.j2d.operations.ArcGraphicsOperation;
import groovy.swing.j2d.operations.CircleGraphicsOperation;
import groovy.swing.j2d.operations.CubicCurveGraphicsOperation;
import groovy.swing.j2d.operations.DrawGraphicsOperation;
import groovy.swing.j2d.operations.EllipseGraphicsOperation;
import groovy.swing.j2d.operations.GradientPaintGraphicsOperation;
import groovy.swing.j2d.operations.ImageGraphicsOperation;
import groovy.swing.j2d.operations.OvalGraphicsOperation;
import groovy.swing.j2d.operations.PolygonGraphicsOperation;
import groovy.swing.j2d.operations.PolylineGraphicsOperation;
import groovy.swing.j2d.operations.QuadCurveGraphicsOperation;
import groovy.swing.j2d.operations.Rect3DGraphicsOperation;
import groovy.swing.j2d.operations.StrokeGraphicsOperation;
import groovy.swing.j2d.operations.TexturePaintGraphicsOperation;
import groovy.util.FactoryBuilderSupport;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GraphicsBuilder extends FactoryBuilderSupport {
   public static final String OPERATION_NAME = "_OPERATION_NAME_";
   private static ColorCache colorCache = new ColorCache();

   public static void addColor( String name, Color color ) {
      colorCache.setColor( name, color );
   }

   public static Color getColor( Object name ) {
      if( name instanceof String ){
         return colorCache.getColor( (String) name );
      }else if( name instanceof Color ){
         return (Color) name;
      }
      return null;
   }

   private boolean building = false;
   private List operations;
   private Map variables = new HashMap();

   public GraphicsBuilder() {
      registerOperations();
   }

   public GraphicsOperation build( Closure closure ) {
      if( building ){
         throw new IllegalStateException( "Can't nest build() calls" );
      }
      building = true;
      operations = new ArrayList();
      closure.setDelegate( this );
      closure.call();
      for( Iterator os = operations.iterator(); os.hasNext(); ){
         GraphicsOperation o = (GraphicsOperation) os.next();
         if( o instanceof VerifiableGraphicsOperation ){
            ((VerifiableGraphicsOperation) o).verify();
         }
      }
      GraphicsOperation go = new DefaultGraphicsOperation( operations, variables );
      operations = null;
      variables.clear();
      building = false;
      return go;
   }

   public List getOperations() {
      return operations;
   }

   public Object getProperty( String name ) {
      Object operation = variables.get( name );
      if( operation == null ){
         return super.getProperty( name );
      }
      return operation;
   }

   protected void postIstantiate( Object name, Map attributes, Object node ) {
      Map context = getContext();
      String operationName = (String) context.get( OPERATION_NAME );
      if( operationName != null && node != null ){
         variables.put( operationName, node );
      }
   }

   protected void preInstantiate( Object name, Map attributes, Object value ) {
      Map context = getContext();
      String operationName = (String) attributes.remove( "id" );
      context.put( OPERATION_NAME, operationName );
   }

   protected void registerGraphicsOperationFactory( String name, final Class sourceClass,
         boolean contextual ) {
      if( contextual ){
         registerFactory( name, new ContextualGraphicsOperationFactory(){
            protected GraphicsOperation getGraphicsOperation( FactoryBuilderSupport builder,
                  Object name, Object value, Map properties ) throws InstantiationException,
                  IllegalAccessException {
               return (GraphicsOperation) sourceClass.newInstance();
            }
         } );
      }else{
         registerFactory( name, new AbstractGraphicsOperationFactory(){
            public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
                  Map properties ) throws InstantiationException, IllegalAccessException {
               if( checkValueIsTypeNotString( value, name, sourceClass ) ){
                  return value;
               }else{
                  return sourceClass.newInstance();
               }
            }
         } );
      }
   }

   private void registerOperations() {
      registerGraphicsOperationFactory( "arc", ArcGraphicsOperation.class, true );
      registerGraphicsOperationFactory( "circle", CircleGraphicsOperation.class, true );
      registerFactory( "color", new ColorFactory() );
      registerGraphicsOperationFactory( "cubicCurve", CubicCurveGraphicsOperation.class, true );
      registerGraphicsOperationFactory( "draw", DrawGraphicsOperation.class, true );
      registerGraphicsOperationFactory( "ellipse", EllipseGraphicsOperation.class, true );
      registerFactory( "font", new FontFactory() );
      registerGraphicsOperationFactory( "gradientPaint", GradientPaintGraphicsOperation.class,
            false );
      registerGraphicsOperationFactory( "image", ImageGraphicsOperation.class, true );
      registerFactory( "line", new LineFactory() );
      registerFactory( "operation", new OperationFactory() );
      registerGraphicsOperationFactory( "oval", OvalGraphicsOperation.class, true );
      registerFactory( "paint", new MethodInvokingGraphicsOperationWithSingleValueFactory( "paint",
            "paint", Paint.class ) );
      registerGraphicsOperationFactory( "polygon", PolygonGraphicsOperation.class, true );
      registerGraphicsOperationFactory( "polyline", PolylineGraphicsOperation.class, true );
      registerGraphicsOperationFactory( "quadCurve", QuadCurveGraphicsOperation.class, true );
      registerFactory( "rect", new RectFactory() );
      registerGraphicsOperationFactory( "rect3d", Rect3DGraphicsOperation.class, true );
      registerFactory( "string", new StringFactory() );
      registerGraphicsOperationFactory( "stroke", StrokeGraphicsOperation.class, false );
      registerGraphicsOperationFactory( "texturePaint", TexturePaintGraphicsOperation.class, false );
   }

   private static class DefaultGraphicsOperation extends GroovyObjectSupport implements
         GraphicsOperation {
      private List operations = new ArrayList();
      private Map variables = new HashMap();

      public DefaultGraphicsOperation( List operations, Map variables ) {
         this.operations.addAll( operations );
         this.variables.putAll( variables );
      }

      public void execute( Graphics2D g, ImageObserver observer ) {
         for( Iterator i = operations.iterator(); i.hasNext(); ){
            GraphicsOperation go = (GraphicsOperation) i.next();
            go.execute( g, observer );
         }
      }

      public Shape getClip( Graphics2D g, ImageObserver observer ) {
         return null;
      }

      public String[] getParameterNames() {
         return new String[0];
      }

      public Object getParameterValue( String name ) {
         return null;
      }

      public Object getProperty( String name ) {
         Object operation = variables.get( name );
         if( operation == null ){
            return super.getProperty( name );
         }
         return operation;
      }

      public boolean parameterHasValue( String name ) {
         return false;
      }

      public void setParameterValue( String name, Object value ) {
      }
   }
}
