
/*
map = new IdentityHashMap()

o = new Object() {
  public static Object getMetaClass(Object o) {
    return map.get(o)   // static access to map ?
  }
}

mixMetaClass = o.metaClass

// should be in a with() 
mutator = Object.metaClass.mutator()
mutator.addMixin(mixMetaClass)
mutator.close()

fooMetaClass = new org.codehaus.groovy2.lang.ExpandoMetaClass(String)
map.put("foo", fooMetaClass);

System.out.prinln("foo".metaClass)
System.out.prinln("foo".metaClass)
*/
