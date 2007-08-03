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
 * limitations under the License.
 */

package org.kordamp.groovy.wings.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;

import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.SIcon;

/**
 * Base on the original groovy.swing.factory.RichActionWidgetFactory.
 * 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SRichActionWidgetFactory extends AbstractWingSFactory {
   private static final Class[] ACTION_ARGS = { Action.class };
   private static final Class[] BOOLEAN_ARGS = { Boolean.TYPE };
   private static final Class[] ICON_ARGS = { SIcon.class };
   private static final Class[] STRING_ARGS = { String.class };

   private Constructor actionCtor;
   private Constructor booleanCtor;
   private Constructor iconCtor;
   private Class klass;
   private Constructor stringCtor;

   public SRichActionWidgetFactory( Class klass ) {
      try{
         actionCtor = klass.getConstructor( ACTION_ARGS );

      }catch( NoSuchMethodException ex ){
         // ignore
      }catch( SecurityException ex ){
         Logger.getLogger( "global" )
               .log( Level.SEVERE, null, ex );
      }
      try{
         iconCtor = klass.getConstructor( ICON_ARGS );
      }catch( NoSuchMethodException ex ){
         // ignore
      }catch( SecurityException ex ){
         Logger.getLogger( "global" )
               .log( Level.SEVERE, null, ex );
      }
      try{
         stringCtor = klass.getConstructor( STRING_ARGS );
      }catch( NoSuchMethodException ex ){
         // ignore
      }catch( SecurityException ex ){
         Logger.getLogger( "global" )
               .log( Level.SEVERE, null, ex );
      }
      try{
         booleanCtor = klass.getConstructor( BOOLEAN_ARGS );
      }catch( NoSuchMethodException ex ){
         // ignore
      }catch( SecurityException ex ){
         Logger.getLogger( "global" )
               .log( Level.SEVERE, null, ex );
      }
      this.klass = klass;
   }

   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      try{
         // some wingS widgets do not support property 'label'
         // as of wingS 3.0
         if( properties.containsKey( "label" ) ){
            properties.put( "text", properties.get( "label" ) );
            properties.remove( "label" );
         }
         if( value == null ){
            return klass.newInstance();
         }else if( value instanceof Action ){
            return actionCtor.newInstance( new Object[] { value } );
         }else if( value instanceof SIcon ){
            return iconCtor.newInstance( new Object[] { value } );
         }else if( value instanceof String ){
            return stringCtor.newInstance( new Object[] { value } );
         }else if( value instanceof Boolean ){
            return booleanCtor.newInstance( new Object[] { value } );
         }else if( klass.isAssignableFrom( value.getClass() ) ){
            return value;
         }else{
            throw new RuntimeException(
                  name
                        + " can only have a value argument of type javax.swing.Action, org.wings.SIcon, java.lang.String, java.lang.Boolean, or "
                        + klass.getName() );
         }
      }catch( IllegalArgumentException e ){
         throw new RuntimeException( "Failed to create component for '" + name + "' reason: " + e,
               e );
      }catch( InvocationTargetException e ){
         throw new RuntimeException( "Failed to create component for '" + name + "' reason: " + e,
               e );
      }
   }
}