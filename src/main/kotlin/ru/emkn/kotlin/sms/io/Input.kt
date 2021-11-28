package ru.emkn.kotlin.sms.io

import org.tinylog.Logger
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File
import java.nio.file.Files
import kotlin.IllegalArgumentException

/**
 * Creates a directory by given filepath and all it's ancestors (if it doesn't exist already).
 * Returns a created directory as a [java.io.File].
 * If the specified file already exists and is not a directory, throws IllegalArgumentException
 */
fun ensureDirectory(directoryPath: String) : File {
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
fun readFileContentOrNull(file: File) : List<String>? {
    if (!file.exists()) {
        Logger.info {"File \"$file\" doesn't exist."}
        return null
    }
    if (!file.canRead()) {
        Logger.info {"File \"$file\" cannot be read."}
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
) : List<List<String>> {
    return files.map { file ->
        file to readFileContentOrNull(file)
    }.mapNotNull { (file, content) ->
        if (content == null) {
            reactionOnFailure(file)
        }
        content
    }
}

private fun readAllReadableFilesPairFile(
    files: List<File>,
    reactionOnFailure: (File) -> Unit = {},
) : List<Pair<File, List<String>>> {
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

fun <T> readAndParseFile(
    file: File,
    parser: (FileContent) -> T,
    strategyIfCouldntRead: (File) -> Nothing,
    strategyOnWrongFormat: (File, IllegalArgumentException) -> Nothing,
) : T {
    val content = readFileContentOrNull(file) ?: strategyIfCouldntRead(file)
    return try {
        parser(content)
    } catch (e: IllegalArgumentException) {
        strategyOnWrongFormat(file, e)
    }
}

fun <T> readAndParseAllFiles(
    files: List<File>,
    parser: (FileContent) -> T,
    strategyIfCouldntRead: (File) -> Unit,
    strategyOnWrongFormat: (File, IllegalArgumentException) -> Unit,
) : List<T> {
    val filesWithContents = readAllReadableFilesPairFile(files, strategyIfCouldntRead)
    return filesWithContents.flatMap { (file, content) ->
        try {
            listOf(parser(content))
        } catch (e: IllegalArgumentException) {
            strategyOnWrongFormat(file, e)
            listOf()
        }
    }
}