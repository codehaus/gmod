import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

ExecutorService pool = Executors.newFixedThreadPool(10)
Future f = pool.submit({
    try {
        while (true) {
            // loop forever
        }
    } catch (InterruptedException e) {
        e.printStackTrace()
    }
} as Runnable)

sleep(100)
println "isDone? " + f.isDone()

f.cancel(true)

sleep(1000)
println "isDone? " + f.isDone()
println pool.shutdownNow()
println pool.isTerminated()