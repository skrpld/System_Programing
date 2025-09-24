import kotlinx.coroutines.*

suspend fun main() = kotlinx.coroutines.coroutineScope {
    for (i in 1..10) {
        launch {
            kotlin.io.println("Process $i started:\t${java.lang.Thread.currentThread().name}")
            kotlinx.coroutines.delay(100)
            kotlin.io.println("Process $i\tended:\t${java.lang.Thread.currentThread().name}")
        }
    }
}