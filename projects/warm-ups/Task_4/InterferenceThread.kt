class InterferenceThread internal constructor(private val checker: InterferenceExample) : Thread() {

    private fun increment() {
        checker.incrementCounter()
    }

    val i: Int
        get() = checker.getCounter()

    override fun run() {
        while (!checker.stop()) {
            increment()
        }
    }
}
