package org.codehaus.groovy.junctions.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;

public class PostPaneUI extends BasicTaskPaneUI {
   public static ComponentUI createUI( JComponent c ) {
      return new PostPaneUI();
   }

   public void update( Graphics g, JComponent c ) {
      if( c.isOpaque() ){
         g.setColor( c.getParent()
               .getBackground() );
         g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
         g.setColor( c.getBackground() );
         g.fillRect( 0, roundHeight, c.getWidth(), c.getHeight() - roundHeight );
      }
      paint( g, c );
   }

   protected Border createPaneBorder() {
      return new GlossyPaneBorder();
   }

   protected static class HeaderChevronIcon implements Icon {
      private static int width = 7;
      private static int height = 14;
      boolean up = true;

      public HeaderChevronIcon( boolean up ) {
         this.up = up;
      }

      public int getIconHeight() {
         return width;
      }

      public int getIconWidth() {
         return height;
      }

      public void paintIcon( Component c, Graphics g, int x, int y ) {
         Color gradientStart = new Color( 0, 166, 220 );
         Color gradientEnd = new Color( 0, 77, 152 );
         int thick = 6;
         GradientPaint gradient1 = new GradientPaint( x, y, gradientStart, x + height, y,
               gradientEnd );
         Graphics2D g2 = (Graphics2D) g;
         g2.setPaint( gradient1 );
         x++;
         if( up ){
            y += (width * 1.5) - 1;
            int[] xpoints = { x, x, x + width, x + height, x + height, x + width };
            int[] ypoints = { y, y - thick, y - width - thick, y - thick, y, y - width };
            Polygon polygon = new Polygon( xpoints, ypoints, 6 );
            g2.fillPolygon( polygon );
            g2.setPaint( Color.WHITE );
            g2.drawPolygon( polygon );

         }else{
            y -= 2;
            int[] xpoints = { x, x, x + width, x + height, x + height, x + width };
            int[] ypoints = { y, y + thick, y + width + thick, y + thick, y, y + width };
            Polygon polygon = new Polygon( xpoints, ypoints, 6 );
            g2.fillPolygon( polygon );
            g2.setPaint( Color.WHITE );
            g2.drawPolygon( polygon );

         }
      }
   }

   class GlossyPaneBorder extends PaneBorder {
      protected JLabel label2;

      GlossyPaneBorder() {
         label2 = new JLabel();
         label2.setOpaque( false );
      }

      @Override
      public Dimension getPreferredSize( JXTaskPane group ) {
         Dimension dim = null;

         configureLabel( group );
         dim = label.getPreferredSize();

         // add the title left offset
         dim.width += 3;
         // add the controls width
         dim.width += titleHeight;
         // and some space between label and controls
         dim.width += 3;

         dim.height = getTitleHeight();

         dim.width += label2.getPreferredSize().width;
         dim.width += 3;

         return dim;
      }

      @Override
      protected boolean isMouseOverBorder() {
         return true;
      }

      @Override
      protected void paintChevronControls( JXTaskPane group, Graphics g, int x, int y, int width,
            int height ) {

         HeaderChevronIcon chevron;
         if( group.isExpanded() ){
            chevron = new HeaderChevronIcon( true );
         }else{
            chevron = new HeaderChevronIcon( false );
         }
         int chevronX = x + width / 2 - chevron.getIconWidth() / 2;
         int chevronY = y + (height / 2 - chevron.getIconHeight());
         chevron.paintIcon( group, g, chevronX, chevronY );
         chevron.paintIcon( group, g, chevronX, chevronY + chevron.getIconHeight() + 1 );
      }

      @Override
      protected void paintOvalAroundControls( JXTaskPane jxtaskpane, Graphics g, int i, int j,
            int k, int l ) {
         /*
         if( jxtaskpane.isSpecial() ){
            g.setColor( specialTitleBackground.brighter() );
            g.drawOval( i, j, k, l );
         }else{
            Color gradientStart = new Color( 0, 166, 220 );
            Color gradientEnd = new Color( 0, 77, 152 );
            int offset = 4;
            int extGradientThickness = offset;
            Graphics2D g2 = (Graphics2D) g;
            for( ; offset < 6; offset++ ){
               GradientPaint gradient2 = new GradientPaint( i + offset, j + (l / 2) + offset,
                     gradientStart, i + k - (offset * 2), j + offset, gradientEnd );
               GradientPaint gradient3 = new GradientPaint( i + offset, j + l - (offset * 2),
                     gradientEnd, i + offset, j + (l / 2) + offset, gradientStart );
               g2.setPaint( gradient3 );
               g2.drawOval( i + offset, j + offset, k - (offset * 2), l - (offset * 2) );
               g2.setPaint( gradient2 );
               g2.drawArc( i + (offset * 2), j + offset, k - (offset * 4), (l / 2) - (offset), 0,
                     180 );

            }

            g2.setPaint( Color.BLACK );
            g2.drawOval( i, j, k, l );

            GradientPaint gradient1 = new GradientPaint( i + offset + 1, j + l - (offset * 2) - 1,
                  new Color( 237, 238, 232 ), i + offset + 1, j + offset + 1, new Color( 145, 149,
                        158 ) );
            g2.setPaint( gradient1 );
            g2.fillOval( i + offset + 1, j + offset + 1, k - (offset * 2) - 1, l - (offset * 2) - 1 );
            g2.setPaint( new Color( 207, 208, 202 ) );
            for( int x = 1; x < extGradientThickness; x++ ){
               g2.drawOval( i + x, j + x, k - (x * 2), l - (x * 2) );
            }
         }
         */
      }

      protected void paintExpandedControls( JXTaskPane group, Graphics g, int x, int y, int width,
            int height ) {
         ((Graphics2D) g).setRenderingHint( RenderingHints.KEY_ANTIALIASING,
               RenderingHints.VALUE_ANTIALIAS_ON );

         paintOvalAroundControls( group, g, x, y, width, height );
         g.setColor( getPaintColor( group ) );
         paintChevronControls( group, g, x, y, width, height );

         ((Graphics2D) g).setRenderingHint( RenderingHints.KEY_ANTIALIASING,
               RenderingHints.VALUE_ANTIALIAS_OFF );
      }

      @Override
      protected void paintTitle( JXTaskPane group, Graphics g, Color textColor, int x, int y,
            int width, int height ) {
         configureLabel( group );
         label.setForeground( textColor );
         label2.setFont( group.getFont() );
         label2.setText( ((PostPane) group).getPublishedDateText() );
         label2.setForeground( textColor );

         int width2 = label2.getPreferredSize().width;

         g.translate( x, y );
         label.setBounds( 0, 0, width - 3 - width2, height );
         label.paint( g );
         g.translate( -x, -y );

         g.translate( x + width - width2, y );
         label2.setBounds( 0, 0, width2, height );
         label2.paint( g );
         g.translate( -x - width + width2, -y );
      }

      protected void paintTitleBackground( JXTaskPane group, Graphics g ) {
         if( group.isSpecial() ){
            g.setColor( specialTitleBackground );
            g.fillRoundRect( 0, 0, group.getWidth(), roundHeight * 2, roundHeight, roundHeight );
            g.fillRect( 0, roundHeight, group.getWidth(), titleHeight - roundHeight );
         }else{
            Paint oldPaint = ((Graphics2D) g).getPaint();

            GradientPaint gradient1 = new GradientPaint( 0f, 0f, // group.getWidth()
                  // / 2,
                  titleBackgroundGradientStart, 0f, // group.getWidth(),
                  titleHeight / 2, titleBackgroundGradientEnd );

            GradientPaint gradient2 = new GradientPaint( 0f, titleHeight / 2, // group.getWidth()
                  // / 2,
                  titleBackgroundGradientEnd, 0f, // group.getWidth(),
                  titleHeight, titleBackgroundGradientStart );

            ((Graphics2D) g).setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,
                  RenderingHints.VALUE_COLOR_RENDER_QUALITY );
            ((Graphics2D) g).setRenderingHint( RenderingHints.KEY_INTERPOLATION,
                  RenderingHints.VALUE_INTERPOLATION_BILINEAR );
            ((Graphics2D) g).setRenderingHint( RenderingHints.KEY_RENDERING,
                  RenderingHints.VALUE_RENDER_QUALITY );
            ((Graphics2D) g).setPaint( gradient1 );

            g.fillRoundRect( 0, 0, group.getWidth(), roundHeight * 2, roundHeight, roundHeight );
            // up
            g.fillRect( 0, roundHeight, group.getWidth(), titleHeight / 2 );

            // down
            ((Graphics2D) g).setPaint( gradient2 );
            g.fillRect( 0, (titleHeight / 2), group.getWidth(), titleHeight / 2 );

            ((Graphics2D) g).setPaint( oldPaint );
         }
      }
   }
}