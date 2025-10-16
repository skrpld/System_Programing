import kotlinx.coroutines.*
import kotlinx.coroutines.async
import java.io.File
import java.io.IOException
import kotlin.apply
import kotlin.io.copyTo
import kotlin.io.deleteRecursively
import kotlin.io.writeText
import kotlin.system.measureTimeMillis
import kotlin.text.repeat

fun copyFileToDirectory(sourceFile: java.io.File, destinationDir: java.io.File, newFileName: String) {
    if (!destinationDir.exists()) {
        destinationDir.mkdirs()
    }
    val destinationFile = java.io.File(destinationDir, newFileName)
    sourceFile.copyTo(destinationFile, overwrite = true)
}

fun main() = kotlinx.coroutines.runBlocking {
    // === 1. Подготовка ===
    kotlin.io.println("Создание больших тестовых файлов...")
    val sourceFile1 = java.io.File("source_file_1.txt").apply {
        writeText("Это содержимое первого файла. ".repeat(100500))
    }
    val sourceFile2 = java.io.File("source_file_2.txt").apply {
        writeText("Это содержимое второго файла. ".repeat(100500))
    }
    kotlin.io.println("Тестовые файлы созданы.\n")

    val sequentialDestinationDir = java.io.File("sequential_copy_output")
    val parallelDestinationDir = java.io.File("parallel_copy_output")

    // === 2. Последовательное копирование ===
    kotlin.io.println("=== Запуск ПОСЛЕДОВАТЕЛЬНОГО копирования ===")
    val sequentialTime = kotlin.system.measureTimeMillis {
        try {
            copyFileToDirectory(sourceFile1, sequentialDestinationDir, "file1_copy.txt")
            kotlin.io.println("Файл 1 скопирован...")
            copyFileToDirectory(sourceFile2, sequentialDestinationDir, "file2_copy.txt")
            kotlin.io.println("Файл 2 скопирован...")
        } catch (e: java.io.IOException) {
            kotlin.io.println("Ошибка при последовательном копировании: ${e.message}")
        }
    }
    kotlin.io.println("=== Последовательное копирование завершено ===")
    kotlin.io.println("Время выполнения последовательного копирования: $sequentialTime мс\n")


    // === 3. Параллельное копирование ===
    kotlin.io.println("=== Запуск ПАРАЛЛЕЛЬНОГО копирования ===")
    val parallelTime = kotlin.system.measureTimeMillis {
        kotlinx.coroutines.coroutineScope {
            val job1 = async(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    copyFileToDirectory(sourceFile1, parallelDestinationDir, "file1_copy.txt")
                    kotlin.io.println("Параллельное копирование файла 1 завершено.")
                } catch (e: java.io.IOException) {
                    kotlin.io.println("Ошибка при параллельном копировании файла 1: ${e.message}")
                }
            }

            val job2 = async(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    copyFileToDirectory(sourceFile2, parallelDestinationDir, "file2_copy.txt")
                    kotlin.io.println("Параллельное копирование файла 2 завершено.")
                } catch (e: java.io.IOException) {
                    kotlin.io.println("Ошибка при параллельном копировании файла 2: ${e.message}")
                }
            }

            kotlinx.coroutines.awaitAll(job1, job2)
        }
    }
    kotlin.io.println("=== Параллельное копирование завершено ===")
    kotlin.io.println("Время выполнения параллельного копирования: $parallelTime мс\n")

    // === 4. Сравнение и очистка ===
    kotlin.io.println("=== Результаты ===")
    kotlin.io.println("Последовательное: $sequentialTime мс")
    kotlin.io.println("Параллельное:    $parallelTime мс")
    if (parallelTime < sequentialTime) {
        val difference = sequentialTime - parallelTime
        val percentage = (difference.toDouble() / sequentialTime * 100).toInt()
        kotlin.io.println("Параллельное копирование было быстрее на $difference мс ($percentage%).")
    } else {
        kotlin.io.println("Последовательное копирование было быстрее или заняло столько же времени.")
    }

    kotlin.io.println("\nУдаление временных файлов и директорий...")
    sourceFile1.delete()
    sourceFile2.delete()
    sequentialDestinationDir.deleteRecursively()
    parallelDestinationDir.deleteRecursively()
    kotlin.io.println("Очистка завершена.")
}