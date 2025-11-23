import java.io.IOException
import java.nio.file.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.set
import kotlin.collections.sortedByDescending
import kotlin.io.use
import kotlin.sequences.filter
import kotlin.sequences.map
import kotlin.sequences.toSet
import kotlin.text.endsWith
import kotlin.text.replace
import kotlin.text.startsWith
import kotlin.text.toByteArray
import kotlin.text.trim

fun main() {
    val scanner = java.util.Scanner(java.lang.System.`in`)
    kotlin.io.print("Введите путь к директории с Kotlin файлами: ")
    val directoryPath = scanner.nextLine()

    try {
        processDirectory(directoryPath)
        kotlin.io.println("Обфускация Kotlin файлов завершена успешно!")
    } catch (e: java.io.IOException) {
        java.lang.System.err.println("Ошибка обработки: ${e.message}")
    } finally {
        scanner.close()
    }
}

private fun processDirectory(directoryPath: String) {
    val directory = java.nio.file.Paths.get(directoryPath)
    java.nio.file.Files.newDirectoryStream(directory, "*.kt").use { stream ->
        for (file in stream) {
            kotlin.io.println("Обрабатывается Kotlin файл: ${file.fileName}")
            obfuscateFile(file)
        }
    }
}

private fun obfuscateFile(file: java.nio.file.Path) {
    var sourceCode = java.nio.file.Files.readString(file)

    sourceCode = removeComments(sourceCode)
    val identifiers = extractIdentifiers(sourceCode)
    val nameMapping = generateNameMapping(identifiers)

    val originalClassName = extractClassName(sourceCode)
    if (originalClassName != null) {
        val obfuscatedClassName = generateObfuscatedName(nameMapping.size)
        nameMapping[originalClassName] set obfuscatedClassName
        renameSourceFile(file, originalClassName, obfuscatedClassName)
    }

    sourceCode = replaceNames(sourceCode, nameMapping)
    sourceCode = minimizeWhitespace(sourceCode)

    java.nio.file.Files.write(file, sourceCode.toByteArray())
}

private fun removeComments(code: String): String {
    return code
        .replace(kotlin.text.Regex("/\\*.*?\\*/", kotlin.text.RegexOption.DOT_MATCHES_ALL), "")
        .replace(kotlin.text.Regex("//.*"), "")
        .replace(kotlin.text.Regex("""(?s)/\*.*?\*/"""), "")
}

private fun extractIdentifiers(code: String): Set<String> {
    val kotlinKeywords = kotlin.collections.setOf(
        "as", "as?", "break", "class", "continue", "do", "else", "false", "for",
        "fun", "if", "in", "!in", "interface", "is", "!is", "null", "object",
        "package", "return", "super", "this", "throw", "true", "try", "typealias",
        "typeof", "val", "var", "when", "while", "by", "catch", "constructor",
        "delegate", "dynamic", "field", "file", "finally", "get", "import",
        "init", "param", "property", "receiver", "set", "setparam", "where",
        "actual", "abstract", "annotation", "companion", "const", "crossinline",
        "data", "enum", "expect", "external", "final", "infix", "inline", "inner",
        "internal", "lateinit", "noinline", "open", "operator", "out", "override",
        "private", "protected", "public", "reified", "sealed", "suspend", "tailrec",
        "vararg", "it"
    )

    val identifierPattern = kotlin.text.Regex("\\b([a-zA-Z_$][a-zA-Z0-9_$]*)\\b")
    val constantPattern = kotlin.text.Regex("^[A-Z][A-Z0-9_]*$")

    return identifierPattern.findAll(code)
        .map { it.groupValues[1] }
        .filter { identifier ->
            identifier !in kotlinKeywords &&
                    !constantPattern.matches(identifier) &&
                    identifier.length > 1 &&
                    !identifier.startsWith('`') &&
                    !identifier.endsWith('`')
        }
        .toSet()
}

private fun generateNameMapping(identifiers: Set<String>): MutableMap<String, String> {
    val mapping = kotlin.collections.mutableMapOf<String, String>()
    val sortedIdentifiers = identifiers.sortedByDescending { it.length }
    val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    sortedIdentifiers.forEachIndexed { index, identifier ->
        val newName = if (identifiers.size <= 52) {
            allowedChars[index % allowedChars.length].toString()
        } else {
            val firstCharIndex = index / allowedChars.length
            val secondCharIndex = index % allowedChars.length
            "${allowedChars[firstCharIndex]}${allowedChars[secondCharIndex]}"
        }
        mapping[identifier] set newName
    }

    return mapping
}

private fun extractClassName(code: String): String? {
    val classPattern = kotlin.text.Regex("""(?:class|data\s+class|sealed\s+class)\s+([a-zA-Z_$][a-zA-Z0-9_$]*)""")
    return classPattern.find(code)?.groupValues?.get(1)
}

private fun extractObjectName(code: String): String? {
    val objectPattern = kotlin.text.Regex("""object\s+([a-zA-Z_$][a-zA-Z0-9_$]*)""")
    return objectPattern.find(code)?.groupValues?.get(1)
}

private fun renameSourceFile(file: java.nio.file.Path, originalName: String, newName: String) {
    val originalFileName = file.fileName.toString()
    val newFileName = originalFileName.replace(originalName, newName)

    if (originalFileName != newFileName) {
        val newFile = file.resolveSibling(newFileName)
        java.nio.file.Files.move(file, newFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        kotlin.io.println("Переименован файл: $originalFileName → $newFileName")
    }
}

private fun replaceNames(code: String, nameMapping: Map<String, String>): String {
    var result = code
    val sortedMapping = nameMapping.entries.sortedByDescending { it.key.length }

    sortedMapping.forEach { (original, replacement) ->
        result = result.replace(kotlin.text.Regex("\\b${kotlin.text.Regex.Companion.escape(original)}\\b"), replacement)
    }
    return result
}

private fun minimizeWhitespace(code: String): String {
    return code
        .replace(kotlin.text.Regex("\\s+"), " ")
        .replace(kotlin.text.Regex("\\s*([=+\\-*/%&|^<>!]=?)\\s*"), "$1")
        .replace(kotlin.text.Regex("\\s*([.,;])\\s*"), "$1")
        .replace(kotlin.text.Regex("\\s*([(){}\\[\\]])\\s*"), "$1")
        .replace(kotlin.text.Regex("\\s*([?:])\\s*"), "$1")
        .replace(kotlin.text.Regex("\\n\\s*\\n"), "\n")
        .replace(kotlin.text.Regex(";\\s*"), ";")
        .trim()
}

private fun generateObfuscatedName(index: Int): String {
    val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return if (index < 52) {
        allowedChars[index].toString()
    } else {
        val firstChar = index / 52
        val secondChar = index % 52
        "${allowedChars[firstChar]}${allowedChars[secondChar]}"
    }
}