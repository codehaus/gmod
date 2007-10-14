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
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JPanel
import groovy.swing.j2d.GraphicsOperation

/**
 * A Panel that can use a GraphicsOperation to draw itself.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsPanel extends JPanel implements PropertyChangeListener {
     private GraphicsOperation graphicsOperation
     private boolean displayed
     private List errorListeners = []

     GraphicsPanel(){
         super( null )
     }

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
         if( graphicsOperation ){
             if( this.graphicsOperation ){
                this.graphicsOperation.removePropertyChangeListener( this )
             }
             this.graphicsOperation = graphicsOperation
             this.graphicsOperation.addPropertyChangeListener( this )
             if( visible ){
                 repaint()
             }
         }
     }

     public void paintComponent( Graphics g ){
         if( graphicsOperation ){
             g.clearRect( 0, 0, size.width as int, size.height as int )
             try{
                 graphicsOperation.execute( g, this )
             }catch( Exception e ){
                 fireGraphicsErrorEvent( e )
             }
         }
     }

     public void addGraphicsErrorListener( GraphicsErrorListener l ){
         errorListeners.add( l )
     }

     public void removeGraphicsErrorListener( GraphicsErrorListener l ){
         errorListeners.remove( l )
     }

     public List getGraphicsErrorListeners(){
         return Collections.unmodifiableList( errorListeners )
     }

     protected void fireGraphicsErrorEvent( Throwable t ) {
         t.printStackTrace()
         def event = new GraphicsErrorEvent( this, t )
         errorListeners.each { listener ->
            listener.errorOccurred( event )
         }
     }

     public void propertyChange( PropertyChangeEvent event ){
         if( visible ){
             repaint()
         }
     }
 }