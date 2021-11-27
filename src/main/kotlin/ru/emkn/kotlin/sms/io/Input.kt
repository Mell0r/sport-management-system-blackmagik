package ru.emkn.kotlin.sms.io

import org.tinylog.Logger
import java.io.File
import java.nio.file.Files

/**
 * Creates a directory by given filepath and all it's ancestors (if it doesn't exist already).
 * Returns a created directory as a java.io.File.
 * If the specified file already exists and is not a directory, returns null.
 */
private fun ensureDirectory(directoryPath: String) : File? {
    val file = File(directoryPath)
    if (file.exists() && !file.isDirectory) {
        Logger.error {"$directoryPath is not a directory!"}
        return null
    }
    Files.createDirectories(file.toPath())
    return file
}

