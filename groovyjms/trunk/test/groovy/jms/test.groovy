import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.Callable

ExecutorService pool = Executors.newFixedThreadPool(10)
Future f = pool.submit({
    try {
     sleep(5000)
        return "done"
    } catch (InterruptedException e) {
        e.printStackTrace()
    }
} as Callable)

sleep(100)
println "isDone? " + f.isDone()

println f.get()

sleep(1000)
println "isDone? " + f.isDone()
println pool.shutdownNow()
println pool.isTerminated()