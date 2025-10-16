import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import kotlin.system.measureTimeMillis

fun copyFileToDirectory(sourceFile: File, destinationDir: File, newFileName: String) {
    if (!destinationDir.exists()) {
        destinationDir.mkdirs()
    }
    val destinationFile = File(destinationDir, newFileName)
    sourceFile.copyTo(destinationFile, overwrite = true)
}

fun main() = runBlocking {
    // === 1. Подготовка ===
    println("Создание больших тестовых файлов...")
    val sourceFile1 = File("source_file_1.txt").apply {
        writeText("Это содержимое первого файла. ".repeat(100500))
    }
    val sourceFile2 = File("source_file_2.txt").apply {
        writeText("Это содержимое второго файла. ".repeat(100500))
    }
    println("Тестовые файлы созданы.\n")

    val sequentialDestinationDir = File("sequential_copy_output")
    val parallelDestinationDir = File("parallel_copy_output")

    // === 2. Последовательное копирование ===
    println("=== Запуск ПОСЛЕДОВАТЕЛЬНОГО копирования ===")
    val sequentialTime = measureTimeMillis {
        try {
            copyFileToDirectory(sourceFile1, sequentialDestinationDir, "file1_copy.txt")
            println("Файл 1 скопирован...")
            copyFileToDirectory(sourceFile2, sequentialDestinationDir, "file2_copy.txt")
            println("Файл 2 скопирован...")
        } catch (e: IOException) {
            println("Ошибка при последовательном копировании: ${e.message}")
        }
    }
    println("=== Последовательное копирование завершено ===")
    println("Время выполнения последовательного копирования: $sequentialTime мс\n")


    // === 3. Параллельное копирование ===
    println("=== Запуск ПАРАЛЛЕЛЬНОГО копирования ===")
    val parallelTime = measureTimeMillis {
        coroutineScope {
            val job1 = async(Dispatchers.IO) {
                try {
                    copyFileToDirectory(sourceFile1, parallelDestinationDir, "file1_copy.txt")
                    println("Параллельное копирование файла 1 завершено.")
                } catch (e: IOException) {
                    println("Ошибка при параллельном копировании файла 1: ${e.message}")
                }
            }

            val job2 = async(Dispatchers.IO) {
                try {
                    copyFileToDirectory(sourceFile2, parallelDestinationDir, "file2_copy.txt")
                    println("Параллельное копирование файла 2 завершено.")
                } catch (e: IOException) {
                    println("Ошибка при параллельном копировании файла 2: ${e.message}")
                }
            }

            awaitAll(job1, job2)
        }
    }
    println("=== Параллельное копирование завершено ===")
    println("Время выполнения параллельного копирования: $parallelTime мс\n")

    // === 4. Сравнение и очистка ===
    println("=== Результаты ===")
    println("Последовательное: $sequentialTime мс")
    println("Параллельное:    $parallelTime мс")
    if (parallelTime < sequentialTime) {
        val difference = sequentialTime - parallelTime
        val percentage = (difference.toDouble() / sequentialTime * 100).toInt()
        println("Параллельное копирование было быстрее на $difference мс ($percentage%).")
    } else {
        println("Последовательное копирование было быстрее или заняло столько же времени.")
    }

    println("\nУдаление временных файлов и директорий...")
    sourceFile1.delete()
    sourceFile2.delete()
    sequentialDestinationDir.deleteRecursively()
    parallelDestinationDir.deleteRecursively()
    println("Очистка завершена.")
}