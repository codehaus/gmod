
To use the groovy prefuse library, you will need to copy the prefuse 
jar and this projects jar into your ~/.groovy/lib/ or make sure that 
it is in the class path. After doing so you should be able to run 
the below code.

BELOW IS AN EXAMPLE OF THE LIBRARIES USAGE
=============================================

// create the prefuse graph
def prefuse = new groovy.prefuse.PrefuseBuilder()
def graph = prefuse.graph {
    node("Grand Parent") {
       node("Parent") {
           node("Child 1")
           node("Child 2")
       }
    }
}
// create a swing app using the Groovy SwingBuilder to display the graph
def swing = new groovy.swing.SwingBuilder()
def frame = swing.frame(title:'PrefuseBuilder Test',defaultCloseOperation:javax.swing.WindowConstants.EXIT_ON_CLOSE) {
    widget(graph)
}
frame.show()
