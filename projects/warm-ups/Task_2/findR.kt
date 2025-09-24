import kotlin.collections.sorted
import kotlin.random.Random


fun main() {
//    val debug = Debug()
    val array = List(1000) { Random.nextInt(1, 10001) }

//    debug.out(array)
    findR2(array)
}

// Classic solution (1 pass)
fun findR(array: List<Int>): Int {
//    val debug = Debug()
    val rSet = kotlin.collections.mutableSetOf<Int>()
    val multSet = kotlin.collections.mutableSetOf<Int>()

    for(i in array) {
        when {
            (i % 21 == 0) -> rSet.add(i)
            (i % 3 == 0 || i % 7 == 0) -> multSet.add(i)
        }
    }

    val rSetSorted = rSet.sorted()
    val multSetSorted = multSet.sorted()
//    debug.out(rSetSorted)
//    debug.out(multSetSorted)

    for(i in rSetSorted) {
        for(n in multSetSorted) {
            when{
                (i % n == 0) ->
                    if(multSetSorted.contains(i / n)) {
//                        debug.out(i, n, i / n)
                        return i
                    }
            }
        }
    }
    return -1
}

// My solution (all variants)
fun findR2(array: List<Int>): Int{
    val debug = Debug()
    val array = array.sorted()

    var i = 0
    while(i < array.size - 1) {
        if(array[i] == array[i + 1]) i += 2
        if(array[i] % 21 == 0) {
            for(n in 0..< i) {
                if(array[i] % array[n] == 0) {
                    for(m in n + 1..< i) {
                        if (array[i] / array[n] == array[m]) {
                            debug.out(array[i], array[n], array[m])
                            return array[i]
                        }
                    }
                }
            }
        }
        i++
    }
    debug.out()
    return -1
}

// functions for debug
class Debug {
    fun out() {
        println("\nNumber R is not exist")
    }
    fun out(r: Int, a: Int, b: Int) {
        println("\nNumber R is: $r\nMultipliers is: $a and $b")
    }
    fun out(array: List<Int>) {
        for(i in 1..<array.size) {
            when(i % 10) {
                0 -> println(array[i - 1].toString() + " ")
                else -> print(array[i - 1].toString() + " ")
            }
        }
    }
}