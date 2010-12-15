package org.codehaus.groovy2.compiler.ast;

import java.util.HashMap;

import org.codehaus.groovy.ast.Variable;

public class StackAllocator {
  private final StackAllocator allocator;
  private final HashMap<Variable,Integer> registerMap =
      new HashMap<Variable, Integer>();
  private int slotCount;
  
  public StackAllocator(int startSlot) {
    this.allocator = null;
    this.slotCount = startSlot;
  }
  
  StackAllocator(StackAllocator allocator) {
    this.allocator = allocator;
    this.slotCount = allocator.slotCount;
  }
  
  public int getSlot(Variable variable) {
    Integer wrappedSlot = lookup(variable);
    if (wrappedSlot != null) {
      return wrappedSlot;
    }
    int slot = slotCount++;
    registerMap.put(variable, slot);
    return slot;
  }

  private Integer lookup(Variable variable) {
    Integer register = registerMap.get(variable);
    if (register != null)
      return register;
    return (allocator != null)? allocator.lookup(variable): null;
  }
}
