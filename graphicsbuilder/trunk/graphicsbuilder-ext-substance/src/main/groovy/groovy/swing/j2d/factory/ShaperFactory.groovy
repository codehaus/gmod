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

import groovy.swing.j2d.operations.shapes.ShaperGraphicsOperation

//import org.jvnet.substance.shaperpack.BasePolygonShaper

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ShaperFactory extends AbstractGraphicsOperationFactory {
    private String name
    private Class shaperClass
    
    ShaperFactory( String name, Class shaperClass ) {
       this.name = name
       this.shaperClass = shaperClass
    }
    
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
            Map properties ) throws InstantiationException, IllegalAccessException {
        new ShaperGraphicsOperation( name, shaperClass.newInstance() )
    }
}