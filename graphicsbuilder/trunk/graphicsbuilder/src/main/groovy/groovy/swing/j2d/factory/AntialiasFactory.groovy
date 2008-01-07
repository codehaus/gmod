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

import groovy.swing.j2d.operations.Grouping
import groovy.swing.j2d.operations.misc.RenderingHintGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class AntialiasFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
            Map properties ) throws InstantiationException, IllegalAccessException {
        RenderingHintGraphicsOperation go = new RenderingHintGraphicsOperation()
        go.key = 'antialiasing'
        setAntialiasValue(go,value)
        setAntialiasValue(go,properties.remove("enabled"))
        return go
    }

    public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node, Map attributes ){
       return false
    }

    public boolean isLeaf(){
       return true
    }

    private void setAntialiasValue( go, value ){
       if( value == null ){ go.value = 'antialias on' }
       else if( value instanceof Boolean ){
          go.value = "antialias ${value?'on':'off'}"
       }else if( value instanceof String ){
          if( "off" == value ){ go.value = 'antialias off' }
          else if( "on" == value ){ go.value = 'antialias on' }
          else{ throw new IllegalArgumentException("value must be a bololean or any of ['on'|'off']") }
       }
    }
}