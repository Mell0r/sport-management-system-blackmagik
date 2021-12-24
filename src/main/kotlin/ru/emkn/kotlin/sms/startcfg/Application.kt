package ru.emkn.kotlin.sms.startcfg

import org.tinylog.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.csv.CreatableFromCsvAndCompetition
import ru.emkn.kotlin.sms.results_processing.FileContent

/**
 * An application for a single team.
 */
class Application(
    val teamName: String,
    val applicantsList: List<Applicant>,
) {
    companion object : CreatableFromCsvAndCompetition<Application> {
        private const val SIZE_OF_APPLICATION_ROW = 5

        private fun readApplicantFromLineOrNull(
            lineNo: Int,
            tokens: List<String>,
            teamName: String
        ): Applicant? {
            assert(tokens.size == 5)
            val birthYear = tokens[3].toIntOrNull()
            if (birthYear == null) {
                Logger.warn {
                    "Line $lineNo: The applicant has incorrect birth " +
                            "year, so he/she is not allowed to competition."
                }
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

        override fun readFromCsvContentAndCompetition(
            fileContent: FileContent,
            competition: Competition
        ): Application {
            val application = fileContent.map { row -> row.split(",") }
            require(application.size >= 2) { "Application can not be empty!" }
            require(application.all { it.size == SIZE_OF_APPLICATION_ROW }) { "Some line contains the wrong number of commas! Must be exactly ${SIZE_OF_APPLICATION_ROW - 1}." }
            require(application[0][0] != "") { "Application can not have empty team name!" }

            val teamName = application[0][0]
            val applicantsList = application
                .drop(1)
                .mapIndexedNotNull { index, tokens ->
                    val lineNo =
                        index + 2 // 2 = 1 for zero-based indexing + 1 for the first line being dropped
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