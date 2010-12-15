package org.codehaus.groovy2.compiler.type;

import java.util.HashMap;

import org.codehaus.groovy.ast.ClassNode;

public class TypeScope {
  private final HashMap<ClassNode, Type> map =
      new HashMap<ClassNode, Type>();
  private final HashMap<Class<?>, Type> classMap =
      new HashMap<Class<?>, Type>();
  
  public TypeScope() {
    classMap.put(Object.class, PrimitiveType.ANY);
  }
  
  public Type getType(ClassNode classNode) {
    Type type = map.get(classNode);
    if (type != null) {
      return type;
    }
    
    if (classNode.isPrimaryClassNode()) {
      type = new PrimaryType(classNode);  
    } else {
      type = getType(classNode.getTypeClass());
    }
    map.put(classNode, type);
    return type;
  }

  public Type getType(Class<?> clazz) {
    Type type = classMap.get(clazz);
    if (type != null) {
      return type;
    }
    
    if (clazz.isPrimitive() || clazz == void.class) {
      type = PrimitiveType.valueOf(clazz.getName().toUpperCase());
    } else {
      type = new RuntimeType(clazz);
    }
    classMap.put(clazz, type);
    return type;
  }
}
