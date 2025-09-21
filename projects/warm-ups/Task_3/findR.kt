//import kotlin.collections.sorted
//import kotlin.random.Random
//
//fun main() {
//    val array = kotlin.collections.List(1000) { kotlin.random.Random.Default.nextInt(1, 10001) }
////    for(i in 1..1000) {
////        when(i % 10) {
////            0 -> println(array[i - 1].toString() + " ")
////            else -> print(array[i - 1].toString() + " ")
////        }
////    }
//    findR(array)
//}

fun findR(array: List<Int>): Int {
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
//    println(rSetSorted)
//    println(multSetSorted)

    for(i in rSetSorted) {
        for(n in multSetSorted) {
            when{
                (i % n == 0) ->
                    if(multSetSorted.contains(i / n)) {
                        out(i, n, i / n)    //for debug
                        return i
                    }
            }
        }
    }
    return -1
}

fun out(r: Int, a: Int, b: Int) {
    kotlin.io.println("Number R is: $r\nMultipliers is: $a and $b")
}