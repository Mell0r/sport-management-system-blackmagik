package ru.emkn.kotlin.sms

import org.tinylog.Logger
import ru.emkn.kotlin.sms.time.Time

/**
 * Checks whether the given applicant can to participate in the chosen group.
 */
fun checkApplicant(applicant: Participant): Boolean {
    return applicant.group.checkParticipantValidity(applicant)
}

/**
 * Returns ParticipantsList by applicationFiles and competition, filtering them of wrong applications and applicants.
 */
fun getParticipantsListFromApplications(
    applications: List<Application>,
    competition: Competition
): ParticipantsList {
    val countSet = mutableSetOf<String>()
    applications.forEach {
        if (countSet.contains(it.teamName))
            throw IllegalArgumentException("Two different applications have same team name.")
        countSet.add(it.teamName)
    }

    val applicationsWithParticipants = applications.map { application ->
        application.applicantsList.map { applicant ->
            val group =
                competition.getGroupByLabelOrNull(applicant.supposedGroupLabel)
            if (group == null) {
                Logger.warn { "Applicant ${applicant.name} ${applicant.lastName} in team ${applicant.teamName} has invalid group label. Skipping." }
                null
            } else {
                Participant(
                    age = competition.year - applicant.birthYear,
                    name = applicant.name,
                    lastName = applicant.lastName,
                    group = group,
                    team = applicant.teamName,
                    sportsCategory = applicant.sportsCategory,
                )
            }
        }.filterNotNull()
    }

    applicationsWithParticipants.forEachIndexed { applicationInd, application ->
        application.forEachIndexed { applicantInd, applicant ->
            if (!checkApplicant(applicant)) {
                Logger.warn {
                    "Applicant number $applicantInd in $applicationInd application don't pass group requirement, " +
                            "so he/she is not allowed to competition."
                }
            }
        }
    }
    return ParticipantsList(applicationsWithParticipants.map { application ->
        application.filter { checkApplicant(it) }
    }.flatten())
}

/**
 * Generates ParticipantList and StartProtocols for all groups by applications and Competition.
 */
fun getStartConfigurationByApplications(
    applications: List<Application>,
    competition: Competition
): Pair<ParticipantsList, List<StartingProtocol>> {
    val participantsList =
        getParticipantsListFromApplications(applications, competition)

    var curMinutes = 0
    val startingProtocols = competition.groups.map { group ->
        StartingProtocol(
            group,
            participantsList.list.filter { it.group == group }
                .map {
                    StartingProtocolEntry(
                        it.id,
                        Time(12 * 3600 + (curMinutes++) * 60)
                    )
                })
    }
    return Pair(participantsList, startingProtocols)
}