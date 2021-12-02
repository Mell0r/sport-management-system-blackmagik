package ru.emkn.kotlin.sms.cli

import kotlinx.cli.ArgType
import org.tinylog.Logger
import java.io.File

/**
 * A custom [ArgType] object for [kotlinx.cli],
 * which parses a list of .csv files or folders,
 * separated by commas (with no space).
 *
 * If a folder is in the list, it is transformed
 * into all .csv files in that folder (and its sub-folders as well)
 *
 * All files, which don't have .csv extension, are skipped.
 */
object CsvFileListArgType : ArgType<List<File>>(true) {
    override val description =
        "{ A list of .csv files or folders, separated by commas. }"

    override fun convert(
        value: kotlin.String,
        name: kotlin.String
    ): List<File> {
        val files = value
            .split(',')
            .map { File(it) }
        val directories = files.filter { it.isDirectory }
        val filesInDirectories = directories
            .flatMap { it.walk() }
        val allFiles = (files + filesInDirectories).distinct()
        val (csvFiles, otherFiles) = allFiles.partition { it.extension == "csv" }
        otherFiles.forEach { file ->
            Logger.warn { "File $file does not have .csv extension. Skipping." }
        }
        return csvFiles
    }
}