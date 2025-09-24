import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select

class Race {
    suspend fun startRace() = coroutineScope {
        val rabbit = Animal("rabbit", 3, 100)   // Set (name, priority, distance)
        val turtle = Animal("turtle", 8, 100)

        val result = select<String> {
            async { rabbit.run() }.onAwait { it }
            async { turtle.run() }.onAwait { it }
        }

        winner(result)
    }

    private fun winner(result: String) {
        val formated = result.padEnd(16).take(16)
        print("""
            
            ██▓▓▒▒▓▓▓▓▒▒▒▒▓▓▓▓▒▒▓▓██
            ▓▓                    ▓▓
            ▒▒     Winner is:     ▒▒
            ▒▒  ${formated}  ▒▒
            ▓▓                    ▓▓
            ██▓▓▒▒▓▓▓▓▒▒▒▒▓▓▓▓▒▒▓▓██
            
        """.trimIndent())
    }
}