package org.codehaus.groovy2.lang;

import java.util.Collection;

import groovy2.lang.MetaClass;
import groovy2.lang.MetaClassMutator;
import groovy2.lang.Method;
import groovy2.lang.Property;

import org.junit.Assert;
import org.junit.Test;

public class MetaClassTest {
  public static class TestAdd {
    public void foo(long l) { }
  }
  
  public static class TestAddMixin {
    public static void bar(TestAdd testAdd, int i) { }
  }
  
  @Test
  public void testObjectMetaClass() {
    MetaClass metaClass = RT.getMetaClass(Object.class);
    
    System.out.println(metaClass.getMethods());
    System.out.println(metaClass.getProperties());
    
    Collection<Property> properties = metaClass.getProperties();
    for(Property property: properties) {
      if (property.getName().equals("metaclass")) {
        return;
      }
    }
    
    Assert.fail("not metaclass property defined on object metaclass");
  }
  
  @Test
  public void testWrapperAndPrimitive() {
    Assert.assertSame(RT.getMetaClass(boolean.class), RT.getMetaClass(Boolean.class));
    Assert.assertSame(RT.getMetaClass(byte.class), RT.getMetaClass(Byte.class));
    Assert.assertSame(RT.getMetaClass(short.class), RT.getMetaClass(Short.class));
    Assert.assertSame(RT.getMetaClass(char.class), RT.getMetaClass(Character.class));
    Assert.assertSame(RT.getMetaClass(int.class), RT.getMetaClass(Integer.class));
    Assert.assertSame(RT.getMetaClass(long.class), RT.getMetaClass(Long.class));
    Assert.assertSame(RT.getMetaClass(float.class), RT.getMetaClass(Float.class));
    Assert.assertSame(RT.getMetaClass(double.class), RT.getMetaClass(Double.class));
    Assert.assertSame(RT.getMetaClass(void.class), RT.getMetaClass(Void.class));
  }
  
  @Test
  public void testAddMixin() {
    MetaClass metaClass = RT.getMetaClass(TestAdd.class);
    metaClass.getMethods(); // initialize
    
    MetaClassMutator mutator = metaClass.mutator();
    try {
      mutator.addMixin(RT.getMetaClass(TestAddMixin.class));
    } finally {
      mutator.close();
    }
    
    for(Method method: metaClass.getMethods()) {
      if (method.getName().equals("bar"))
        return;
    }
    Assert.fail("no method bar in TestAdd (mixin TestAddMixin)");
  }
}
