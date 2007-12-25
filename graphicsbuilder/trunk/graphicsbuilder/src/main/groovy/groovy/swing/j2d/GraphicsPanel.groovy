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
import java.awt.LayoutManager
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JPanel
import groovy.swing.j2d.event.*

/**
 * A Panel that can use a GraphicsOperation to draw itself.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsPanel extends JPanel implements PropertyChangeListener, MouseListener,
   MouseMotionListener, MouseWheelListener, KeyListener {
     private GraphicsOperation graphicsOperation
     private GraphicsContext context = new GraphicsContext()
     private boolean displayed
     private List errorListeners = []

     GraphicsPanel(){
         super( null )
         addMouseListener( this )
         addMouseMotionListener( this )
         addMouseWheelListener( this )
         addKeyListener( this )
     }

     public void setLayout( LayoutManager mgr ){
         // do not allow the layout to be changed
     }

     /**
      * Returns the current GraphicsOperation of this Panel
      * @return the current GraphicsOperation of this Panel
      */
     public GraphicsOperation getGraphicsOperation(){
         return graphicsOperation
     }

     /**
      * Sets the GraphicsOperation for this Panel.<br>
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

     /**
      * Returns the current GraphicsOperation of this Panel.<br>
      * Alias for getGraphicsOperation()
      * @return the current GraphicsOperation of this Panel
      */
     public GraphicsOperation getGo(){
         return graphicsOperation
     }

     /**
      * Sets the GraphicsOperation for this Panel.<br>
      * Alias for setGraphicsOperation()
      * If the panel is visible, a <code>repaint()</code> will be ensued
      */
     public void setGo( GraphicsOperation graphicsOperation ){
         setGraphicsOperation( graphicsOperation )
     }

     public void paintComponent( Graphics g ){
         context.g = g
         context.target = this
         if( graphicsOperation ){
             g.clearRect( 0, 0, size.width as int, size.height as int )
             try{
                 context.shapes = []
                 graphicsOperation.execute( context )
             }catch( Exception e ){
                 fireGraphicsErrorEvent( e )
             }
         }
     }

     public void addGraphicsErrorListener( GraphicsErrorListener listener ){
         if( !listener ) return;
         if( errorListeners.contains(listener) ) return;
         errorListeners.add( listener )
     }

     public void removeGraphicsErrorListener( GraphicsErrorListener listener ){
         if( !listener ) return;
         errorListeners.remove( listener )
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

     /* ===== MouseListener ===== */

     public void mouseEntered( MouseEvent e ){
        // noop
        //fireMouseEvent( e, "mouseEntered" )
     }

     public void mouseExited( MouseEvent e ){
         // noop
         //fireMouseEvent( e, "mouseExited" )
     }

     public void mousePressed( MouseEvent e ){
         fireMouseEvent( e, "mousePressed" )
     }

     public void mouseReleased( MouseEvent e ){
         fireMouseEvent( e, "mouseReleased" )
     }

     public void mouseClicked( MouseEvent e ){
         fireMouseEvent( e, "mouseClicked" )
     }


     /* ===== MouseMotionListener ===== */

     public void mouseMoved( MouseEvent e ){
         fireMouseEvent( e, "mouseMoved" )
     }

     public void mouseDragged( MouseEvent e ){
         fireMouseEvent( e, "mouseDragged" )
     }

     /* ===== MouseWheelListener ===== */

     public void mouseWheelMoved( MouseWheelEvent e ){
         fireMouseEvent( e, "mouseWheelMoved" )
     }

     /* ===== KeyListener ===== */

     public void keyPressed( KeyEvent e ){

     }

     public void keyReleased( KeyEvent e ){

     }

     public void keyTyped( KeyEvent e ){

     }

     /* ===== PRIVATE ===== */

     private void fireMouseEvent( MouseEvent e, String mouseEventMethod ){
         if( !context.shapes ) return
         def shape = getSourceShape(e)
         if( shape ){
            def inputEvent = new GraphicsInputEvent( this, e, shape )
            shape."$mouseEventMethod"( inputEvent )
         }
     }

     private def getSourceShape( MouseEvent e ){
         def shapes = context.shapes
         for( shape in shapes.reverse() ){
             if( shape instanceof ShapeProvider ){
                 def s = shape.getGloballyTransformedShape(context)
                 if( s.contains(e.point) ){
                    return shape
                 }
             }
         }
         return null
     }
 }