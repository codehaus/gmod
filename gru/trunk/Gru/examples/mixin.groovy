
o = new Object() {
  public static String yai(String s) {
    return s.concat(" yai");
  }
}

mixMetaClass = o.metaClass

// should be in a with() 
mutator = String.metaClass.mutator()
mutator.addMixin(mixMetaClass)
mutator.close()

System.out.println("groovy".yai())
