
def p = new java.awt.Point()
mutator = p.metaClass.mutator()
mutator.addProperty("z", int.metaClass)
mutator.close()

//System.out.println(p.metaClass.properties)

System.out.println()

//def setter = p.metaClass.findProperty("z").setter
//setter(p, 3)

p.z = 3
System.out.println(p.z)

System.out.println("done !")

