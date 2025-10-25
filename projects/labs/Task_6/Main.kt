import kotlinx.coroutines.*
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.system.measureTimeMillis

suspend fun main() = run {
    val copier = NioFileCopier()

    // === 1. Подготовка ===
    println("Создание больших тестовых файлов с использованием NIO...")
    createTestFiles()
    println("Тестовые файлы созданы.\n")

    val syncOutputDir = Paths.get("nio_sync_output")
    val asyncOutputDir = Paths.get("nio_async_output")

    val sourceFiles = listOf(
        Paths.get("nio_source_1.txt"),
        Paths.get("nio_source_2.txt"),
        Paths.get("nio_source_3.txt")
    )

    val syncTargets = listOf(
        syncOutputDir.resolve("file1_nio_copy.txt"),
        syncOutputDir.resolve("file2_nio_copy.txt"),
        syncOutputDir.resolve("file3_nio_copy.txt")
    )

    val asyncTargets = listOf(
        asyncOutputDir.resolve("file1_nio_copy.txt"),
        asyncOutputDir.resolve("file2_nio_copy.txt"),
        asyncOutputDir.resolve("file3_nio_copy.txt")
    )

    // === 2. Синхронное копирование с NIO ===
    println("=== Запуск СИНХРОННОГО копирования с NIO ===")
    val syncTime = measureTimeMillis {
        try {
            sourceFiles.forEachIndexed { index, sourceFile ->
                copier.copyFileSync(sourceFile, syncTargets[index])
                println("Файл ${index + 1} скопирован синхронно...")
            }
        } catch (e: Exception) {
            println("Ошибка при синхронном копировании: ${e.message}")
        }
    }
    println("=== Синхронное копирование завершено ===")
    println("Время выполнения синхронного копирования: $syncTime мс\n")

    // === 3. Асинхронное копирование с NIO ===
    println("=== Запуск АСИНХРОННОГО копирования с NIO ===")
    val asyncTime = measureTimeMillis {
        coroutineScope {
            val jobs = sourceFiles.mapIndexed { index, sourceFile ->
                async(Dispatchers.IO) {
                    try {
                        val success = copier.copyFileAsync(sourceFile, asyncTargets[index])
                        if (success) {
                            println("Асинхронное копирование файла ${index + 1} завершено.")
                        }
                    } catch (e: Exception) {
                        println("Ошибка при асинхронном копировании файла ${index + 1}: ${e.message}")
                    }
                }
            }
            jobs.awaitAll()
        }
    }
    println("=== Асинхронное копирование завершено ===")
    println("Время выполнения асинхронного копирования: $asyncTime мс\n")

    // === 4. Демонстрация копирования директории ===
    println("=== Демонстрация копирования директории с NIO ===")
    val testDir = Paths.get("test_directory")
    val testDirCopy = Paths.get("test_directory_copy")

    try {
        // Создаем тестовую директорию с файлами
        Files.createDirectories(testDir)
        Files.write(testDir.resolve("file_in_dir_1.txt"), "Содержимое файла 1 в директории".toByteArray())
        Files.write(testDir.resolve("file_in_dir_2.txt"), "Содержимое файла 2 в директории".toByteArray())

        // Копируем всю директорию
        copier.copyDirectorySync(testDir, testDirCopy)
        println("Директория успешно скопирована с использованием NIO")
    } catch (e: Exception) {
        println("Ошибка при копировании директории: ${e.message}")
    }

    // === 5. Сравнение и очистка ===
    println("\n=== Результаты ===")
    println("Синхронное копирование (NIO): $syncTime мс")
    println("Асинхронное копирование (NIO): $asyncTime мс")

    if (asyncTime < syncTime) {
        val difference = syncTime - asyncTime
        val percentage = (difference.toDouble() / syncTime * 100).toInt()
        println("Асинхронное копирование было быстрее на $difference мс ($percentage%).")
    } else {
        println("Синхронное копирование было быстрее или заняло столько же времени.")
    }

    println("\nУдаление временных файлов и директорий...")
    try {
        // Удаляем исходные файлы
        sourceFiles.forEach { Files.deleteIfExists(it) }

        // Удаляем выходные директории
        Files.walkFileTree(syncOutputDir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                Files.delete(dir)
                return FileVisitResult.CONTINUE
            }
        })

        Files.walkFileTree(asyncOutputDir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                Files.delete(dir)
                return FileVisitResult.CONTINUE
            }
        })

        // Удаляем тестовые директории
        Files.walkFileTree(testDir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                Files.delete(dir)
                return FileVisitResult.CONTINUE
            }
        })

        Files.walkFileTree(testDirCopy, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                Files.delete(dir)
                return FileVisitResult.CONTINUE
            }
        })

        println("Очистка завершена.")
    } catch (e: Exception) {
        println("Ошибка при очистке: ${e.message}")
    }
}