
o = new Object() {
  public String take(List list) {
    return 'a list'
  }
	 
  public String take(String s) {
    return 'a string'
  }
}

String s = null
System.out.println(o.take(s))
