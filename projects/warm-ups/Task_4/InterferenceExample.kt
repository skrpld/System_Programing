import java.util.concurrent.atomic.AtomicInteger

internal class InterferenceExample {
    private val counter = AtomicInteger()
    private var sharedCounter = 0 // variable with synchronized

    fun stop(): Boolean {
        return counter.incrementAndGet() > HUNDRED_MILLION
    }

    // Synchronized increment method
    @Synchronized
    fun incrementCounter() {
        sharedCounter++
    }

    // Synchronized getter
    @Synchronized
    fun getCounter(): Int {
        return sharedCounter
    }

    @Throws(InterruptedException::class)
    fun example() {
        val thread1 = InterferenceThread(this)
        val thread2 = InterferenceThread(this)
        thread1.start()
        thread2.start()
        thread1.join()
        thread2.join()
        println("Expected: ${2 * HUNDRED_MILLION}")
        println("Result: ${thread1.i}")
    }

    companion object {
        private const val HUNDRED_MILLION = 100000000
    }
}
