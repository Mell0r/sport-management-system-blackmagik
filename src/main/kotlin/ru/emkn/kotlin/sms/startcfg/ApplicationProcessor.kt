package ru.emkn.kotlin.sms.startcfg

import org.tinylog.Logger
import ru.emkn.kotlin.sms.Competition

/**
 * An object who processes given applications (that already passed by the format),
 * filtering out applicants which cannot be assigned to the competition.
 */
class ApplicationProcessor(
    private val competition: Competition,
    private val applications: MutableList<Application> = mutableListOf(),
) {

    /**
     * From given [applications], filters all correct applicants,
     * which are allowed to participate in [competition].
     *
     * @throws [IllegalArgumentException] if data is not correct
     * (currently only when two teams have equal name).
     */
    fun process(): List<ProcessedApplicant> {
        ensureTeamNamesDistinction()
        return applications.flatMap { application ->
            application.applicantsList.mapNotNull { applicant ->
                val groupLabel = applicant.supposedGroupLabel
                val group = competition.getGroupByLabelOrNull(groupLabel)
                if (group == null) {
                    skipApplicant(applicant, reason = "Invalid group label \"$groupLabel\".")
                    null
                } else if (!group.checkApplicantValidity(applicant)) {
                    skipApplicant(applicant, reason = "Does not pass the requirement of group \"$groupLabel\".")
                    null
                } else {
                    ProcessedApplicant(
                        age = applicant.getAge(competition.year),
                        name = applicant.name,
                        lastName = applicant.lastName,
                        group = group,
                        team = applicant.teamName,
                        sportsCategory = applicant.sportsCategory,
                    )
                }
            }
        }
    }

    private fun skipApplicant(applicant: Applicant, reason: String) {
        with(applicant) {
            Logger.warn {
                "Applicant $name $lastName birth year $birthYear in team $teamName is skipped due to the following reason:\n$reason"
            }
        }
    }

    private fun ensureTeamNamesDistinction() {
        val teamNames = mutableSetOf<String>()
        applications.forEach {
            require(!teamNames.contains(it.teamName)) {
                "Two (or more) different applications have same team name ${it.teamName}."
            }
            teamNames.add(it.teamName)
        }
    }
}