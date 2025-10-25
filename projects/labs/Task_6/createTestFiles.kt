import java.nio.file.Files
import java.nio.file.Paths

fun createTestFiles() {
    val testContent1 = "Это содержимое первого файла с использованием NIO. ".repeat(100000)
    val testContent2 = "Это содержимое второго файла с использованием NIO. ".repeat(100000)
    val testContent3 = "Это содержимое третьего файла с использованием NIO. ".repeat(100000)

    Files.write(Paths.get("nio_source_1.txt"), testContent1.toByteArray())
    Files.write(Paths.get("nio_source_2.txt"), testContent2.toByteArray())
    Files.write(Paths.get("nio_source_3.txt"), testContent3.toByteArray())

    println("Тестовые файлы созданы с использованием NIO")
}
