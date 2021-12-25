package ru.emkn.kotlin.sms.csv

import ru.emkn.kotlin.sms.ResultOrMessage
import com.github.michaelbull.result.*
import ru.emkn.kotlin.sms.io.FileContent
import ru.emkn.kotlin.sms.io.FileParser

interface CreatableFromCsv<T> : FileParser<T> {
    /**
     * Tries to parse and object from CSV content.
     * Returns [Err] in case of invalid format.
     *
     * Is used instead of [parse] only to highlight that it reads from CSV.
     */
    fun readFromCsvContent(fileContent: FileContent): ResultOrMessage<T>

    override fun parse(fileContent: FileContent): ResultOrMessage<T> {
        return readFromCsvContent(fileContent)
    }
}