package org.codehaus.groovy2.lang;

import groovy2.lang.Closure;
import groovy2.lang.Failures;
import groovy2.lang.FunctionType;
import groovy2.lang.MOPResult;
import groovy2.lang.MetaClass;
import groovy2.lang.Method;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy2.dyn.Switcher;

public class MethodResolver {
  static class MethodEntry {
    final FunctionType methodType;
    final Method target;
    final MethodHandle returnConverter;
    final MethodHandle[] parameterConverters;
    
    MethodEntry(FunctionType methodType, Method target, MethodHandle returnConverter, MethodHandle[] parameterConverters) {
      this.methodType = methodType;
      this.target = target;
      this.returnConverter = returnConverter;
      this.parameterConverters = parameterConverters;
    }
    
    @Override
    public String toString() {
      return target.toString();
    }
    
    MethodHandle asTarget() {
      MethodHandle target = this.target.asMethodHandle();
      
      if (parameterConverters != null) {
        MethodHandle[] filters = new MethodHandle[parameterConverters.length];
        for(int i=0; i<filters.length; i++) {
          filters[i] = parameterConverters[i];
        }
        target = MethodHandles.filterArguments(target, filters);
      }
      
      if (returnConverter != null) {
        throw new AssertionError("NYI"); //FIXME wait filterReturnvalue
      }
      return target;
    }
  }
  
  // this code use the same algorithm as resolve(), should be updated accordingly
  public static List<Method> getMostSpecificMethods(Map<FunctionType, Method> methodMap, FunctionType signature, boolean allowConversions) {
    Method method = methodMap.get(signature);  // short-cut
    if (method != null) {
      return Collections.singletonList(method);
    }
    
    ArrayList<MethodEntry> entries = applicable(methodMap.values(), signature, allowConversions);
    int size = entries.size();
    if (size == 0) {
      return Collections.emptyList();
    }
    if (size == 1) {
      return Collections.singletonList(entries.get(0).target);
    }
    
    MethodEntry mostSpecific = mostSpecific(entries, new boolean[signature.getParameterCount()]);
    if (mostSpecific != null) {
      return Collections.singletonList(mostSpecific.target);
    }
    
    Method[] methods = new Method[size];
    for(int i=0; i<size; i++) {
      methods[i] = entries.get(i).target;
    }
    return Arrays.asList(methods);
  }
  
  // this code use the same algorithm as getMostSpecificMethods(), should be updated accordingly
  public static MOPResult resolve(Map<FunctionType, Method> methodMap, boolean isStatic, FunctionType signature, boolean allowConversions, MethodHandle reset) {
    Method method = methodMap.get(signature);  // short-cut
    if (method != null) {
      if (isStatic && !Modifier.isStatic(method.getModifiers())) {
        return asMOPResult(Failures.fail("most specific method "+method+" is not static "));
      }
      return asMOPResult(method);
    }
    
    //System.out.println("MOP method resolver: values :" + methodMap.values());
    
    ArrayList<MethodEntry> entries = applicable(methodMap.values(), signature, allowConversions);
    
    //System.out.println("MOP method resolver: applicable "+entries+" isStatic "+isStatic+" "+signature);
    
    int size = entries.size();
    if (size == 0) {
      return asMOPResult(Failures.fail("no applicable method among "+entries));
    }
    MethodEntry mostSpecific = entries.get(0);
    if (size == 1) {
      if (isStatic && !Modifier.isStatic(mostSpecific.target.getModifiers())) {
        return asMOPResult(Failures.fail("most specific method "+mostSpecific+" is not static "));
      }
      return asMOPResult(mostSpecific.target);
    }
    
    int count = mostSpecific.methodType.getParameterCount();
    boolean[] guardNeeded = new boolean[count];
    
    mostSpecific = mostSpecific(entries, guardNeeded);
    if (mostSpecific == null) {
      return asMOPResult(Failures.fail("no most specific method among "+entries));
    }
    if (isStatic && !Modifier.isStatic(mostSpecific.target.getModifiers())) {
      return asMOPResult(Failures.fail("most specific method "+mostSpecific+" is not static "));
    }
    
    return asMOPResult(mostSpecific.target);
  }
  
  private static MOPResult asMOPResult(Closure target) {
    return new MOPResult(target, Collections.<Switcher>emptyList());
  }

  public static ArrayList<MethodEntry> applicable(Collection<Method> methods, FunctionType functionType, boolean allowConversions) {
    //System.out.println("method resolver: potentially applicable methods "+methods);
    
    ArrayList<MethodEntry> entries = new ArrayList<MethodEntry>();
    loop: for(Method method: methods) {
      int functionTypeCount = functionType.getParameterCount();
        
      FunctionType methodType = method.getMethodType();
      int methodTypeCount = methodType.getParameterCount();
      if (method.isVarargs()) {
        if (methodTypeCount > functionTypeCount) {
          continue;
        }
        methodType = spread(methodType, functionTypeCount);  
      } else {
        if (methodTypeCount != functionTypeCount) {
          continue;
        }
      }
      
      assert methodType.getParameterCount() == functionTypeCount;
      
      //System.out.println("method resolver: applicable "+method);
      
      boolean isParameterConverted = false;
      MethodHandle[] parameterConverters = new MethodHandle[functionTypeCount];
      for(int i=0; i<functionTypeCount; i++) {
        if (!isAssignable(methodType.getParameterType(i), functionType.getParameterType(i), 
            parameterConverters, i, allowConversions)) {
          
          isParameterConverted |= (parameterConverters[i] != null);
          continue loop;  
        }
      }
      
      MethodHandle[] returnConverters = new MethodHandle[1];
      /* FIXME: should we use the return type ? // also don't forget void case
      if (!isAssignable(functionType.getReturnType(), methodType.getReturnType(),
          returnConverters, 0,
          allowConversions)) {
        continue loop;
      }*/
      
      entries.add(new MethodEntry(methodType, method,
          returnConverters[0],
          (isParameterConverted)? parameterConverters: null));
    }
    return entries;
  }
  
  

  private static FunctionType spread(FunctionType methodType, int count) {
    throw new AssertionError("NYI: varargs spreading no implemented");
  }

  private static boolean isAssignable(MetaClass metaClass1, MetaClass metaClass2, MethodHandle[] converters, int index, boolean allowConversions) {
    //System.out.println("isAssignable "+metaClass1+" "+metaClass2);
    
    if (isSuperType(metaClass1, metaClass2)) {
      
      /*
      // check if we need converter to implement subtyping relation
      if (needSubTypeConverter(metaClass1, metaClass2)) {
        Closure converter = metaClass1.mopConverter(metaClass2);
        if (!Failures.isFailure(converter)) {
          converters[index] = converter.asMethodHandle();
          return true;
        }
      } else // no subtype converter needed
      {
        return true;
      }*/
      return true;
    }
    
    if (!allowConversions)
      return false;
    
    
    return false; //FIXME
    /*
    Closure converter = metaClass1.mopConverter(metaClass2);
    if (Failures.isFailure(converter)) {
      return false;
    }
    converters[index] = converter.asMethodHandle();
    return true;
    */
  }
  
  private static boolean needSubTypeConverter(MetaClass metaClass1, MetaClass metaClass2) {
    Class<?> clazz1 = RT.getRawClass(metaClass1);
    Class<?> clazz2 = RT.getRawClass(metaClass2);
    
    if (clazz1.isPrimitive() && (clazz2.isPrimitive() || Utils.getPrimitive(clazz2).isPrimitive())) {
        return false;
    }
    if (Utils.getPrimitive(clazz1).isPrimitive() && clazz2.isPrimitive()) {
      return false;
    }
    if (clazz1.isAssignableFrom(clazz2)) {
      return false;
    }
    return true;
  }

  public static MethodEntry mostSpecific(ArrayList<MethodEntry> entries, /*out*/ boolean[] guardNeeded) {
    int count = guardNeeded.length;
    MethodEntry mostSpecific = entries.get(0);
    
    int size = entries.size();
    for(int i=1; i<size; i++) {
      MethodEntry entry = entries.get(i);
      
      FunctionType mostSpecificType = mostSpecific.methodType;
      FunctionType entryType = entry.methodType;
      
      int bias = 0;  // 0: no idea, 1:mostSpecific subtype, -1: entry subtype 
      for(int p=0; p<count; p++) {
        MetaClass mostSpecificParameterType = mostSpecificType.getParameterType(p);
        MetaClass entryParameterType = entryType.getParameterType(p);
        if (mostSpecificParameterType != entryParameterType) {
          guardNeeded[p] = true;
          
          switch(bias) {
          case 1:
            if (!isSuperType(entryParameterType, mostSpecificParameterType)) {
              return null;
            }
            continue;
          case -1:
            if (!isSuperType(mostSpecificParameterType, entryParameterType)) {
              return null;
            }
            continue;
          case 0:
            if (isSuperType(entryParameterType, mostSpecificParameterType)) {
              bias = 1;
              continue;
            }
            if (isSuperType(mostSpecificParameterType, entryParameterType)) {
              bias = -1;
              continue;
            }
            return null;
          default:
            throw new AssertionError();
          }
        }
      }
      
      assert bias != 0;
      
      if (bias == -1) {
        mostSpecific = entry;
        continue;
      }
      
      /*
      MetaClass mostSpecificReturnType = mostSpecificType.getReturnType();
      MetaClass entryReturnType = entryType.getReturnType();
      if (mostSpecificReturnType == entryReturnType) {
        if (bias == -1) {
          mostSpecific = entry;
          continue;
        }
        continue;
      }
      
      switch(bias) {
      case 1:
        if(!isSuperType(mostSpecificReturnType, entryReturnType)) {
          return null;
        }
        continue;
      case -1:
        if (!isSuperType(entryReturnType, mostSpecificReturnType)) {
          return null;
        }
        mostSpecific = entry;
        continue;
        
      case 0:
        if (isSuperType(mostSpecificReturnType, entryReturnType)) {
          continue;
        }
        if (isSuperType(entryReturnType, mostSpecificReturnType)) {
          mostSpecific = entry;
          continue;
        }
        return null;
      default:
        throw new AssertionError();
      }*/
    }
    
    return mostSpecific;
  }

  private static boolean isSuperType(MetaClass metaClass1, MetaClass metaClass2) {
    if (metaClass1 == metaClass2) {
      return true;
    }
    
    //System.out.println("isSuperType: supertypes("+metaClass2+") "+metaClass2.getSuperTypes());
    
    //FIXME: don't works with parameterized types
    for(MetaClass superType: metaClass2.getSuperTypes()) {
      if (isSuperType(metaClass1, superType)) {
        return true;
      }
    }
    return false;
  }
}
