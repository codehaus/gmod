package org.codehaus.groovy.junctions.swingx;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.Action;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

public class PostPane extends JXTaskPane {
   public static final String PUBLISHED_DATE_CHANGED_KEY = "publishedDate";
   public final static String uiClassID = "swingx/PostPaneUI";

   private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat( "MMM dd, yyyy" );
   private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat( "hh:mm a" );

   private static final long serialVersionUID = -4541811681843453907L;

   // ensure at least the default ui is registered
   static{
      LookAndFeelAddons.contribute( new PostPaneAddon() );
   }

   private Action expandedChangedAction;
   private Date publishedDate;

   public PostPane() {
      addPropertyChangeListener( EXPANDED_CHANGED_KEY, new ActionPropertyChangeListener() );
   }

   public Action getExpandedChangedAction() {
      return expandedChangedAction;
   }

   public Date getPublishedDate() {
      return publishedDate;
   }

   public String getPublishedDateText() {
      Calendar today = Calendar.getInstance();
      Calendar date = Calendar.getInstance();
      date.setTime( publishedDate );

      if( today.get( Calendar.YEAR ) == date.get( Calendar.YEAR )
            && today.get( Calendar.DAY_OF_YEAR ) == date.get( Calendar.DAY_OF_YEAR ) ){
         return HOUR_FORMAT.format( publishedDate );
      }
      return DAY_FORMAT.format( publishedDate );
   }

   public void setComponentOrientation( ComponentOrientation o ) {
      // nothing...
   }

   public void setExpandedChangedAction( Action expandedChangedAction ) {
      this.expandedChangedAction = expandedChangedAction;
   }

   public void setPublishedDate( Object publishedDate ) throws ParseException{
   	Date old = this.publishedDate;
   	Date newDate;
  	if (publishedDate instanceof Date) {
      this.publishedDate = (Date)publishedDate;
      newDate = this.publishedDate;
   	} else {
   		//It's a String
   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
   		newDate = df.parse((String)publishedDate);
   		this.publishedDate = newDate;
   	}
   	firePropertyChange ( PUBLISHED_DATE_CHANGED_KEY, old, newDate);
   }

   public String getUIClassID() {
      return PostPane.uiClassID;
   }

   class ActionPropertyChangeListener implements PropertyChangeListener {
      public void propertyChange( PropertyChangeEvent evt ) {
         if( getExpandedChangedAction() != null ){
            getExpandedChangedAction().actionPerformed(
                  new ActionEvent( this, ActionEvent.ACTION_PERFORMED, EXPANDED_CHANGED_KEY,
                        (int) ActionEvent.ACTION_EVENT_MASK ) );
         }
      }
   }
}
