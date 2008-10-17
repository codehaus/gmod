import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.Callable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.FutureTask
import groovy.jms.pool.JMSConnector
import javax.jms.MessageListener

int threads = 5;
ExecutorService pool = Executors.newFixedThreadPool(threads)
Future f0 = pool.submit(new JMSConnector([queue:'myQueue'], { m -> println m}))
sleep(1000)
println "future.isDone()? "+f0.isDone()
f0.cancel(true)
sleep(5000)
println "future.isDone()? " +f0.isDone()
sleep(5000)
//System.exit(-1)

