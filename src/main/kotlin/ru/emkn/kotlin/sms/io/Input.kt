package ru.emkn.kotlin.sms.io

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.binding
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.runIoOperation
import java.io.File
import java.nio.file.Files

typealias FileContent = List<String>

/**
 * Creates a directory by given filepath and all it's ancestors (if it doesn't exist already).
 * Returns a created directory as a [java.io.File].
 * If the specified file already exists and is not a directory, throws IllegalArgumentException
 */
fun ensureDirectory(directoryPath: String): ResultOrMessage<File> {
    val file = File(directoryPath)
    if (file.exists() && !file.isDirectory) {
        return Err("$directoryPath already exists and is not a directory!")
    }
    return runIoOperation {
        Files.createDirectories(file.toPath())
        file
    }
}

/**
 * Tries to read a [file] into [FileContent] (a list of strings).
 */
fun readFile(file: File): ResultOrMessage<FileContent> {
    if (!file.exists()) {
        return Err("File \"$file\" doesn't exist.")
    }
    if (!file.canRead()) {
        return Err("File \"$file\" cannot be read.")
    }
    return runIoOperation { file.readLines() }
}


/**
 * Tries to read ALL [files] into [FileContent] (a list of strings).
 * @returns [Err] if any of the files could not have been read.
 */
fun readAllFiles(files: List<File>): ResultOrMessage<List<FileContent>> {
    return binding {
        files.map { file ->
            readFile(file).bind()
        }
    }
}