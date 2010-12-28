import groovy2.lang.Array;
import groovy2.lang.MetaClass;

import org.codehaus.groovy2.lang.RT;


public class ArrayTest {
  public static void main(String[] args) {
    /*System.out.println(String[].class.getSuperclass());
    
    MetaClass metaClass = RT.getMetaClass(String[].class);
    System.out.println(metaClass.getSuperTypes());
    
    MetaClass metaClass2 = RT.getMetaClass(Object[].class);
    System.out.println(metaClass2.getSuperTypes());
    
    MetaClass metaClass3 = RT.getMetaClass(int[].class);
    System.out.println(metaClass3.getSuperTypes());
    System.out.println(metaClass3.getMethods());
    */
   
    MetaClass metaClass4 = RT.getMetaClass(Array.class);
    System.out.println(metaClass4.getMethods());
  }
}
