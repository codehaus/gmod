package org.codehaus.groovy2.lang;

import groovy2.lang.Attribute;
import groovy2.lang.Closure;
import groovy2.lang.FunctionType;
import groovy2.lang.MetaClass;
import groovy2.lang.MetaClassMutator;
import groovy2.lang.Method;
import groovy2.lang.Property;
import groovy2.lang.mop.MOPDoCallEvent;
import groovy2.lang.mop.MOPNewInstanceEvent;
import groovy2.lang.mop.MOPPropertyEvent;
import groovy2.lang.mop.MOPInvokeEvent;
import groovy2.lang.mop.MOPOperatorEvent;
import groovy2.lang.mop.MOPResult;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.MethodType;
import java.dyn.NoAccessException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.codehaus.groovy2.dyn.Switcher;
import org.codehaus.groovy2.lang.java.JVMAttribute;
import org.codehaus.groovy2.lang.java.JVMMethod;
import org.codehaus.groovy2.lang.java.JVMProperty;

public class ExpandoMetaClass implements MetaClass {
  final ReentrantLock lock =
      new ReentrantLock();
  
  private Switcher switcher;
  private boolean sealed;
  
  private final Class<?> type;
  final CopyOnWriteArrayList<MetaClass> superTypes;
  final CopyOnWriteArrayList<MetaClass> mixins =
      new CopyOnWriteArrayList<MetaClass>();
  
  final LinkedList<WeakReference<MetaClass>> subTypes =
      new LinkedList<WeakReference<MetaClass>>();
  
  private HashMap<String, Attribute> attributeMap;                   // lazy populated
  private HashMap<String, Property> propertyMap;                     // lazy populated
  private HashMap<FunctionType, Method> constructorMap;              // lazy populated
  private List<Method> methods;                                      // lazy populated
  private HashMap<String, HashMap<FunctionType, Method>> methodMap;  // lazy populated
  
  public ExpandoMetaClass(Class<?> type) {
    this.type = type;
    
    this.switcher = new Switcher();
    
    // init hierarchy
    ArrayList<MetaClass> superTypes = new ArrayList<MetaClass>();
    for(Class<?> interfaze: type.getInterfaces()) {
      superTypes.add(RT.getMetaClass(interfaze));
    }
    Class<?> superclass = (type.isInterface())? Object.class: type.getSuperclass();
    if (superclass != null) {
      superTypes.add(RT.getMetaClass(superclass));
    }
    this.superTypes = new CopyOnWriteArrayList<MetaClass>(superTypes);
  }
  
  @Override
  public Class<?> getType() {
    return type;
  }
  @Override
  public List<MetaClass> getSuperTypes() {
    return superTypes;
  }
  @Override
  public List<MetaClass> getMixins() {
    return mixins;
  }
  
  
  @Override
  public String toString() {
    return type.getName().toString();
  }
  
  
  @Override
  public MetaClass getRawMetaClass() {
    return this;
  }
  @Override
  public List<MetaClass> getTypeArguments() {
    return Collections.emptyList();
  }
  
  
  // -- MOP ---------------------------------------------
  
  private MethodHandle switcherGuard(MethodHandle target, MethodHandle fallback) {
    assert lock.isHeldByCurrentThread();
    
    if (sealed) { // metaclass can't change, guard not needed
      return target;
    }
    target = MethodHandles.convertArguments(target, fallback.type());
    return switcher.guard(target, fallback);
  }
  
  @Override
  public MOPResult mopInvoke(MOPInvokeEvent mopEvent) { 
    if (!lock.isHeldByCurrentThread())
      throw new IllegalStateException("MOP method can only be called under the mutation lock");

    getMethods(); // lazy initialize
    String name = mopEvent.getName();
    HashMap<FunctionType, Method> map = methodMap.get(name);
    if (map == null) {
      return new MOPResult(new LinkageError("no method "+name+" defined for metaclass "+this));
    }

    FunctionType signature = mopEvent.getSignature().dropFirstParameter();

    MOPResult result = MethodResolver.resolve(map, mopEvent.isStatic(), signature, false, mopEvent.getFallback());
    if (result.getFailure() != null)
      return result;
    return new MOPResult(switcherGuard(result.getTarget(), mopEvent.getReset()));
  }
  
  @Override
  public MOPResult mopNewInstance(MOPNewInstanceEvent mopEvent) {
    if (!lock.isHeldByCurrentThread())
      throw new IllegalStateException("MOP method can only be called under the mutation lock");

    getContructors(); // lazy initialize

    FunctionType signature = mopEvent.getSignature().dropFirstParameter();

    MethodHandle reset = mopEvent.getReset();
    MOPResult result = MethodResolver.resolve(constructorMap, true, signature, false, reset);
    if (result.getFailure() != null)
      return result;
    return new MOPResult(switcherGuard(result.getTarget(), reset));
  }
  
  @Override
  public MOPResult mopOperator(MOPOperatorEvent mopEvent) {
    if (!lock.isHeldByCurrentThread())
      throw new IllegalStateException("MOP method can only be called under the mutation lock");

    getMethods(); // lazy initialize
    String name = mopEvent.getName();
    HashMap<FunctionType, Method> map = methodMap.get(name);
    if (map == null) {
      return new MOPResult(new LinkageError("no method "+name+" defined for metaclass "+this));
    }

    FunctionType signature = mopEvent.getSignature().dropFirstParameter();

    MethodHandle reset = mopEvent.getReset();
    MOPResult result = MethodResolver.resolve(map, false, signature, false, reset);
    if (result.getFailure() != null)
      return result;
    return new MOPResult(switcherGuard(result.getTarget(), reset));
  }
  
  @Override
  public MOPResult mopDoCall(MOPDoCallEvent mopEvent) {
    if (!lock.isHeldByCurrentThread())
      throw new IllegalStateException("MOP method can only be called under the mutation lock");

    if (type.isAssignableFrom(Closure.class)) {
      MOPResult result = mopInvoke(new MOPInvokeEvent(mopEvent.getCallerClass(),
          mopEvent.getFallback(), mopEvent.getReset(), false, "asMethodHandle",
          new FunctionType(RT.getMetaClass(MethodHandle.class), this)));
      if (result.getFailure() != null) {
        return result;
      }
      MethodType type = RT.asDynMethodType(mopEvent.getSignature());
      MethodHandle combiner = result.getTarget();
      combiner = MethodHandles.dropArguments(combiner, 0, type.parameterArray());
      MethodHandle target = MethodHandles.foldArguments(MethodHandles.exactInvoker(type), combiner);
      return new MOPResult(target);
    }

    return mopInvoke(new MOPInvokeEvent(mopEvent.getCallerClass(),
        mopEvent.getFallback(), mopEvent.getReset(), false, "doCall",
        mopEvent.getSignature()));
  }
  
  
  
  @Override
  public MOPResult mopGetProperty(MOPPropertyEvent mopEvent) {
    if (!lock.isHeldByCurrentThread())
      throw new IllegalStateException("MOP method can only be called under the mutation lock");

    String name = mopEvent.getName();
    Property property = findProperty(name);
    Closure getter;
    if (property != null && (getter=property.getGetter()) != null) {
      if (mopEvent.isStatic() && !Modifier.isStatic(property.getModifiers())) {
        return new MOPResult(new LinkageError("property "+property+" is not static "));
      }
      return new MOPResult(switcherGuard(getter.asMethodHandle(), mopEvent.getReset()));
    }

    Attribute attribute = findAttribute(name);
    if (attribute != null && (getter=attribute.getGetter()) != null) {
      if (mopEvent.isStatic() && !Modifier.isStatic(attribute.getModifiers())) {
        return new MOPResult(new LinkageError("attribute "+attribute+" is not static "));
      }
      return new MOPResult(switcherGuard(getter.asMethodHandle(), mopEvent.getReset()));
    }

    return new MOPResult(new LinkageError("no property "+name+" defined for metaclass "+this));
  }
  
  @Override
  public MOPResult mopSetProperty(MOPPropertyEvent mopEvent) {
    if (!lock.isHeldByCurrentThread())
      throw new IllegalStateException("MOP method can only be called under the mutation lock");

    String name = mopEvent.getName();
    Property property = findProperty(name);
    Closure setter;
    if (property != null && (setter = property.getSetter()) != null) {
      if (mopEvent.isStatic() && !Modifier.isStatic(property.getModifiers())) {
        return new MOPResult(new LinkageError("property "+property+" is not static "));
      }
      return new MOPResult(switcherGuard(setter.asMethodHandle(), mopEvent.getReset()));
    }

    Attribute attribute = findAttribute(name);
    if (attribute != null && (setter = attribute.getSetter()) != null) {
      if (mopEvent.isStatic() && !Modifier.isStatic(attribute.getModifiers())) {
        return new MOPResult(new LinkageError("attribute "+attribute+" is not static "));
      }
      return new MOPResult(switcherGuard(attribute.getSetter().asMethodHandle(), mopEvent.getReset()));
    }

    return new MOPResult(new LinkageError("no property "+name+" defined for metaclass "+this));
    
  }
  
  @Override
  public MOPResult mopConverter(MetaClass metaClass) {
    if (!lock.isHeldByCurrentThread())
      throw new IllegalStateException("MOP method can only be called under the mutation lock");

    Collection<Method> constructors = metaClass.findConstructors(type);
    if (constructors.size() != 1) {
      return new MOPResult(new LinkageError("no converter for "+this+" to "+metaClass));
    }
    return new MOPResult(constructors.iterator().next().asMethodHandle());
  }
  
  // -- Mutation -----------------------------------------
  
  //TODO sealed can be volatile
  
  public boolean isSealed() {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return sealed;
    } finally {
      lock.unlock();
    }
  }
  
  
  //FIXME: Possible deadlock between seal() and invalidateAll()
  //       seal takes the lock from subtypes to supertype and
  //       invalidate take them in the reverse order
  /*public void seal() {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      if (sealed) {
        return;
      }
      
      flushLocalCache();
      
      // seal all super types
      for(MetaClass superType: superTypes) {
        if (!(superType instanceof ExpandoMetaClass)) {
          throw new IllegalStateException("super type "+superType+" is not an expando metaclass");
        }
        ((ExpandoMetaClass)superType).seal();
      }
    } finally {
      lock.unlock();
    }
  }*/
  
  private void flushLocalCache() {
    assert lock.isHeldByCurrentThread();
    
    attributeMap = null;
    propertyMap = null;
    methods = null;
    methodMap = null;
    constructorMap = null;
  }
  
  void invalidateAll() {
    assert lock.isHeldByCurrentThread();
    
    //System.out.println("invalidateAll "+type);
    
    flushLocalCache();
    
    // all future callsite paths will be protected with a new Switcher
    Switcher.invalidate(switcher);
    switcher = new Switcher();
    
    for(Iterator<WeakReference<MetaClass>> it = subTypes.iterator(); it.hasNext();) {
      WeakReference<MetaClass> reference = it.next();
      MetaClass metaClass = reference.get();
      if (metaClass == null) {
        it.remove();  // cleaning
        continue;
      }
      
      //FIXME remove cast
      ExpandoMetaClass expandoMetaClass = (ExpandoMetaClass)metaClass;
      ReentrantLock lock = expandoMetaClass.lock;
      lock.lock();
      try {
        expandoMetaClass.invalidateAll();  
      } finally {
        lock.unlock();
      }
    }
  }
  
  //this method is final because it is called in the constructor
  @Override
  public final Mutator mutator() {
    if (sealed) {
      throw new IllegalStateException("metaclass is sealed");
    }
    return new Mutator();
  }
  
  public class Mutator implements MetaClassMutator {
    private boolean mutation;
    Mutator() {
      lock.lock();
    }
    
    @Override
    public void close() {
      try {
        if (mutation) {
          invalidateAll();
        }
      } finally {
        lock.unlock();
      }
    }
    
    // this method is in fact a module private method
    public void addSuperType(MetaClass superType) {
      mutation = true;
      superTypes.add(superType);
      
      //FIXME
      ((ExpandoMetaClass)superType).subTypes.add(new WeakReference<MetaClass>(ExpandoMetaClass.this));
    }

    @Override
    public void addMixin(MetaClass mixin) {
      mutation = true;
      mixins.add(mixin);
    }
    @Override
    public void removeMixin(MetaClass mixin) {
      mutation = true;
      mixins.remove(mixin);
    }

    @Override
    public Attribute addAttribute(int modifiers, String name, Type type) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Property addProperty(int modifiers, String name, Type type) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Method addMethod(int modifiers, String name, Closure closure) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Method addConstructor(int modifiers, Closure closure) {
      throw new UnsupportedOperationException();
    }
    
    @Override
    public void removeAttribute(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removeProperty(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removeMethod(String name, Type... types) {
      throw new UnsupportedOperationException();
    }
    
    @Override
    public void removeConstructor(Type... types) {
      throw new UnsupportedOperationException();
    }
  }
  
  // -- Reflection ---------------------------------------
  
  // TODO: find* can be implemented using CAS
  
  @Override
  public Attribute findAttribute(String name) {
    lock.lock();
    try {
      getAttributes();  // lazy init
      return attributeMap.get(name);
    } finally {
      lock.unlock();
    }
  }
  
  @Override
  public Property findProperty(String name) {
    lock.lock();
    try {
      getProperties();  // lazy init
      return propertyMap.get(name);
    } finally {
      lock.unlock();
    }
  }
  
  @Override
  public Collection<Method> findConstructors(Type... compatibleTypes) {
    FunctionType functionType = new FunctionType(this, RT.getMetaClasses(compatibleTypes));
    HashMap<FunctionType, Method> constructorMap;
    lock.lock();
    try {  
      getContructors();  // lazy init
      constructorMap = this.constructorMap;
    } finally {
      lock.unlock();
    }
    return MethodResolver.getMostSpecificMethods(constructorMap, functionType, false);
  }
  
  @Override
  public Collection<Method> findMethods(String name, Type... compatibleTypes) {
    HashMap<FunctionType, Method> map;
    lock.lock();
    try {  
      getMethods();  // lazy init
      map = methodMap.get(name);
    } finally {
      lock.unlock();
    }
    FunctionType functionType = new FunctionType(RT.getMetaClass(Object.class), RT.getMetaClasses(compatibleTypes));
    return MethodResolver.getMostSpecificMethods(map, functionType, false);
  }
  
  
  
  @Override
  public Collection<Attribute> getAttributes() {
    lock.lock();
    try {
      HashMap<String, Attribute> attributeMap = this.attributeMap;
      if (attributeMap == null) {
        attributeMap = new HashMap<String, Attribute>();
        populateAttributeMap(attributeMap);
        this.attributeMap = attributeMap;
      }
      return Collections.unmodifiableCollection(attributeMap.values());
    } finally {
      lock.unlock();
    }
  }
  
  private void populateAttributeMap(HashMap<String, Attribute> attributeMap) {
    for(MetaClass superType: getSuperTypes()) {
      if (superType instanceof ExpandoMetaClass) {
        ((ExpandoMetaClass)superType).populateAttributeMap(attributeMap);  
      } else {
        for(Attribute attribute: superType.getAttributes()) {
          attributeMap.put(attribute.getName(), attribute);
        }
      }
    }
    
    for(Field field: type.getDeclaredFields()) {
      /*if (!Modifier.isPublic(field.getModifiers())) {
        continue;
      }*/
      field.setAccessible(true);
      
      Attribute attribute = new JVMAttribute(this, field);
      attributeMap.put(attribute.getName(), attribute);
    }
  }

  @Override
  public Collection<Property> getProperties() {
    lock.lock();
    try {
      HashMap<String, Property> propertyMap = this.propertyMap;
      if (propertyMap == null) {
        propertyMap = new HashMap<String, Property>();
        populatePropertyMap(propertyMap, getMethods());
        this.propertyMap = propertyMap;
      }
      return Collections.unmodifiableCollection(propertyMap.values());
    } finally {
      lock.unlock();
    }
  }
  
  static class AccessorsEntry {
    int modifiers;
    Closure getter;
    Closure setter;
  }
  
  private static AccessorsEntry accessorEntry(HashMap<String, AccessorsEntry> accessorMap, String name) {
    AccessorsEntry entry = accessorMap.get(name);
    if (entry == null) {
      entry = new AccessorsEntry();
      accessorMap.put(name, entry);
    }
    return entry;
  }
  
  private void populatePropertyMap(HashMap<String, Property> propertyMap, Collection<Method> methods) {
    for(MetaClass superType: getSuperTypes()) {
      if (superType instanceof ExpandoMetaClass) {
        ExpandoMetaClass superTypeAsRaw = (ExpandoMetaClass)superType;
        superTypeAsRaw.populatePropertyMap(propertyMap, superTypeAsRaw.getMethods());  
      } else {
        for(Property property: superType.getProperties()) {
          propertyMap.put(property.getName(), property);
        }
      }
    }
    
    HashMap<String, AccessorsEntry> accessorMap =
        new HashMap<String, AccessorsEntry>();
    for(Method method: methods) {
      String name = method.getName();
      if (name.length() > 3 && name.startsWith("get") &&  method.getParameterCount() == 1) {
        AccessorsEntry entry = accessorEntry(accessorMap, Utils.uncapitalize(name.substring(3)));
        entry.modifiers = method.getModifiers();
        entry.getter = method;
      }
      if (name.length() > 2 && name.startsWith("is") && name.length() > 3 && method.getParameterCount() == 1) {
        AccessorsEntry entry = accessorEntry(accessorMap, Utils.uncapitalize(name.substring(2)));
        entry.modifiers = method.getModifiers();
        entry.getter = method;
      }
      if (name.length() > 3 && name.startsWith("set") && method.getParameterCount() == 2) {
        AccessorsEntry entry = accessorEntry(accessorMap, Utils.uncapitalize(name.substring(3)));
        entry.modifiers = method.getModifiers();
        entry.setter = method;
      }
    }
    
    for(Map.Entry<String, AccessorsEntry> entry: accessorMap.entrySet()) {
      AccessorsEntry accessorsEntry = entry.getValue();
      Closure getter = accessorsEntry.getter;
      Closure setter = accessorsEntry.setter;
      
      MetaClass type;
      if (getter == null) {
        if (setter == null) {
          throw new AssertionError();
        }
        type = setter.getFunctionType().getParameterType(1);
      } else {
        type = getter.getFunctionType().getReturnType();
      }
      
      String name = entry.getKey();
      Property property = new JVMProperty(this, accessorsEntry.modifiers, name, type, getter, setter); 
      propertyMap.put(name, property);
    }
  }
  
  
  @Override
  public Collection<Method> getMethods() {
    lock.lock();
    try {
      List<Method> methods = this.methods;
      if (methods == null) {
        HashMap<String, HashMap<FunctionType, Method>> methodMap = new HashMap<String, HashMap<FunctionType, Method>>();
        populateMethodMap(methodMap);
        this.methodMap = methodMap;

        methods = new ArrayList<Method>();
        for(HashMap<FunctionType, Method> map: methodMap.values()) {
          methods.addAll(map.values());
        }
        this.methods = Collections.unmodifiableList(methods);
      }
      return methods;
    } finally {
      lock.unlock();
    }
  }

  private void populateMethodMap(HashMap<String, HashMap<FunctionType, Method>> methodMap) {
    for(MetaClass superType: getSuperTypes()) {
      if (superType instanceof ExpandoMetaClass) {
        ((ExpandoMetaClass)superType).populateMethodMap(methodMap);  
      } else {
        for(Method method: superType.getMethods()) {
          String name = method.getName();
          HashMap<FunctionType, Method> map = methodMap.get(name);
          if (map == null) {
            map = new HashMap<FunctionType, Method>();
            methodMap.put(name, map);
          }
          map.put(method.getMethodType(), method);
        }
      }
    }
    
    // add mixins
    for(MetaClass mixin: getMixins()) {
      //TODO: Revisit: there is no inheritance of mixins ??
      for(java.lang.reflect.Method method: RT.getRawClass(mixin).getDeclaredMethods()) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
          continue;
        }
        
        String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length < 1 || parameterTypes[0] != type) {
          continue;  // this will also skip init(MetaClass)
        }
        
        modifiers = modifiers & (~Modifier.STATIC);
        Method metaMethod = new JVMMethod(this, modifiers, name, unreflect(method)); 
        HashMap<FunctionType, Method> map = methodMap.get(name);
        if (map == null) {
          map = new HashMap<FunctionType, Method>();
          methodMap.put(name, map);
        }
        map.put(metaMethod.getMethodType(), metaMethod);
      }
    }
    
    for(java.lang.reflect.Method method: type.getDeclaredMethods()) {
      int modifiers = method.getModifiers();
      if (!Modifier.isPublic(modifiers)) {
        continue;
      }
      
      String name = method.getName();
      MethodHandle mh = unreflect(method);
      if (Modifier.isStatic(modifiers)) {  // a static method is a class method
        mh = MethodHandles.dropArguments(mh, 0, Class.class);
      }
      Method metaMethod = new JVMMethod(this, modifiers, name, mh);
      
      HashMap<FunctionType, Method> map = methodMap.get(name);
      if (map == null) {
        map = new HashMap<FunctionType, Method>();
        methodMap.put(name, map);
      }
      map.put(metaMethod.getMethodType(), metaMethod);
    }
  }
  
  private static MethodHandle unreflect(java.lang.reflect.Method method) {
    try {
      return MethodHandles.publicLookup().unreflect(method);
    } catch (NoAccessException e) {
      throw (AssertionError)new AssertionError().initCause(e);
    }
  }
  
  @Override
  public Collection<Method> getContructors() {
    HashMap<FunctionType, Method> constructorMap;
    lock.lock();
    try {
      constructorMap = this.constructorMap;
      if (constructorMap == null) {
        constructorMap = new HashMap<FunctionType, Method>();
        populateConstructorMap(constructorMap);
        this.constructorMap = constructorMap;
      }
    } finally {
      lock.unlock();
    }
    return Collections.unmodifiableCollection(constructorMap.values());
  }

  private void populateConstructorMap(HashMap<FunctionType, Method> constructorMap) {
    for(Constructor<?> constructor: type.getConstructors()) {
      int modifiers = constructor.getModifiers();
      if (!Modifier.isPublic(modifiers)) {
        continue;
      }
      
      MethodHandle mh = unreflect(constructor);
      
      // see it as a class method
      modifiers = modifiers | Modifier.STATIC;
      mh = MethodHandles.dropArguments(mh, 0, Class.class);
      
      Method metaMethod = new JVMMethod(this, modifiers, "<init>", mh);
      constructorMap.put(metaMethod.getMethodType(), metaMethod);
    }
  }
  
  private static MethodHandle unreflect(Constructor<?> constructor) {
    try {
      return MethodHandles.publicLookup().unreflectConstructor(constructor);
    } catch (NoAccessException e) {
      throw (AssertionError)new AssertionError().initCause(e);
    }
  }
}
