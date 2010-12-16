
def x = javax.swing.JButton.metaClass.findProperty("foreground")

def getter = x.getGetter()
def setter = x.getSetter()

System.out.println(getter)
System.out.println(setter)

def button = new javax.swing.JButton()

setter(button, java.awt.Color.RED)
System.out.println(getter(button))
System.out.println(button.foreground)



