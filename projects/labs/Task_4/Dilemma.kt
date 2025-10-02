import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class Dilemma {
    suspend fun start() = coroutineScope {
        val numOfIteration = Random.nextInt(10, 20)

        val chicken = Object("chicken", numOfIteration)
        val egg = Object("egg", numOfIteration)

        launch { chicken.run() }
        launch { egg.run() }
    }
}
