package org.codehaus.groovy2.compiler.type;

public interface TypeVisitor<R, P> {
  public R visitPrimitiveType(PrimitiveType type, P param);
  public R visitPrimaryType(PrimaryType type, P param);
  public R visitRuntimeType(RuntimeType type, P param);
}
