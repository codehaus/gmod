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

package groovy.swing.j2d.operations

import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation
import groovy.swing.j2d.impl.ExtPathOperation
import groovy.swing.j2d.impl.MoveToExtPathOperation

import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.GeneralPath
import java.awt.image.ImageObserver
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ExtPathGraphicsOperation extends AbstractShapeGraphicsOperation {
   def winding

   private List pathOperations = []

   ExtPathGraphicsOperation() {
      super( "path", [] as String[], ["winding"] as String[] )
   }

   public void addPathOperation( ExtPathOperation operation ) {
      pathOperations.add( operation )
   }

   public boolean isDirty() {
      // shortcut
      if( super.isDirty() ){
         return true
      }
      for( po in pathOperations ){
         if( po.isDirty() ){
             return true
         }
     }
     return false
   }

   protected Shape computeShape( Graphics2D g, ImageObserver observer ) {
      ExtendedGeneralPath path = new ExtendedGeneralPath( getWindingRule() )
      if( pathOperations.size() > 0 && !(pathOperations[0] instanceof MoveToExtPathOperation) ){
         throw new IllegalStateException("You must call 'moveTo' as the first operation of a path")
      }
      pathOperations.each { pathOperation ->
         pathOperation.apply( path, g, observer )
         pathOperation.setDirty( false )
      }
      path.closePath()
      return path
   }

   private int getWindingRule() {
      if( !parameterHasValue("winding") ){
         return GeneralPath.WIND_NON_ZERO
      }

      Object windingValue = getParameterValue( "winding" )
      int winding = GeneralPath.WIND_NON_ZERO

      if( windingValue instanceof Integer ){
         return windingValue
      }else if( windingValue instanceof String ){
         if( "non_zero".compareToIgnoreCase( (String) windingValue ) == 0 ){
            winding = GeneralPath.WIND_NON_ZERO
         }else if( "even_odd".compareToIgnoreCase( (String) windingValue ) == 0 ){
            winding = GeneralPath.WIND_EVEN_ODD
         }else{
            throw new IllegalStateException( "'winding=" + windingValue
                  + "' is not one of [non_zero,even_odd]" )
         }
      }else{
         throw new IllegalStateException( "'winding' value is not a String nor an Integer" );
      }

      return winding
   }
}