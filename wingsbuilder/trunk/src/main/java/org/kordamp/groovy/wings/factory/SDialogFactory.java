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

import java.util.LinkedList;
import java.util.Map;

import org.kordamp.groovy.wings.WingSBuilder;
import org.wings.SDialog;
import org.wings.SFrame;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SDialogFactory extends AbstractWingSFactory {
   public Object doNewInstance( WingSBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      if( WingSBuilder.checkValueIsType( value, name, SDialog.class ) ){
         return value;
      }
      SDialog dialog;
      Object owner = properties.remove( "owner" );
      LinkedList containingWindows = builder.getContainingWindows();
      // if owner not explicit, use the last window type in the list
      if( (owner == null) && !containingWindows.isEmpty() ){
         owner = containingWindows.getLast();
      }
      if( owner instanceof SFrame ){
         dialog = new SDialog( (SFrame) owner );
      }else{
         dialog = new SDialog();
      }
      containingWindows.add( dialog );
      return dialog;
   }
}