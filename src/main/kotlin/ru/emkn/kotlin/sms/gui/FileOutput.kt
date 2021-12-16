package ru.emkn.kotlin.sms.gui

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.CsvDumpable
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File

/**
 * Tries to write [content] to file at [filePath].
 * Creates all necessary parent directories.
 * Overwrites file if it already existed. Caller MUST confirm that user definitely wants to overwrite.
 *
 * @return true if it successfully wrote, false if some exception occurred
 */
fun safeWriteFileContentToFile(content: FileContent, filePath: String) : Boolean {
    return try {
        val file = File(filePath)
        file.mkdirs()
        file.createNewFile()
        file.writeText(content.joinToString("\n"))
        true
    } catch (e: Exception) {
        Logger.error {
            "Could not write to file at \"$filePath\". Following exception occured:\n" +
            "${e.message}"
        }
        false
    }
}

/**
 * Tries to write content of [dumpable] of to file at [filePath].
 * Creates all necessary parent directories.
 * Overwrites file if it already existed. Caller MUST confirm that user definitely wants to overwrite.
 *
 * @return true if it successfully wrote, false if some exception occurred
 */
fun safeCSVDumpbaleToFile(dumpable: CsvDumpable, filePath: String) : Boolean {
    return safeWriteFileContentToFile(dumpable.dumpToCsv(), filePath)
}