class Account(private var _balance: Double = 0.0) {
    private val lock = Object()

    val balance: Double
        get() = synchronized(lock) { _balance }

    fun deposit(amount: Double) {
        synchronized(lock) {
            require(amount > 0) { "Сумма пополнения должна быть положительной" }
            _balance += amount
            println("Пополнение: +${"%.2f".format(amount)} руб. Баланс: ${"%.2f".format(_balance)} руб.")
            lock.notifyAll()
        }
    }

    fun withdraw(amount: Double): Boolean {
        synchronized(lock) {
            require(amount > 0) { "Сумма снятия должна быть положительной" }
            if (_balance >= amount) {
                _balance -= amount
                println("Снятие: -${"%.2f".format(amount)} руб. Баланс: ${"%.2f".format(_balance)} руб.")
                return true
            }
            return false
        }
    }

    fun waitForAmount(targetAmount: Double, timeout: Long = Long.MAX_VALUE): Boolean {
        synchronized(lock) {
            require(targetAmount > 0) { "Целевая сумма должна быть положительной" }

            println("Ожидание накопления ${"%.2f".format(targetAmount)} руб. Текущий баланс: ${"%.2f".format(_balance)} руб.")

            var remainingTime = timeout
            val startTime = System.currentTimeMillis()

            while (_balance < targetAmount && remainingTime > 0) {
                try {
                    lock.wait(remainingTime)
                    val elapsed = System.currentTimeMillis() - startTime
                    remainingTime = timeout - elapsed
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return false
                }
            }

            return _balance >= targetAmount
        }
    }
}
