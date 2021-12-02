package ru.emkn.kotlin.sms

import org.tinylog.Logger
import ru.emkn.kotlin.sms.time.Time

/**
 * Checks whether the given applicant can to participate in the chosen group.
 */
fun checkApplicant(applicant: Participant, competition: Competition): Boolean {
    return competition.requirementByGroup[applicant.supposedGroup]?.checkApplicant(
        applicant.age
    ) ?: false
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

    var curId = 0
    val applicationsWithParticipants = applications.map { application ->
        application.applicantsList.map {
            Participant(
                curId++,
                competition.year - it[0].toInt(),
                it[1],
                it[2],
                it[3],
                it[4],
                it[5]
            )
        }
    }

    applicationsWithParticipants.forEachIndexed { applicationInd, application ->
        application.forEachIndexed { applicantInd, applicant ->
            if (!checkApplicant(applicant, competition)) {
                Logger.warn {
                    "Applicant number $applicantInd in $applicationInd application don't pass group requirement, " +
                            "so he/she is not allowed to competition."
                }
            }
        }
    }
    return ParticipantsList(applicationsWithParticipants.map { application ->
        application.filter { checkApplicant(it, competition) }
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
            participantsList.list.filter { it.supposedGroup == group }
                .map {
                    StartingProtocolEntry(
                        it.id,
                        Time(12 * 3600 + (curMinutes++) * 60)
                    )
                })
    }
    return Pair(participantsList, startingProtocols)
}