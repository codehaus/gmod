package groovy2.lang;

import java.util.Arrays;

public final class FunctionType {
  private final MetaClass returnType;
  private final MetaClass[] parameterTypes;
  
  public FunctionType(MetaClass returnType, MetaClass... parameterTypes) {
    this.returnType = returnType;
    this.parameterTypes = parameterTypes;
  }
  
  @Override
  public int hashCode() {
    return Arrays.hashCode(parameterTypes) ^ returnType.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof FunctionType)) {
      return false;
    }
    FunctionType functionType = (FunctionType)obj;
    return returnType == functionType.returnType &&
        Arrays.equals(parameterTypes, functionType.parameterTypes);
  }
  
  @Override
  public String toString() {
    return Arrays.toString(parameterTypes)+':'+returnType;
  }
  
  public MetaClass getReturnType() {
    return returnType;
  }
  public int getParameterCount() {
    return parameterTypes.length;
  }
  public MetaClass getParameterType(int index) {
    return parameterTypes[index];
  }
  
  public FunctionType dropFirstParameter() {
    int length = parameterTypes.length - 1;
    MetaClass[] types = new MetaClass[length];
    for(int i=0; i<length; i++) {
      types[i] = parameterTypes[i + 1];
    }
    return new FunctionType(returnType, types);
  }
}
