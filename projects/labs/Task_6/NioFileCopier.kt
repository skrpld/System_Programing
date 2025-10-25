import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.use

class NioFileCopier {

    // Синхронное копирование с использованием NIO
    fun copyFileSync(sourcePath: Path, targetPath: Path) {
        try {
            Files.createDirectories(targetPath.parent)
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
        } catch (e: java.nio.file.FileAlreadyExistsException) {
            println("Файл уже существует: ${e.file}")
        } catch (e: java.nio.file.NoSuchFileException) {
            println("Файл не найден: ${e.file}")
        } catch (e: IOException) {
            println("Ошибка ввода-вывода: ${e.message}")
        }
    }

    // Асинхронное копирование с использованием корутин
    suspend fun copyFileAsync(sourcePath: Path, targetPath: Path) = withContext(Dispatchers.IO) {
        try {
            Files.createDirectories(targetPath.parent)
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
            true
        } catch (e: FileAlreadyExistsException) {
            println("Файл уже существует: ${e.file}")
            false
        } catch (e: NoSuchFileException) {
            println("Файл не найден: ${e.file}")
            false
        } catch (e: IOException) {
            println("Ошибка ввода-вывода: ${e.message}")
            false
        }
    }

    // Дополнительный метод с использованием Files.walk для копирования директорий
    fun copyDirectorySync(sourceDir: Path, targetDir: Path) {
        try {
            Files.createDirectories(targetDir)
            Files.walk(sourceDir).use { paths ->
                paths.forEach { sourcePath ->
                    val relativePath = sourceDir.relativize(sourcePath)
                    val targetPath = targetDir.resolve(relativePath)

                    if (Files.isDirectory(sourcePath)) {
                        Files.createDirectories(targetPath)
                    } else {
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
                    }
                }
            }
        } catch (e: IOException) {
            println("Ошибка при копировании директории: ${e.message}")
        }
    }
}
