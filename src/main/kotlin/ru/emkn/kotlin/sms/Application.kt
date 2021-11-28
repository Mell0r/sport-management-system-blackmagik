package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

// class for an application for a single team
class Application : CsvDumpable {
    companion object : CreatableFromFileContent<Application>{
        override fun readFromFileContent(fileContent: FileContent): Application {
            TODO("Not yet implemented")
        }

    }
    override fun dumpToCsv(): FileContent {
        TODO("Not yet implemented")
    }
}