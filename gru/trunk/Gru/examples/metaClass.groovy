
System.out.println(String.metaClass)
System.out.println("foo".metaClass)

System.out.println("foo".metaClass.metaClass)
System.out.println("foo".metaClass.metaClass.metaClass)

def foo(x) {
  System.out.println(x.metaClass.isEmpty)
}

foo("foo")
foo(new java.util.ArrayList())

def myIsEmpty(s) {
  return true
}

String.metaClass.isEmpty = myIsEmpty
foo("foo")






