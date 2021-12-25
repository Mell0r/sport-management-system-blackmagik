package ru.emkn.kotlin.sms.io

import com.github.michaelbull.result.*
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.successOrNothing
import ru.emkn.kotlin.sms.successOrNull
import java.io.File

/**
 * An object that can load objects of type [T] from a text file.
 */
interface FileParser<T> {
    /**
     * Tries to parse an object of type [T] from [fileContent].
     * The only abstract option in this interface.
     */
    fun parse(fileContent: FileContent): ResultOrMessage<T>

    /**
     * Tries to parse ALL file contents in [fileContentList].
     *
     * @returns [Err] if some file content has invalid format.
     */
    fun parseAll(fileContentList: List<FileContent>): ResultOrMessage<List<T>> {
        return binding {
            fileContentList.map { fileContent ->
                parse(fileContent).bind()
            }
        }
    }

    /**
     * Tries to parse file contents in [fileContentList].
     * If some file content has invalid format, it is skipped.
     */
    fun parseSome(
        fileContentList: List<FileContent>,
        strategy: () -> Unit
    ): List<T> {
        return fileContentList.flatMap { fileContent ->
            parse(fileContent).mapBoth(
                success = { listOf(it) },
                failure = { listOf() },
            )
        }
    }

    /**
     * Tries to read and parse a [file].
     */
    fun readAndParse(file: File): ResultOrMessage<T> {
        return readFile(file).andThen(::parse)
    }

    /**
     * Tries to read nad parse ALL [files].
     *
     * @returns [Err] if any of the files could not be read.
     */
    fun readAndParseAll(files: List<File>): ResultOrMessage<List<T>> {
        return binding {
            files.map { file ->
                readAndParse(file).bind()
            }
        }
    }

    /**
     * Tries to read ALL [files] and parse them.
     * If some file has invalid format,
     * then [strategyOnWrongFormat] is called with the corresponding file and message passed as an argument.
     *
     * @returns [Err] if any of the [files] could not be read.
     */
    fun readAllParseSome(
        files: List<File>,
        strategyOnWrongFormat: (File, String?) -> Unit = { _, _ -> }, // do nothing by default
    ): ResultOrMessage<List<T>> {
        val filesWithContent = files zip readAllFiles(files).successOrNothing {
            return Err(it)
        }
        val parsed = filesWithContent.mapNotNull { (file, content) ->
            parse(content).successOrNull { message ->
                strategyOnWrongFormat(file, message)
            }
        }
        return Ok(parsed)
    }
}