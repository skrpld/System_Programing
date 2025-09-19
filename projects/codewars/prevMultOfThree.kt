/*
Previous multiple of three  (7 kyu)

Given a positive integer n: 0 < n < 1e6, remove the last digit until you're left with a number that is a multiple of three.

Return n if the input is already a multiple of three, and if no such number exists, return null.
 */
fun prevMultOfThree(n: Int): Int? {
    var result: String = n.toString()
    val length: Int = result.length

    when (length) {
        1 -> {
            if(result.toInt() % 3 == 0) return n
            else return null
        }
        else -> {
            for(i: Int in 0..length - 1) {
                var j: Int = result.dropLast(i).toInt()
                if(j % 3 == 0) {
                    return j
                }
            }
            return null
        }
    }
}
