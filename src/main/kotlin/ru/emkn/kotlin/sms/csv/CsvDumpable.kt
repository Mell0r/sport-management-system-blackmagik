package ru.emkn.kotlin.sms.csv

import ru.emkn.kotlin.sms.io.FileContent

interface CsvDumpable {
    fun dumpToCsv(): FileContent
    fun defaultCsvFileName(): String
}