package org.codehaus.groovy2.compiler.type;

import java.util.HashMap;

public enum PrimitiveType implements Type {
  ANY(Object.class, null),
  BOOLEAN(boolean.class, "java/lang/Boolean"),
  BYTE(byte.class,       "java/lang/Byte"),
  SHORT(short.class,     "java/lang/Short"),
  CHAR(char.class,       "java/lang/Character"),
  INT(int.class,         "java/lang/Integer"),
  LONG(long.class,       "java/lang/Long"),
  FLOAT(float.class,     "java/lang/Float"),
  DOUBLE(double.class,   "java/lang/Double"),
  VOID(void.class, null);
  
  private final Class<?> runtimeClass;
  private final String wrapperName;
  private final String unwrappedMethodName;
  
  private PrimitiveType(Class<?> runtimeClass, String wrapperName) {
    this.runtimeClass = runtimeClass;
    this.wrapperName = wrapperName;
    this.unwrappedMethodName = name() + "Value";
  }
  
  public Class<?> getRuntimeClass() {
    return runtimeClass;
  }
  public String getWrapperName() {
    return wrapperName;
  }
  public String getUnwrappedMethodName() {
    return unwrappedMethodName;
  }
  
  @Override
  public String getName() {
    return name();
  }
  
  @Override
  public <R, P> R accept(TypeVisitor<? extends R,? super P> visitor, P param) {
    return visitor.visitPrimitiveType(this, param);
  }
  
  public static PrimitiveType wrapperAsPrimitive(Class<?> clazz) {
    return wrapperToPrimitiveMap.get(clazz.getName());
  }
  
  public static PrimitiveType wrapperAsPrimitive(Type type) {
    if (!(type instanceof RuntimeType)) {
      return null;
    }
    RuntimeType runtimeType = (RuntimeType)type;
    return wrapperAsPrimitive(runtimeType.getType());
  }
  
  private static final HashMap<String, PrimitiveType> wrapperToPrimitiveMap;
  static {
    HashMap<String, PrimitiveType> map = new HashMap<String, PrimitiveType>();
    for(PrimitiveType primitiveType: PrimitiveType.values()) {
      String wrapperName = primitiveType.getWrapperName();
      if (wrapperName == null) {  // any and void have no wrapper
        continue;
      }
      map.put(wrapperName.replace('/', '.'), primitiveType);
    }
    wrapperToPrimitiveMap = map;
  }
}
