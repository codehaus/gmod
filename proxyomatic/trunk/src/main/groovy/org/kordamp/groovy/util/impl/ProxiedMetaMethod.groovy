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

import java.lang.reflect.Modifier
import org.codehaus.groovy.reflection.CachedClass

/**
 * Contains code borrowed from [groovy.util.ProxyGenerator, org.codehaus.groovy.runtime.MetaClassHelper]
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ProxiedMetaMethod extends MetaMethod {
    private MethodKey methodKey
    private ProxyHandler proxyHandler
    private CachedClass theClass
    
    ProxiedMetaMethod( MethodKey methodKey, ProxyHandler proxyHandler, CachedClass theClass ){
       this.methodKey = methodKey
       this.proxyHandler = proxyHandler
       this.theClass = theClass
    }
    
    int getModifiers() {
       Modifier.PUBLIC
    }

    String getName() {
       methodKey.name
    }

    Class getReturnType() {
       methodKey.returnType
    }

    CachedClass getDeclaringClass() {
       theClass
    }

    Object invoke( Object object, Object[] arguments ) {
       proxyHandler.invokeCustom( methodKey, arguments )
    }

    protected Class[] getPT() {
       methodKey.parameterTypes
    }
}
