package org.codehaus.groovy2.lang;

import groovy2.lang.Attribute;
import groovy2.lang.Closure;
import groovy2.lang.Failures;
import groovy2.lang.FunctionType;
import groovy2.lang.MetaClass;
import groovy2.lang.MetaClassMutator;
import groovy2.lang.Method;
import groovy2.lang.Property;
import groovy2.lang.mop.MOPConverterEvent;
import groovy2.lang.mop.MOPInvokeEvent;
import groovy2.lang.mop.MOPNewInstanceEvent;
import groovy2.lang.mop.MOPOperatorEvent;
import groovy2.lang.mop.MOPPropertyEvent;
import groovy2.lang.mop.MOPResult;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.codehaus.groovy2.dyn.Switcher;
import org.codehaus.groovy2.lang.mop.InstanceProperty;
import org.codehaus.groovy2.lang.mop.ReflectAttribute;
import org.codehaus.groovy2.lang.mop.ReflectClosure;
import org.codehaus.groovy2.lang.mop.ReflectMethod;
import org.codehaus.groovy2.lang.mop.ReflectProperty;

public class ClassMetaClass implements MetaClass {
  final ReentrantLock lock =
      new ReentrantLock();

  Switcher switcher;
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

  ArrayList<Method> addedMethods;                                    // lazy populated
  ArrayList<Property> addedProperties;                               // lazy populated
  
  public ClassMetaClass(Class<?> type) {
    this.type = type;

    this.switcher = new Switcher();

    // init hierarchy
    ArrayList<MetaClass> superTypes = new ArrayList<MetaClass>();
    for(Class<?> interfaze: type.getInterfaces()) {
      superTypes.add(RT.getMetaClass(interfaze));
    }

    Class<?> superclass = getSuperclass(type);
    if (superclass != null) {
      superTypes.add(RT.getMetaClass(superclass));
    }

    this.superTypes = new CopyOnWriteArrayList<MetaClass>(superTypes);
  }

  private static Class<?> getSuperclass(Class<?> type) {
    if (type.isInterface()) {
      return Object.class;
    }
    if (type.isArray()) {
      Class<?> componentType = type.getComponentType();
      if (type != Object[].class && !componentType.isPrimitive()) {
        return Utils.asArray(getSuperclass(componentType));
      }
    }
    return type.getSuperclass();
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

  private static MOPResult asMOPResult(Switcher switcher, Closure target) {
    /*
    if (sealed) { // metaclass can't change, guard not needed
      return target;
    }*/

    return new MOPResult(target, switcher);
  }

  private static MOPResult asMOPResult(Switcher switcher, MOPResult mopResult) {
    /*
    if (sealed) { // metaclass can't change, guard not needed
      return target;
    }*/

    if (mopResult.getConditions().isEmpty()) {
      return new MOPResult(mopResult.getTarget(), switcher);
    }

    HashSet<Switcher> switchers = new HashSet<Switcher>(mopResult.getConditions());
    switchers.add(switcher);
    return new MOPResult(mopResult.getTarget(), switchers);
  }

  @Override
  public MOPResult mopInvoke(MOPInvokeEvent mopEvent) { 
    Switcher switcher;
    HashMap<String, HashMap<FunctionType, Method>> methodMap;
    lock.lock();
    try {
      getMethods(); // lazy initialize
      methodMap = this.methodMap;
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }

    String name = mopEvent.getName();
    HashMap<FunctionType, Method> map = methodMap.get(name);
    if (map == null) {
      if ("call".equals(name)) {
        return mopDoCall(mopEvent);
      }
      
      return mopMissingInvoke(mopEvent);
    }

    FunctionType signature = mopEvent.getSignature().dropFirstParameter();
    return asMOPResult(switcher, MethodResolver.resolve(this, map, mopEvent.isStatic(), signature, false, mopEvent.getFallback()));
  }

  public MOPResult mopDoCall(MOPInvokeEvent mopEvent) {
    Switcher switcher;
    HashMap<String, HashMap<FunctionType, Method>> methodMap;
    lock.lock();
    try {
      getMethods(); // lazy initialize
      methodMap = this.methodMap;
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }
    
    HashMap<FunctionType, Method> map = methodMap.get("asMethodHandle");
    if (map == null || map.size() != 1) {
      return mopMissingInvoke(mopEvent);
    }
    
    MethodHandle mh = MethodHandles.genericInvoker(RT.asDynMethodType(mopEvent.getSignature()).dropParameterTypes(0, 1));
    MethodHandle combiner = map.values().iterator().next().asMethodHandle();
    mh = MethodHandles.dropArguments(mh, 1, combiner.type().parameterType(0));
    mh = MethodHandles.foldArguments(mh, combiner);
    Closure target = new ReflectClosure(false, mh);
    return asMOPResult(switcher, target);
  }
  
  public MOPResult mopMissingInvoke(MOPInvokeEvent mopEvent) {
    return asMOPResult(switcher, Failures.fail("no method "+mopEvent.getName()+" defined for metaclass "+this));
  }
  
  @Override
  public MOPResult mopNewInstance(MOPNewInstanceEvent mopEvent) {
    Switcher switcher; 
    HashMap<FunctionType, Method> constructorMap;
    lock.lock();
    try {
      getContructors(); // lazy initialize
      constructorMap = this.constructorMap;
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }

    FunctionType signature = mopEvent.getSignature().dropFirstParameter();
    return asMOPResult(switcher, MethodResolver.resolve(this, constructorMap, true, signature, false, mopEvent.getReset()));
  }

  @Override
  public MOPResult mopOperator(MOPOperatorEvent mopEvent) {
    Switcher switcher;
    HashMap<String, HashMap<FunctionType, Method>> methodMap;
    lock.lock();
    try {
      getMethods(); // lazy initialize
      methodMap = this.methodMap;
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }
    String name = mopEvent.getName();
    HashMap<FunctionType, Method> map = methodMap.get(name);
    if (map == null) {
      return asMOPResult(switcher, Failures.fail("no method "+name+" defined for metaclass "+this));
    }

    FunctionType signature = mopEvent.getSignature().dropFirstParameter();
    return asMOPResult(switcher, MethodResolver.resolve(this, map, false, signature, false, mopEvent.getReset()));
  }

  @Override
  public MOPResult mopGetProperty(MOPPropertyEvent mopEvent) {
    Switcher switcher;
    HashMap<String, Property> propertyMap;
    lock.lock();
    try {
      getProperties();  // lazy init
      propertyMap = this.propertyMap;
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }

    String name = mopEvent.getName();
    Property property = propertyMap.get(name);
    Closure getter;
    if (property != null && (getter=property.getGetter()) != null) {
      if (mopEvent.isStatic() && !Modifier.isStatic(property.getModifiers())) {
        return asMOPResult(switcher, Failures.fail("property "+property+" is not static "));
      }
      return asMOPResult(switcher, getter);
    }

    HashMap<String, Attribute> attributeMap;
    lock.lock();
    try {
      getAttributes();  // lazy init
      attributeMap = this.attributeMap;
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }

    Attribute attribute = attributeMap.get(name);
    if (attribute != null && (getter=attribute.getGetter()) != null) {
      if (mopEvent.isStatic() && !Modifier.isStatic(attribute.getModifiers())) {
        return asMOPResult(switcher, Failures.fail("attribute "+attribute+" is not static "));
      }
      return asMOPResult(switcher, getter);
    }

    return mopMissingGetProperty(mopEvent);
  }

  public MOPResult mopMissingGetProperty(MOPPropertyEvent mopEvent) {
    Switcher switcher;
    Collection<Method> methods;
    lock.lock();
    try {
      methods = getMethodsByName("getProperty2");
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }
    
    switch (methods.size()) {
    case 0: // fallthrough
    default:
      return asMOPResult(switcher, Failures.fail("no property "+mopEvent.getName()+" defined for metaclass "+this));

    case 1:
    }

    MethodHandle mh = methods.iterator().next().asMethodHandle();
    mh = MethodHandles.insertArguments(mh, 1, mopEvent);
    Closure target = new ReflectClosure(false, mh);
    return asMOPResult(switcher, target);
  }
  
  
  @Override
  public MOPResult mopSetProperty(MOPPropertyEvent mopEvent) {
    Switcher switcher;
    HashMap<String, Property> propertyMap;
    lock.lock();
    try {
      getProperties();  // lazy init
      propertyMap = this.propertyMap;
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }

    String name = mopEvent.getName();
    Property property = propertyMap.get(name);
    Closure setter;
    if (property != null && (setter = property.getSetter()) != null) {
      if (mopEvent.isStatic() && !Modifier.isStatic(property.getModifiers())) {
        return asMOPResult(switcher, Failures.fail("property "+property+" is not static "));
      }
      return asMOPResult(switcher, setter);
    }

    HashMap<String, Attribute> attributeMap;
    lock.lock();
    try {
      getAttributes();  // lazy init
      attributeMap = this.attributeMap;
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }

    Attribute attribute = attributeMap.get(name);
    if (attribute != null && (setter = attribute.getSetter()) != null) {
      if (mopEvent.isStatic() && !Modifier.isStatic(attribute.getModifiers())) {
        return asMOPResult(switcher, Failures.fail("attribute "+attribute+" is not static "));
      }
      return asMOPResult(switcher, setter);
    }

    return mopMissingSetProperty(mopEvent);
  }
  
  public MOPResult mopMissingSetProperty(MOPPropertyEvent mopEvent) {
    Collection<Method> methods;
    Switcher switcher;
    lock.lock();
    try {
      methods = getMethodsByName("setProperty2");
      switcher = this.switcher;
    } finally {
      lock.unlock();
    }

    switch (methods.size()) {
    case 0: // fallthrough
    default:
      return asMOPResult(switcher, Failures.fail("no property "+mopEvent.getName()+" defined for metaclass "+this));

    case 1:
    }

    MethodHandle mh = methods.iterator().next().asMethodHandle();
    mh = MethodHandles.insertArguments(mh, 1, mopEvent);
    Closure target = new ReflectClosure(false, mh);
    return asMOPResult(switcher, target);
  }
  

  @Override
  public MOPResult mopConverter(MOPConverterEvent mopEvent) {
    MOPResult result = mopNewInstance(new MOPNewInstanceEvent(mopEvent.getCallerClass(),
        false, mopEvent.getFallback(), mopEvent.getReset(), mopEvent.getSignature()));

    Closure target = result.getTarget();
    if (!Failures.isFailure(target)) {
      Closure closure = new ReflectClosure(false, MethodHandles.insertArguments(target.asMethodHandle(), 0, type));
      return new MOPResult(closure, result.getConditions());
    }

    /* infinite loop
    result = mopInvoke(new MOPInvokeEvent(mopEvent.getCallerClass(),
        false, mopEvent.getFallback(), mopEvent.getReset(),
        true, "valueOf", functionType));
    if (!Failures.isFailure(target)) {
      Closure closure = new JVMClosure(false, MethodHandles.insertArguments(target.asMethodHandle(), 0, type));
      return new MOPResult(closure, result.getConditions());
    }*/
    return asMOPResult(switcher, Failures.fail("no conversion from " + this+ " to " + mopEvent.getSignature().getReturnType()));
  }

  
  // -- Meta meta protocol ---------------------------------
  
  public Object getProperty2(MOPPropertyEvent mopEvent) throws Throwable {
    String name = mopEvent.getName();
    Collection<Method> methods = getMethodsByName(name);
    
    switch (methods.size()) {
    case 0: // fallthrough
    default:
      throw new Throwable("no method "+mopEvent.getName()+" found");
      
    case 1:
    }
    
    return methods.iterator().next();
  }
  
  public void setProperty2(MOPPropertyEvent mopEvent, Closure closure) {
    Mutator mutator = mutator();
    try {
      mutator.addMethod(Modifier.PUBLIC, mopEvent.getName(), closure);
    } finally {
      mutator.close();
    }
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
    Switcher switcher = this.switcher;
    this.switcher = new Switcher();
    Switcher.invalidateAll(switcher);

    for(Iterator<WeakReference<MetaClass>> it = subTypes.iterator(); it.hasNext();) {
      WeakReference<MetaClass> reference = it.next();
      MetaClass metaClass = reference.get();
      if (metaClass == null) {
        it.remove();  // cleaning
        continue;
      }

      //FIXME remove cast
      ClassMetaClass expandoMetaClass = (ClassMetaClass)metaClass;
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
      ((ClassMetaClass)superType).subTypes.add(new WeakReference<MetaClass>(ClassMetaClass.this));
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
    public Attribute addAttribute(String name, MetaClass type) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Property addProperty(String name, MetaClass type) {
      mutation = true;
      InstanceProperty property = new InstanceProperty(ClassMetaClass.this, name, type);
      if (addedProperties == null) {
        addedProperties = new ArrayList<Property>();
      }
      addedProperties.add(property);
      return property;
    }

    @Override
    public Method addMethod(int modifiers, String name, Closure closure) {
      mutation = true;
      MethodHandle mh = closure.asMethodHandle();
      if (Modifier.isStatic(modifiers)) {  // a static method is a class method
        mh = MethodHandles.dropArguments(mh, 0, Class.class);
      }
      ReflectMethod method = new ReflectMethod(ClassMetaClass.this, modifiers, name, mh);
      if (addedMethods == null) {
        addedMethods = new ArrayList<Method>();
      }
      addedMethods.add(method);
      return method;
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
    public void removeMethod(String name, MetaClass... types) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removeConstructor(MetaClass... types) {
      throw new UnsupportedOperationException();
    }
  }

  // -- Reflection ---------------------------------------

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
  public Collection<Method> findConstructors(MetaClass... compatibleTypes) {
    FunctionType functionType = new FunctionType(this, compatibleTypes);
    HashMap<FunctionType, Method> constructorMap;
    lock.lock();
    try {  
      getContructors();  // lazy init
      constructorMap = this.constructorMap;
    } finally {
      lock.unlock();
    }
    return MethodResolver.getMostSpecificMethods(this, constructorMap, functionType, false);
  }

  @Override
  public Collection<Method> findMethods(String name, MetaClass... compatibleTypes) {
    HashMap<FunctionType, Method> map;
    lock.lock();
    try {  
      getMethods();  // lazy init
      map = methodMap.get(name);
    } finally {
      lock.unlock();
    }
    FunctionType functionType = new FunctionType(RT.getMetaClass(Object.class), compatibleTypes);
    return MethodResolver.getMostSpecificMethods(this, map, functionType, false);
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
      if (superType instanceof ClassMetaClass) {
        ((ClassMetaClass)superType).populateAttributeMap(attributeMap);  
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

        Attribute attribute = new ReflectAttribute(this, field);
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
      if (superType instanceof ClassMetaClass) {
        ClassMetaClass superTypeAsRaw = (ClassMetaClass)superType;
        superTypeAsRaw.populatePropertyMap(propertyMap, superTypeAsRaw.getMethods());  
      } else {
        for(Property property: superType.getProperties()) {
          propertyMap.put(property.getName(), property);
        }
      }
    }

    HashMap<String, AccessorsEntry> accessorMap =
        new HashMap<String, AccessorsEntry>();
    for (Method method : methods) {
      String name = method.getName();
      if (name.length() > 3 && name.startsWith("get") &&
          method.getParameterCount() == 1) {
        AccessorsEntry entry = accessorEntry(accessorMap, Utils.uncapitalize(name.substring(3)));
        entry.modifiers = method.getModifiers();
        entry.getter = method;
      }
      if (name.length() > 2 && name.startsWith("is") && name.length() > 3 &&
          method.getParameterCount() == 1) {
        AccessorsEntry entry = accessorEntry(accessorMap, Utils.uncapitalize(name.substring(2)));
        entry.modifiers = method.getModifiers();
        entry.getter = method;
      }
      if (name.length() > 3 && name.startsWith("set") &&
          method.getParameterCount() == 2) {
        AccessorsEntry entry = accessorEntry(accessorMap, Utils.uncapitalize(name.substring(3)));
        entry.modifiers = method.getModifiers();
        entry.setter = method;
      }
    }

    for (Map.Entry<String, AccessorsEntry> entry : accessorMap.entrySet()) {
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
      Property property = new ReflectProperty(this, accessorsEntry.modifiers,
          name, type, getter, setter);
      propertyMap.put(name, property);
    }
    
    // add user defined properties
    if (addedProperties != null) {
      for(Property property: addedProperties) {
        propertyMap.put(property.getName(), property);
      }
    }
  }


  @Override
  public Collection<Method> getMethodsByName(String name) {
    lock.lock();
    try {
      getMethods(); // lazy allocated
      HashMap<FunctionType, Method> map = methodMap.get(name);
      if (map == null) {
        return Collections.emptySet();
      }
      if (map.size() == 1) {
        return Collections.singleton(map.values().iterator().next());
      }
      ArrayList<Method> clone = new ArrayList<Method>(map.values());
      return Collections.unmodifiableCollection(clone);
    } finally {
      lock.unlock();
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
      if (superType instanceof ClassMetaClass) {
        ((ClassMetaClass)superType).populateMethodMap(methodMap);  
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
      Class<?> rawClass = RT.getRawClass(mixin);
      if (!Modifier.isPublic(rawClass.getModifiers()))
        continue;
        
      for(java.lang.reflect.Method method: rawClass.getDeclaredMethods()) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
          continue;
        }

        String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length < 1 || name.startsWith("__")) {
          continue;  // this test will skip __init__(MetaClass) or __boot__
        }

        modifiers = modifiers & (~Modifier.STATIC);
        MetaClass declaringMetaClass = RT.getMetaClass(parameterTypes[0]);
        Method metaMethod = new ReflectMethod(declaringMetaClass, modifiers, name, unreflect(method)); 
        addMethodInCache(methodMap, metaMethod);
      }
    }

    if (Modifier.isPublic(type.getModifiers())) {  
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
          Method metaMethod = new ReflectMethod(this, modifiers, name, mh);
          addMethodInCache(methodMap, metaMethod);
      }
    }
    
    // add user-added method
    if (addedMethods != null) {
      for(Method method: addedMethods) {
        addMethodInCache(methodMap, method);
      }
    }
  }

  private static void addMethodInCache(HashMap<String, HashMap<FunctionType, Method>> methodMap, Method method) {
    String name = method.getName();
    HashMap<FunctionType, Method> map = methodMap.get(name);
    if (map == null) {
      map = new HashMap<FunctionType, Method>();
      methodMap.put(name, map);
    }
    map.put(method.getMethodType(), method);
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
        if (mh == null) {  // workaround hotspot bug
          continue;
        }

        // see it as a class method
        modifiers = modifiers | Modifier.STATIC;
        mh = MethodHandles.dropArguments(mh, 0, Class.class);

        Method metaMethod = new ReflectMethod(this, modifiers, "<init>", mh);
        constructorMap.put(metaMethod.getMethodType(), metaMethod);
    }
  }

  private static MethodHandle unreflect(Constructor<?> constructor) {
    try {
      return MethodHandles.publicLookup().unreflectConstructor(constructor);
    } catch (NoAccessException e) {
      throw (AssertionError)new AssertionError().initCause(e);
    } catch(UnsupportedOperationException e) {
      // should not be thrown but hotspot's JSR292 implementation has some restrictions
      return null;
    } 
  }
}
