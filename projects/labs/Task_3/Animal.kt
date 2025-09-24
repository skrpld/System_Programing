import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class Animal(
    val name: String,
    val priority: Int,
    val distance: Int
) {
    suspend fun run(): String = coroutineScope {
        for(i in 0..distance){
            if(i % 10 == 0) {       // Message every 10 meters? of distance
                println("$name has run $i\tmeters") // ${Thread.currentThread().name}
            }
        }
        delay((11 - priority) * 10L)
        name
    }
}