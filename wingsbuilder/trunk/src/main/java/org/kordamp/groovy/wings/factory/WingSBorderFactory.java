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

package org.kordamp.groovy.wings.factory;

import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.wings.SComponent;
import org.wings.SRootContainer;
import org.wings.border.SBorder;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class WingSBorderFactory extends AbstractWingSFactory {
   public boolean isLeaf() {
      // no children
      return true;
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node, Map attributes ) {
      // never do bean apply
      return false;
   }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
      Map context = builder.getContext();
      Boolean applyBorderToParent = (Boolean) context.get( "applyBorderToParent" );
      if( applyBorderToParent != null && applyBorderToParent.booleanValue() ){
         if( parent instanceof SRootContainer ){
            setParent( builder, ((SRootContainer) parent).getContentPane(), child );
         }else if( parent instanceof SComponent ){
            ((SComponent) parent).setBorder( (SBorder) child );
         }else{
            throw new RuntimeException(
                  "Border cannot be applied to parent, it is neither a JComponent or a RootPaneContainer" );
         }
      }
   }
}