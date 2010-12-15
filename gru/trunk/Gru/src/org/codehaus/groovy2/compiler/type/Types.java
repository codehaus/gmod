package org.codehaus.groovy2.compiler.type;

import java.lang.reflect.Array;


public class Types {
  public static Type arrayOf(Type type, TypeScope typeScope) {
    return type.accept(ARRAY_VISITOR, typeScope);
  } // where
  private static final TypeVisitor<Type, TypeScope> ARRAY_VISITOR =
      new TypeVisitor<Type, TypeScope>() {
        private Type asRuntimeArray(Class<?> clazz, TypeScope typeScope) {
          return typeScope.getType(Array.newInstance(clazz, 0).getClass());
        }
        
        @Override
        public Type visitRuntimeType(RuntimeType type, TypeScope typeScope) {
          return asRuntimeArray(type.getClass(), typeScope);
        }
        
        @Override
        public Type visitPrimitiveType(PrimitiveType type, TypeScope typeScope) {
          switch(type) {
          case ANY:
            return PrimitiveType.ANY;
          case VOID:
            throw new AssertionError("void[] ??");
          default:
            return asRuntimeArray(type.getRuntimeClass(), typeScope);
          }
        }
        
        @Override
        public Type visitPrimaryType(PrimaryType type, TypeScope typeScope) {
          return typeScope.getType(type.getPrimaryNode().makeArray());
        }
      };
}