package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

interface CsvDumpable {
    fun dumpToCsv(): FileContent
}

interface CreatableFromFileContent<T> {
    fun readFromFileContent(fileContent: FileContent): T
}