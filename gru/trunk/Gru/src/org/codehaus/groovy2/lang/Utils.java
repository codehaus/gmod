package org.codehaus.groovy2.lang;


class Utils {
  public static Class<?> getPrimitive(Class<?> clazz) {
    if (!clazz.getName().startsWith("java.lang")) {
      return clazz;  // short circuit
    }
    
    if (clazz == Integer.class)
      return int.class;
    if (clazz == Double.class)
      return double.class;
    if (clazz == Boolean.class)
      return boolean.class;
    if (clazz == Long.class)
      return long.class;
    if (clazz == Character.class)
      return char.class;
    if (clazz == Byte.class)
      return byte.class;
    if (clazz == Float.class)
      return float.class;
    if (clazz == Short.class)
      return short.class;
    if (clazz == Void.class)
      return void.class;
    return clazz;
  }
  
  public static Class<?> getWrapper(Class<?> clazz) {
    if (clazz == void.class)  // void is not a primitive
      return Void.class;
    
    if (!clazz.isPrimitive()) {
      return clazz;  // short circuit
    }
    
    if (clazz == int.class)
      return Integer.class;
    if (clazz == double.class)
      return Double.class;
    if (clazz == boolean.class)
      return Boolean.class;
    if (clazz == long.class)
      return Long.class;
    if (clazz == char.class)
      return Character.class;
    if (clazz == byte.class)
      return Byte.class;
    if (clazz == float.class)
      return Float.class;
    if (clazz == short.class)
      return Short.class;
    
    return clazz;
  }

  public static String capitalize(String name) {
    return Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }

  public static String uncapitalize(String name) {
    return Character.toLowerCase(name.charAt(0)) + name.substring(1);
  }
}
