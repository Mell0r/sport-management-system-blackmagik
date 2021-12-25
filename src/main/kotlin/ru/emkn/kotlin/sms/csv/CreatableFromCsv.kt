package ru.emkn.kotlin.sms.csv

import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.io.FileContent
import ru.emkn.kotlin.sms.io.FileParser

interface CreatableFromCsv<T> : FileParser<T> {
    /**
     * @throws [IllegalArgumentException] if the file had wrong format.
     */
    fun readFromCsvContent(fileContent: FileContent): ResultOrMessage<T>

    override fun parse(fileContent: FileContent): ResultOrMessage<T> {
        return readFromCsvContent(fileContent)
    }
}