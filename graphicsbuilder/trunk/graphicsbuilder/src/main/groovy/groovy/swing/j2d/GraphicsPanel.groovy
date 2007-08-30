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

package groovy.swing.j2d

import java.awt.Graphics
import javax.swing.JPanel
import groovy.swing.j2d.GraphicsOperation

/**
 * A Panel that can use a GraphicsOperation to draw itself.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsPanel extends JPanel {
     private GraphicsOperation graphicsOperation
     private boolean displayed

     /**
      * Returns the current GraphicsOperation of this Panel
      * @return the current GraphicsOperation of this Panel
      */
     public GraphicsOperation getGraphicsOperation(){
         return graphicsOperation
     }

     /**
      * Returns the GraphicsOperation of this Panel.<br>
      * If the panel is visible, a <code>repaint()</code> will be ensued
      */
     public void setGraphicsOperation( GraphicsOperation graphicsOperation ){
         this.graphicsOperation = graphicsOperation
         if( visible ){
             repaint()
         }
     }

     public void paintComponent( Graphics g ){
         if( graphicsOperation ){
             g.clearRect( 0, 0, size.width as int, size.height as int )
             graphicsOperation.execute( g, this )
         }
     }
 }