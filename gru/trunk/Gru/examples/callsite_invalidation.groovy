
//java -XX:+UnlockExperimentalVMOptions -XX:+EnableInvokeDynamic -cp classes:lib/groovy-1.7.5.jar:lib/commons-cli-1.2.jar:lib/asm-all-3.3.jar:lib/antlr-2.7.7.jar groovy.ui.GroovyMain examples/example.groovy


def foo(x, y) {
  x.charAt(y)
}

System.out.println(foo("hello" , 1))
System.out.println(foo("foo" , 2))

//String.metaClass.flushCache()
//String.metaClass.seal()

System.out.println(foo("hello" , 1))
System.out.println(foo("foo" , 2))









 
