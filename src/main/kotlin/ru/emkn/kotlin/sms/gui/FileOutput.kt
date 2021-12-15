package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.CsvDumpable
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File

/**
 * Tries to write [content] to file at [filePath].
 * Creates all necessary parent directories.
 * Overwrites file if it already existed. Caller MUST confirm that user definitely wants to overwrite.
 *
 * @throws [Exception] in case any IO exception occurs.
 */
fun safeWriteFileContentToFile(content: FileContent, filePath: String) {
    TODO()
}

/**
 * Tries to write content of [dumpable] of to file at [filePath].
 * Creates all necessary parent directories.
 * Overwrites file if it already existed. Caller MUST confirm that user definitely wants to overwrite.
 *
 * @throws [Exception] in case any IO exception occurs.
 */
fun safeCSVDumpbaleToFile(dumpable: CsvDumpable, filePath: String) {
    safeWriteFileContentToFile(dumpable.dumpToCsv(), filePath)
}