
o = new Object() {
  public Object getProperty2(groovy2.lang.mop.MOPPropertyEvent mopEvent) {
    //System.out.println(mopEvent.getName());
    return mopEvent.getName()
  }
}

System.out.println(o.x)
System.out.println(o.y)
