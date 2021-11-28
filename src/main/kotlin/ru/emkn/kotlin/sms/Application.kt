package ru.emkn.kotlin.sms

import org.tinylog.Logger
import ru.emkn.kotlin.sms.results_processing.FileContent


const val SIZE_OF_APPLICATION_ROW = 5

// class for an application for a single team
class Application(val teamName : String, val applicantsList: List<List<String>>) : CsvDumpable {
    companion object : CreatableFromFileContent<Application>{
        override fun readFromFileContent(fileContent: FileContent): Application {
            val application = fileContent.map{ row -> row.split(",") }
            if (application.size < 2)
                throw IllegalArgumentException("Application can not be empty!")
            if (application.any { it.size != SIZE_OF_APPLICATION_ROW })
                throw IllegalArgumentException("Some line contains the wrong number of commas! Must be exactly $SIZE_OF_APPLICATION_ROW.")
            if (application[0][0] == "")
                throw IllegalArgumentException("Application can not have empty team name!")

            val teamName = application[0][0]
            val applicantsList = application.drop(1).filterIndexed { applicantInd, applicant ->
                if (applicant[2].toIntOrNull() == null)
                    Logger.warn { "Applicant number $applicantInd has incorrect birth year, so he/she is not allowed to competition." }
                applicant[2].toIntOrNull() != null
            }.map { listOf(it[2], it[1], it[0], it[3], teamName, it[4]) }
            return Application(teamName, applicantsList)
        }
    }
    override fun dumpToCsv() = listOf("$teamName,,,,") + applicantsList.map { it.joinToString(",") }
}