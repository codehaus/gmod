
o = new Object() {
  public Object getProperty2(groovy2.lang.mop.MOPPropertyEvent mopEvent) {
    return mopEvent.getName()
  }
  
  public void setProperty2(groovy2.lang.mop.MOPPropertyEvent mopEvent, Object value) {
    System.out.println(value)
  }
}

o.foo = "foo"
System.out.println(o.bar)
