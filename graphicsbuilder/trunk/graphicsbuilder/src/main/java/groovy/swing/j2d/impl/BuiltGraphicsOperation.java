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

package groovy.swing.j2d.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class BuiltGraphicsOperation extends GroupingGraphicsOperation {
    private Map variables = new HashMap();

    public BuiltGraphicsOperation( List operations, Map variables ) {
        super( operations );
        this.variables.putAll( variables );
    }

    public Object getProperty( String name ) {
        Object operation = variables.get( name );
        if( operation == null ){
            return super.getProperty( name );
        }
        return operation;
    }
}