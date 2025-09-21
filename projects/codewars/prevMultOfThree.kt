/*
Previous multiple of three  (7 kyu)

Given a positive integer n: 0 < n < 1e6, remove the last digit until you're left with a number that is a multiple of three.

Return n if the input is already a multiple of three, and if no such number exists, return null.
 */
fun prevMultOfThree(n: Int): Int? {                     // 1
    var result: String = n.toString()                   // 2
    val length: Int = result.length                     // 3

    when (length) {                                     // 4
        1 -> {                                          // 5
            when(result.toInt() % 3) {                  // 6
                0 -> return n                           // 7
                else -> return null                     // 8
            }
        }
        else -> {                                       // 9
            for(i: Int in 0..length - 1) {              // 10
                var j: Int = result.dropLast(i).toInt() // 11
                if(j % 3 == 0) {                        // 12
                    return j                            // 13
                }
            }
            return null                                 // 14
        }
    }
}
