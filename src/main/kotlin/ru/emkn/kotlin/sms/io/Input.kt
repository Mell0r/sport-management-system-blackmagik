package ru.emkn.kotlin.sms.io

import org.tinylog.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File
import java.nio.file.Files

/**
 * Creates a directory by given filepath and all it's ancestors (if it doesn't exist already).
 * Returns a created directory as a [java.io.File].
 * If the specified file already exists and is not a directory, throws IllegalArgumentException
 */
fun ensureDirectory(directoryPath: String): File {
    val file = File(directoryPath)
    if (file.exists() && !file.isDirectory) {
        throw IllegalArgumentException("$directoryPath already exists and is not a directory!")
    }
    Files.createDirectories(file.toPath())
    return file
}


/**
 * Reads an entire [java.io.File].
 * Returns a [List] of [String], or null if it couldn't reach or read the file.
 */
fun readFileContentOrNull(file: File): FileContent? {
    if (!file.exists()) {
        Logger.info { "File \"$file\" doesn't exist." }
        return null
    }
    if (!file.canRead()) {
        Logger.info { "File \"$file\" cannot be read." }
        return null
    }
    return file.readLines()
}


/**
 * Tries to read content of all specified [java.io.File]'s.
 * If it cannot read a file, it reacts based on a function [reactionOnFailure].
 */
fun readAllReadableFiles(
    files: List<File>,
    reactionOnFailure: (File) -> Unit = {},
): List<List<String>> {
    return files.map { file ->
        file to readFileContentOrNull(file)
    }.mapNotNull { (file, content) ->
        if (content == null) {
            reactionOnFailure(file)
        }
        content
    }
}

/**
 * Tries to read content of all specified [java.io.File]'s.
 * If it cannot read a file, it reacts based on a function [reactionOnFailure].
 *
 * Returns pairs of type File -> FileContent
 */
private fun readAllReadableFilesPairFile(
    files: List<File>,
    reactionOnFailure: (File) -> Unit = {},
): List<Pair<File, FileContent>> {
    return files.map { file -> file to readFileContentOrNull(file) }
        .flatMap { (file, contentOrNull) ->
            if (contentOrNull == null) {
                reactionOnFailure(file)
                listOf()
            } else {
                listOf(Pair(file, contentOrNull))
            }
        }
}


/**
 * Tries to read content of specified [java.io.File] and parse the content with [parser].
 *
 * [parser] must throw [IllegalArgumentException] with corresponding message in the case,
 * when the file format is incorrect.
 *
 * If the file cannot be read, reacts based on [strategyOnReadFail] function.
 * If the file was read, but has incorrect format, reacts based on [strategyOnWrongFormat] function.
 */
fun <T> readAndParseFile(
    file: File,
    competition: Competition,
    parser: (FileContent, Competition) -> T,
    strategyOnReadFail: (File) -> Nothing,
    strategyOnWrongFormat: (File, IllegalArgumentException) -> Nothing,
): T {
    val content = readFileContentOrNull(file) ?: strategyOnReadFail(file)
    return try {
        parser(content, competition)
    } catch (e: IllegalArgumentException) {
        strategyOnWrongFormat(file, e)
    }
}


/**
 * Tries to read content of all specified [java.io.File]'s and parse the content of each file with [parser].
 *
 * [parser] must throw [IllegalArgumentException] with corresponding message in the case,
 * when the file format is incorrect.
 *
 * If a file cannot be read, reacts based on [strategyOnReadFail] function.
 * If a file was read, but has incorrect format, reacts based on [strategyOnWrongFormat] function.
 */
fun <T> readAndParseAllFiles(
    files: List<File>,
    competition: Competition,
    parser: (FileContent, Competition) -> T,
    strategyOnReadFail: (File) -> Unit = ::throwReadFailOnFile,
    strategyOnWrongFormat: (File, IllegalArgumentException) -> Unit = ::throwWrongFormatOnFile,
): List<T> {
    val filesWithContents =
        readAllReadableFilesPairFile(files, strategyOnReadFail)
    return filesWithContents.flatMap { (file, content) ->
        try {
            listOf(parser(content, competition))
        } catch (e: IllegalArgumentException) {
            strategyOnWrongFormat(file, e)
            listOf()
        }
    }
}

internal class ReadFailException(val file: File) : Exception(
    "Could not read file at \"${file.absolutePath}\"!"
)

private fun throwReadFailOnFile(file: File): Nothing {
    throw ReadFailException(file)
}

internal class WrongFormatException(val file: File, val illegalArgumentException: IllegalArgumentException) : Exception(
    "File at \"${file.absolutePath}\" has invalid format:\n" +
            illegalArgumentException.message
)

private fun throwWrongFormatOnFile(file: File, illegalArgumentException: IllegalArgumentException): Nothing {
    throw WrongFormatException(file, illegalArgumentException)
}