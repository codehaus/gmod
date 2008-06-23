/*
 * Copyright 2008 the original author or authors.
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
package org.kordamp.groovy.util.impl

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ProxyMetaProperty extends MetaProperty {
    private ProxyHandler proxyHandler
    
    ProxyMetaProperty( String name, Class type, ProxyHandler proxyHandler ){
       super( name, type )
       this.proxyHandler = proxyHandler
    }
    
    Object getProperty( Object object ) {
       proxyHandler.doGetProperty( getName() )
    }

    void setProperty( Object object, Object newValue ) {
       newValue = DefaultTypeTransformation.castToType( newValue, getType() )
       proxyHandler.doSetProperty( getName(), newValue )
    }
}
