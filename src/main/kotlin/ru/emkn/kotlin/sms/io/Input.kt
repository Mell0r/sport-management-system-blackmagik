package ru.emkn.kotlin.sms.io

import org.tinylog.Logger
import java.io.File
import java.nio.file.Files

/**
 * Creates a directory by given filepath and all it's ancestors (if it doesn't exist already).
 * Returns a created directory as a [java.io.File].
 * If the specified file already exists and is not a directory, returns null.
 */
fun ensureDirectory(directoryPath: String) : File? {
    val file = File(directoryPath)
    if (file.exists() && !file.isDirectory) {
        Logger.error {"$directoryPath already exists and is not a directory!"}
        return null
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