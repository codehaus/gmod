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

import org.kordamp.groovy.wings.WingXBuilder;

import java.util.Collection;
import java.util.Map;

import org.wingx.YUIxGrid;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class YUIxGridFactory extends AbstractWingXFactory {
   public Object doNewInstance( WingXBuilder builder, Object name, Object value, Map properties )
         throws InstantiationException, IllegalAccessException {
      WingXBuilder.checkValueIsNull( value, name );
      YUIxGrid grid = null;
      Number rows = (Number) properties.remove( "rows" );
      Number cols = (Number) properties.remove( "cols" );
      Object[][] rowData = (Object[][]) properties.remove( "rowData" );
      Object columnNames = properties.remove( "columnNames" );

      if( rows != null && cols != null ){
         grid = new YUIxGrid( rows.intValue(), cols.intValue() );
      }else if( rowData != null && columnNames != null ){
         if( columnNames instanceof Object[] ){
            grid = new YUIxGrid( rowData, (Object[]) columnNames );
         }else if( columnNames instanceof Collection ){
            grid = new YUIxGrid( rowData, ((Collection) columnNames).toArray() );
         }
      }else{
         grid = new YUIxGrid();
      }

      return grid;
   }
}