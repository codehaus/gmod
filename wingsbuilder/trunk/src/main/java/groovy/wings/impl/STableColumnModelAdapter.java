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

package groovy.wings.impl;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.wings.table.SDefaultTableColumnModel;
import org.wings.table.STableColumn;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class STableColumnModelAdapter extends SDefaultTableColumnModel {
   private static final long serialVersionUID = -6603008579764187093L;

   public STableColumnModelAdapter( TableColumnModel tableColumnModel ) {
      super();
      for( int i = 0; i < tableColumnModel.getColumnCount(); i++ ){
         TableColumn tablecolumn = tableColumnModel.getColumn( i );
         String width = String.valueOf( tablecolumn.getWidth() );
         width = "null".equals( width ) ? "100" : width;
         STableColumn sTableColumn = new STableColumn( i, width );
         sTableColumn.setHeaderValue( tablecolumn.getHeaderValue() );
         addColumn( sTableColumn );
      }
   }
}
