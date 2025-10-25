import kotlin.concurrent.thread
import kotlin.random.Random

fun main() {
    val account = Account()
    val targetAmount = 1000.0 // Target withdrawal

    // Start deposit thread
    val depositThread = thread(name = "DepositThread") {
        var depositCount = 0
        while (depositCount < 15) { // limit num of deposit
            try {
                Thread.sleep(Random.nextLong(500, 1500))
                val depositAmount = Random.nextDouble(50.0, 300.0)
                account.deposit(depositAmount)
                depositCount++
            } catch (e: InterruptedException) {
                println("Поток пополнения прерван")
                break
            }
        }
        println("Поток пополнения завершил работу")
    }

    // Main thread wait deposit and withdrawal
    thread(name = "MainThread") {
        println("Начало работы. Целевая сумма для снятия: ${"%.2f".format(targetAmount)} руб.")

        // Wait target amount
        val success = account.waitForAmount(targetAmount, 30000)

        if (success) {
            println("Целевая сумма достигнута! Пытаемся снять ${"%.2f".format(targetAmount)} руб.")
            val withdrawSuccess = account.withdraw(targetAmount)
            if (withdrawSuccess) {
                println("Снятие прошло успешно!")
            } else {
                println("Не удалось снять деньги")
            }
        } else {
            println("Таймаут ожидания или прерывание потока")
        }

        // time for end thread
        Thread.sleep(2000)
        println("Финальный баланс: ${"%.2f".format(account.balance)} руб.")
        depositThread.interrupt()
    }.join()
}
