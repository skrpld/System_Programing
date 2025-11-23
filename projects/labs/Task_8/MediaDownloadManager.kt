import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class MediaDownloadManager {
    companion object {
        private const val RESOURCE_LIST_FILE = "resources.txt"
        private const val MUSIC_DIRECTORY = "music"
        private const val IMAGES_DIRECTORY = "images"
        private val downloadExecutor = Executors.newFixedThreadPool(5)

        private val trackImageMapping = ConcurrentHashMap<Int, String>()
        private val isDownloadComplete = AtomicBoolean(false)
        private val hasSuccessfulDownloads = AtomicBoolean(false)

        @JvmStatic
        fun main(args: Array<String>) {
            println("=== Менеджер загрузки медиафайлов ===")

            createWorkingDirectories()

            val downloadProcess = Thread { executeDownloadProcess() }
            downloadProcess.start()

            initializeMediaPlayer()

            downloadProcess.join()
            downloadExecutor.shutdown()
            downloadExecutor.awaitTermination(1, TimeUnit.MINUTES)
        }

        private fun createWorkingDirectories() {
            try {
                Files.createDirectories(Paths.get(MUSIC_DIRECTORY))
                Files.createDirectories(Paths.get(IMAGES_DIRECTORY))
                println("Рабочие директории подготовлены")
            } catch (e: Exception) {
                System.err.println("Ошибка создания директорий: ${e.message}")
            }
        }

        private fun executeDownloadProcess() {
            println("\n=== Инициализация процесса загрузки ===")

            val resourceUrls = readResourceUrls() ?: return
            val (audioUrls, pictureUrls) = resourceUrls

            if (audioUrls.isEmpty() && pictureUrls.isEmpty()) {
                println("Не обнаружено URL-адресов для загрузки")
                isDownloadComplete.set(true)
                return
            }

            processMediaDownloads(audioUrls, pictureUrls)
            displayDownloadResults(audioUrls.size, pictureUrls.size)
            isDownloadComplete.set(true)
        }

        private fun readResourceUrls(): Pair<List<String>, List<String>>? {
            return try {
                val audioLinks = mutableListOf<String>()
                val imageLinks = mutableListOf<String>()

                File(RESOURCE_LIST_FILE).forEachLine { line ->
                    val cleanUrl = line.trim()
                    if (cleanUrl.isNotEmpty()) {
                        when {
                            cleanUrl.endsWith(".mp3") -> audioLinks.add(cleanUrl)
                            isSupportedImageFormat(cleanUrl) -> imageLinks.add(cleanUrl)
                        }
                    }
                }

                Pair(audioLinks, imageLinks)
            } catch (e: Exception) {
                System.err.println("Ошибка чтения файла ресурсов: ${e.message}")
                null
            }
        }

        private fun isSupportedImageFormat(url: String): Boolean {
            val validImageFormats = listOf(".jpg", ".jpeg", ".png", ".gif")
            return validImageFormats.any { url.contains(it, ignoreCase = true) }
        }

        private fun processMediaDownloads(audioUrls: List<String>, imageUrls: List<String>) {
            val totalDownloads = audioUrls.size + imageUrls.size
            val completionSignal = CountDownLatch(totalDownloads)
            val successfulAudioDownloads = AtomicInteger(0)
            val successfulImageDownloads = AtomicInteger(0)

            audioUrls.forEachIndexed { index, audioUrl ->
                val mediaPairId = index + 1
                val associatedImageUrl = imageUrls.getOrNull(index)

                downloadAudioWithAssociatedImage(audioUrl, associatedImageUrl, mediaPairId,
                    successfulAudioDownloads, successfulImageDownloads, completionSignal)
            }

            if (imageUrls.size > audioUrls.size) {
                for (i in audioUrls.size until imageUrls.size) {
                    val mediaPairId = i + 1
                    downloadStandaloneImage(imageUrls[i], mediaPairId, successfulImageDownloads, completionSignal)
                }
            }

            completionSignal.await()

            if (successfulAudioDownloads.get() > 0 || successfulImageDownloads.get() > 0) {
                hasSuccessfulDownloads.set(true)
            }
        }

        private fun downloadAudioWithAssociatedImage(
            audioUrl: String,
            imageUrl: String?,
            pairId: Int,
            audioCounter: AtomicInteger,
            imageCounter: AtomicInteger,
            completionSignal: CountDownLatch
        ) {
            val audioFileName = "$MUSIC_DIRECTORY/audio_track_$pairId.mp3"

            downloadExecutor.submit {
                try {
                    if (retrieveFileFromUrl(audioUrl, audioFileName)) {
                        audioCounter.incrementAndGet()
                        println("Аудиофайл загружен: audio_track_$pairId.mp3")
                    } else {
                        handleFailedDownload("аудио", pairId, audioUrl)
                    }
                } catch (e: IOException) {
                    handleFailedDownload("аудио", pairId, audioUrl, e.message)
                } finally {
                    completionSignal.countDown()
                }
            }

            imageUrl?.let { url ->
                val imageFileName = generateImageFilename(url, pairId)
                trackImageMapping[pairId] = imageFileName

                downloadExecutor.submit {
                    try {
                        if (retrieveFileFromUrl(url, imageFileName)) {
                            imageCounter.incrementAndGet()
                            println("Изображение загружен: ${extractFilename(imageFileName)}")
                            validateDownloadedFile(imageFileName)
                        } else {
                            handleFailedDownload("изображение", pairId, url)
                            trackImageMapping.remove(pairId)
                        }
                    } catch (e: IOException) {
                        handleFailedDownload("изображение", pairId, url, e.message)
                        trackImageMapping.remove(pairId)
                    } finally {
                        completionSignal.countDown()
                    }
                }
            }
        }

        private fun downloadStandaloneImage(
            imageUrl: String,
            pairId: Int,
            imageCounter: AtomicInteger,
            completionSignal: CountDownLatch
        ) {
            val imageFileName = generateImageFilename(imageUrl, pairId)

            downloadExecutor.submit {
                try {
                    if (retrieveFileFromUrl(imageUrl, imageFileName)) {
                        imageCounter.incrementAndGet()
                        println("Изображение загружено: ${extractFilename(imageFileName)}")
                    } else {
                        handleFailedDownload("изображение", pairId, imageUrl)
                    }
                } catch (e: IOException) {
                    handleFailedDownload("изображение", pairId, imageUrl, e.message)
                } finally {
                    completionSignal.countDown()
                }
            }
        }

        private fun handleFailedDownload(fileType: String, pairId: Int, url: String, additionalInfo: String? = null) {
            val baseMessage = "Сбой загрузки $fileType $pairId: $url"
            val fullMessage = if (additionalInfo != null) "$baseMessage ($additionalInfo)" else baseMessage
            System.err.println(fullMessage)
        }

        private fun generateImageFilename(url: String, pairId: Int): String {
            val fileExtension = when {
                url.contains(".png", ignoreCase = true) -> ".png"
                url.contains(".jpeg", ignoreCase = true) -> ".jpeg"
                url.contains(".gif", ignoreCase = true) -> ".gif"
                else -> ".jpg"
            }
            return "$IMAGES_DIRECTORY/visual_$pairId$fileExtension"
        }

        private fun extractFilename(fullPath: String): String = File(fullPath).name

        private fun validateDownloadedFile(filePath: String) {
            val targetFile = File(filePath)
            if (targetFile.exists() && targetFile.length() > 0) {
                println("Файл проверен: ${targetFile.name} (${targetFile.length()} байт)")
            } else {
                System.err.println("Файл поврежден или пуст: ${targetFile.name}")
                targetFile.delete()
            }
        }

        @Throws(IOException::class)
        private fun retrieveFileFromUrl(sourceUrl: String, destinationPath: String): Boolean {
            return try {
                val url = URL(sourceUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 30000

                if (connection.responseCode !in 200..299) {
                    System.err.println("HTTP ошибка ${connection.responseCode} для URL: $sourceUrl")
                    return false
                }

                connection.inputStream.use { inputStream ->
                    Channels.newChannel(inputStream).use { byteChannel ->
                        FileOutputStream(destinationPath).use { outputStream ->
                            outputStream.channel.transferFrom(byteChannel, 0, Long.MAX_VALUE)
                        }
                    }
                }
                true
            } catch (e: Exception) {
                File(destinationPath).takeIf { it.exists() }?.delete()
                false
            }
        }

        private fun displayDownloadResults(audioCount: Int, imageCount: Int) {
            println("\n=== Процесс загрузки завершен ===")
            val availableAudioFiles = getAvailableAudioFiles().size
            val availableImageFiles = File(IMAGES_DIRECTORY).listFiles()?.size ?: 0

            println("Запрошено аудиофайлов: $audioCount")
            println("Успешно загружено аудиофайлов: $availableAudioFiles")
            println("Запрошено изображений: $imageCount")
            println("Успешно загружено изображений: $availableImageFiles")

            if (availableAudioFiles == 0 && availableImageFiles == 0) {
                println("ВНИМАНИЕ: Не удалось загрузить ни одного файла!")
            } else if (availableAudioFiles == 0) {
                println("ПРЕДУПРЕЖДЕНИЕ: Аудиофайлы не загружены, но есть изображения")
            }

            println("Созданные связи: $trackImageMapping")
        }

        private fun initializeMediaPlayer() {
            val inputScanner = Scanner(System.`in`)
            var waitingCounter = 0
            val maxWaitCycles = 30

            while (true) {
                val availableTracks = getAvailableAudioFiles()

                if (availableTracks.isNotEmpty()) {
                    waitingCounter = 0
                    displayMediaSelection(availableTracks)

                    when (val userInput = inputScanner.nextLine().trim()) {
                        "0" -> break
                        "r" -> continue
                        else -> processUserSelection(userInput, availableTracks, inputScanner)
                    }
                } else {
                    if (isDownloadComplete.get()) {
                        if (hasSuccessfulDownloads.get()) {
                            println("\nЗагрузка завершена. Аудиофайлы не обнаружены в целевой директории.")
                        } else {
                            println("\nЗагрузка завершена. Не удалось загрузить ни одного аудиофайла.")
                        }
                        println("Нажмите Enter для выхода...")
                        inputScanner.nextLine()
                        break
                    } else {
                        waitingCounter++
                        if (waitingCounter >= maxWaitCycles) {
                            println("\nПревышено время ожидания загрузки файлов.")
                            println("Загрузка может быть завершена с ошибками.")
                            print("Продолжить ожидание? (y/n): ")
                            if (!inputScanner.nextLine().equals("y", ignoreCase = true)) {
                                break
                            }
                            waitingCounter = 0
                        } else {
                            println("\nАудиофайлы не обнаружены. Ожидание завершения загрузки... ($waitingCounter/$maxWaitCycles)")
                            Thread.sleep(2000)
                        }
                    }
                }
            }

            inputScanner.close()
        }

        private fun getAvailableAudioFiles(): List<File> {
            return File(MUSIC_DIRECTORY)
                .listFiles { _, name -> name.endsWith(".mp3", ignoreCase = true) }
                ?.filter { it.length() > 0 }
                ?.sortedBy { extractTrackIdentifier(it.name) }
                ?: emptyList()
        }

        private fun displayMediaSelection(audioFiles: List<File>) {
            println("\n=== Медиаплеер ===")
            println("Доступные аудиотреки:")

            audioFiles.forEachIndexed { index, file ->
                val trackId = extractTrackIdentifier(file.name)
                val trackDescription = buildString {
                    append("${index + 1} - ${file.name} [ID: $trackId]")
                    append(getAssociatedImageStatus(trackId))
                }
                println(trackDescription)
            }

            println("r - Обновить перечень")
            println("0 - Завершить работу")
            print("Укажите номер трека: ")
        }

        private fun getAssociatedImageStatus(trackId: Int): String {
            val imagePath = trackImageMapping[trackId]
            return when {
                imagePath == null -> " [изображение отсутствует]"
                File(imagePath).exists() && File(imagePath).length() > 0 -> " [изображение доступно]"
                else -> " [изображение недоступно]"
            }
        }

        private fun processUserSelection(input: String, audioFiles: List<File>, scanner: Scanner) {
            try {
                val selectedIndex = input.toInt()
                if (selectedIndex in 1..audioFiles.size) {
                    val chosenFile = audioFiles[selectedIndex - 1]
                    val trackId = extractTrackIdentifier(chosenFile.name)

                    launchAudioPlayer(chosenFile)
                    promptImageDisplay(trackId, scanner)

                    println("Нажмите Enter для продолжения...")
                    scanner.nextLine()
                } else {
                    println("Указан неверный номер трека!")
                }
            } catch (e: NumberFormatException) {
                println("Некорректный ввод! Требуется числовое значение.")
            }
        }

        private fun extractTrackIdentifier(fileName: String): Int {
            return fileName
                .removePrefix("audio_track_")
                .removeSuffix(".mp3")
                .toIntOrNull() ?: 0
        }

        private fun launchAudioPlayer(audioFile: File) {
            try {
                if (audioFile.exists() && audioFile.length() > 0) {
                    Runtime.getRuntime().exec(arrayOf("cmd", "/c", "start", "", audioFile.absolutePath))
                    println("Воспроизведение аудио: ${audioFile.name}")
                } else {
                    println("Ошибка: Аудиофайл поврежден или отсутствует")
                }
            } catch (e: Exception) {
                System.err.println("Ошибка запуска плеера: ${e.message}")
            }
        }

        private fun promptImageDisplay(trackId: Int, scanner: Scanner) {
            val imagePath = trackImageMapping[trackId] ?: run {
                println("Для трека $trackId связанное изображение не найдено")
                return
            }

            val imageFile = File(imagePath)
            if (!imageFile.exists() || imageFile.length() == 0L) {
                println("Связанное изображение недоступно: ${imageFile.name}")
                return
            }

            print("Открыть связанное изображение? (y/n): ")
            if (scanner.nextLine().equals("y", ignoreCase = true)) {
                displayImageFile(imageFile)
            }
        }

        private fun displayImageFile(imageFile: File) {
            try {
                Runtime.getRuntime().exec(arrayOf("cmd", "/c", "start", "", imageFile.absolutePath))
                println("Изображение открыто: ${imageFile.name}")
            } catch (e: Exception) {
                System.err.println("Ошибка открытия изображения: ${e.message}")
            }
        }
    }

    class AtomicBoolean(private var value: Boolean) {
        fun get(): Boolean = value
        fun set(newValue: Boolean) { value = newValue }
    }
}