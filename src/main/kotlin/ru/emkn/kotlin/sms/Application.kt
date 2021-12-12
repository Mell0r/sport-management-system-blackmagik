package ru.emkn.kotlin.sms

import org.tinylog.Logger
import ru.emkn.kotlin.sms.results_processing.FileContent


const val SIZE_OF_APPLICATION_ROW = 5

data class Applicant(
    val supposedGroupLabel: String,
    val lastName: String,
    val name: String,
    val birthYear: Int,
    val teamName: String,
    val sportsCategory: String,
)

// class for an application for a single team
class Application(
    val teamName: String,
    val applicantsList: List<Applicant>,
) {
    companion object : CreatableFromFileContentAndCompetition<Application> {
        private fun readApplicantFromLineOrNull(lineNo: Int, tokens: List<String>, teamName: String) : Applicant? {
            assert(tokens.size == 5)
            val birthYear = tokens[3].toIntOrNull()
            if (birthYear == null) {
                Logger.warn { "Applicant at line $lineNo has incorrect birth year, so he/she is not allowed to competition." }
                return null
            }
            return Applicant(
                supposedGroupLabel = tokens[0],
                lastName = tokens[1],
                name = tokens[2],
                birthYear = birthYear,
                teamName = teamName,
                sportsCategory = tokens[4],
            )
        }

        override fun readFromFileContentAndCompetition(fileContent: FileContent, competition: Competition): Application {
            val application = fileContent.map { row -> row.split(",") }
            if (application.size < 2)
                throw IllegalArgumentException("Application can not be empty!")
            if (application.any { it.size != SIZE_OF_APPLICATION_ROW })
                throw IllegalArgumentException("Some line contains the wrong number of commas! Must be exactly $SIZE_OF_APPLICATION_ROW.")
            if (application[0][0] == "")
                throw IllegalArgumentException("Application can not have empty team name!")

            val teamName = application[0][0]
            val applicantsList = application
                .drop(1)
                .mapIndexedNotNull { lineNo, tokens ->
                    readApplicantFromLineOrNull(
                        lineNo,
                        tokens,
                        teamName
                    )
                }
            return Application(teamName, applicantsList)
        }
    }
}