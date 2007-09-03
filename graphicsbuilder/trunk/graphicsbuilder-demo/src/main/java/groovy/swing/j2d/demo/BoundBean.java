package groovy.swing.j2d.demo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BoundBean {
   private Object height;
   private PropertyChangeSupport pcs;
   private Object width;
   private Object x;
   private Object y;

   public BoundBean() {
      pcs = new PropertyChangeSupport( this );
   }

   public void addPropertyChangeListener( PropertyChangeListener listener ) {
      pcs.addPropertyChangeListener( listener );
   }

   public Object getHeight() {
      return height;
   }

   public Object getWidth() {
      return width;
   }

   public Object getX() {
      return x;
   }

   public Object getY() {
      return y;
   }

   public void removePropertyChangeListener( PropertyChangeListener listener ) {
      pcs.removePropertyChangeListener( listener );
   }

   public void setHeight( Object height ) {
      Object oldValue = this.height;
      this.height = height;
      pcs.firePropertyChange( "height", oldValue, height );
   }

   public void setWidth( Object width ) {
      Object oldValue = this.width;
      this.width = width;
      pcs.firePropertyChange( "width", oldValue, width );
   }

   public void setX( Object x ) {
      Object oldValue = this.x;
      this.x = x;
      pcs.firePropertyChange( "x", oldValue, x );
   }

   public void setY( Object y ) {
      Object oldValue = this.y;
      this.y = y;
      pcs.firePropertyChange( "y", oldValue, x );
   }
}