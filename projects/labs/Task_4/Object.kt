import kotlinx.coroutines.delay

class Object(
    val name: String = "undefied",
    val n: Int = 1,
    val modifier: Long = 10 //modifier for prioritize
) {
    suspend fun run(){
        for(i in 1..n) {
            println(name)
            delay(modifier)
        }
    }
}
