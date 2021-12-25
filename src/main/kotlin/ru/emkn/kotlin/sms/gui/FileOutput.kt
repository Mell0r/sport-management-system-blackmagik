package ru.emkn.kotlin.sms.gui

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.csv.CsvDumpable
import ru.emkn.kotlin.sms.io.FileContent
import java.io.File

/**
 * Tries to write [content] to file at [filePath].
 * Creates all necessary parent directories.
 * Overwrites file if it already existed. Caller MUST confirm that user definitely wants to overwrite.
 *
 * @return true if it successfully wrote, false if some exception occurred
 */
fun safeWriteFileContentToFile(
    content: FileContent,
    filePath: String
): Boolean {
    return try {
        val file = File(filePath)
        File(file.parent).mkdirs()
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
 * Tries to write content of [dumpable] to file at [filePath].
 * Creates all necessary parent directories.
 * Overwrites file if it already existed. Caller MUST confirm that user definitely wants to overwrite.
 *
 * @return true if it successfully wrote, false if some exception occurred
 */
fun safeCSVDumpableToFile(dumpable: CsvDumpable, filePath: String): Boolean {
    return safeWriteFileContentToFile(dumpable.dumpToCsv(), filePath)
}

fun getDefaultCSVDumpablePathInDir(
    dumpable: CsvDumpable,
    outputDirectory: File
) =
    "${outputDirectory.absolutePath}/${dumpable.defaultCsvFileName()}"

/**
 * Tries to write content of [dumpables] to files with corresponding default filenames in [outputDirectory].
 * Creates all necessary parent directories.
 * Overwrites file if it already existed. Caller MUST confirm that user definitely wants to overwrite.
 */
fun writeCSVDumpablesToDirectory(
    dumpables: List<CsvDumpable>,
    outputDirectory: File
) {
    dumpables.forEach { dumpable ->
        safeCSVDumpableToFile(
            dumpable = dumpable,
            filePath = getDefaultCSVDumpablePathInDir(
                dumpable,
                outputDirectory
            ),
        )
    }
}