import javax.jms.MessageListener

def test = {}

println test

println test.getClass().fields.find{it.name =="test"}
println test.getClass().fields.find{it.name =="test"}!=null

//test.getClass().metaClass.getTest << { -> "this is a test"}
test.getClass().metaClass.test = "this is a threadlocal test"

test.test = "hello world"



new Thread({test.test = 'abc';println test.test}).run()
sleep 1000
println test.getClass().metaClass.properties.find{it.name =="test"}
println test.test






