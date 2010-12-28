package org.codehaus.groovy2.compiler.type;

import java.lang.reflect.Array;

import org.codehaus.groovy.ast.ClassNode;

import static org.codehaus.groovy2.compiler.type.PrimitiveType.*;

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
      return asRuntimeArray(type.getType(), typeScope);
    }

    @Override
    public Type visitPrimitiveType(PrimitiveType type, TypeScope typeScope) {
      switch(type) {
      case ANY:
        return ANY;
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

  public static Type getComponent(Type type, TypeScope typeScope) {
    return type.accept(GET_COMPONENT_VISITOR, typeScope);
  } // where
  private static final TypeVisitor<Type, TypeScope> GET_COMPONENT_VISITOR =
      new TypeVisitor<Type, TypeScope>() {
    
    @Override
    public Type visitRuntimeType(RuntimeType type, TypeScope typeScope) {
      Class<?> clazz = type.getType();
      if (clazz.isArray()) {
        return typeScope.getType(clazz.getComponentType());
      }
      throw new AssertionError("no component type of "+type);
    }

    @Override
    public Type visitPrimitiveType(PrimitiveType type, TypeScope typeScope) {
      if (type == ANY) {
        return PrimitiveType.ANY;
      }
      throw new AssertionError("no component type of "+type);
    }

    @Override
    public Type visitPrimaryType(PrimaryType type, TypeScope typeScope) {
      ClassNode node = type.getPrimaryNode();
      if (node.isArray()) {
        return typeScope.getType(node.getComponentType());
      }
      throw new AssertionError("no component type of "+type);
    }
  };
}
