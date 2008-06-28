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

package groovy.swing.j2d.factory

import org.jdesktop.swingx.graphics.BlendComposite

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class BlendCompositeFactory extends AbstractGraphicsOperationFactory {
   public boolean isLeaf(){
      return true
   }

   public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
         Map properties ) throws InstantiationException, IllegalAccessException {
      if( FactoryBuilderSupport.checkValueIsTypeNotString( value, name, BlendComposite ) ){
         return value
      }else{
         def mode = properties.remove("mode")
         def alpha = properties.remove("alpha")

         if( mode == null ){
            throw new IllegalArgumentException("Null value for blendComposite.mode");
         }

         def composite = null
         if( mode instanceof BlendComposite.BlendingMode ){
            composite = BlendComposite.getInstance(mode)
         }else if( mode instanceof String ){
            def m = mode.toUpperCase().replaceAll(" ","_")
            composite = BlendComposite.getInstance(BlendComposite.BlendingMode."$m")
         }
         if( alpha != null ) composite = composite.derive( alpha as float )

         if( !composite ) throw new IllegalArgumentException("Invalif value for blendComposite")
         return composite
      }
   }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ){
      // empty
   }
}