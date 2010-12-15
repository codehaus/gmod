package org.codehaus.groovy2.compiler.ast;

import org.codehaus.groovy.ast.ClassNode;
import org.objectweb.asm.MethodVisitor;


public class GenEnv {
  final MethodVisitor mv;
  private final ClassNode classNode; 
  private final StackAllocator allocator;

  public GenEnv(MethodVisitor mv, ClassNode classNode, StackAllocator allocator) {
    this.mv = mv;
    this.classNode = classNode;
    this.allocator = allocator;
  }
  
  public ClassNode getClassNode() {
    return classNode;
  }
  
  public StackAllocator getAllocator() {
    return allocator;
  }
  
  public GenEnv newAllocator() {
    return new GenEnv(mv, classNode, new StackAllocator(allocator));
  }
}
