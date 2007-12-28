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

package groovy.swing.j2d.factory

import groovy.swing.j2d.Grouping
import groovy.swing.j2d.operations.ImageGraphicsOperation

import java.awt.Image

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ImageFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
            Map properties ) throws InstantiationException, IllegalAccessException {
        ImageGraphicsOperation go = new ImageGraphicsOperation()
        if( value != null && Image.class.isAssignableFrom( value.class ) ||
              value instanceof ImageGraphicsOperation ){
            go.image = value
        }
        return go
    }

    public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
       if( parent instanceof Grouping ) {
          parent.addOperation( child )
       }else{
          throw new IllegalArgumentException("image() can only be nested in group()")
       }
    }

    /*
    public boolean isLeaf(){
        return false
    }
    */
}