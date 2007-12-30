package groovy.swing.j2d.svg

import org.xml.sax.*
import org.xml.sax.helpers.DefaultHandler

public abstract class GfxSAXHandler extends DefaultHandler {
   private LinkedList nodes = []

   public void startElement( String namespace, String localname, String qname, Attributes attrs ) throws SAXException {
      def nodeName = localname.length() ? localname : qname
      handleNodeStart( nodeName, attrs )
      nodes.addFirst( nodeName )
   }

   public void endElement( String namespace, String localname, String qname )  throws SAXException {
      def node = nodes.removeFirst()
      def nodeName = localname.length() ? localname : qname
      handleNodeEnd( nodeName )
   }

   public void characters( char[] ch, int start, int length ) throws SAXException {
      handleText( new String(ch[start..(start+length-1)] as char[]) )
   }

   protected abstract void handleNodeStart( String name, Attributes attrs )
   protected abstract void handleNodeEnd( String name )
   protected abstract void handleText( String text )
}