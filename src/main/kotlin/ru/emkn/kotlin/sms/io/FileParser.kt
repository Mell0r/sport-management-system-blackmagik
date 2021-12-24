package ru.emkn.kotlin.sms.io

import com.github.michaelbull.result.*
import ru.emkn.kotlin.sms.ResultOrMessage
import java.io.File

interface FileParser<T> {
    fun parse(fileContent: FileContent): ResultOrMessage<T>
    fun parseAll(fileContentList: List<FileContent>): ResultOrMessage<List<T>> {
        return binding {
            fileContentList.map { fileContent ->
                parse(fileContent).bind()
            }
        }
    }
    fun readAndParse(file: File): ResultOrMessage<T> {
        return readFileContent(file).andThen(::parse)
    }
    fun readAndParseAll(files: List<File>): ResultOrMessage<List<T>> {
        return binding {
            files.map { file ->
                readAndParse(file).bind()
            }
        }
    }
}