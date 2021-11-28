package ru.emkn.kotlin.sms

import org.tinylog.Logger
import ru.emkn.kotlin.sms.time.Time

const val SIZE_OF_APPLICATION_ROW = 5

/**
 * Checks whether the given application has incorrect team name, or it is empty,
 * or has different number of commas in different lines.
 */
fun checkApplicationFormat(application: List<List<String>>, number: Int) : Boolean {
    if (application.size < 2) {
        Logger.info {"Application number $number is empty or wrong, so it was skipped"}
        return false
    }
    if (application.any { it.size != SIZE_OF_APPLICATION_ROW }) {
        Logger.info {"Application number $number doesn't match the required format, so it was skipped"}
        return false
    }
    if (application[0][0] == "") {
        Logger.info {"Application number $number has empty team name, so it was skipped"}
        return false
    }
    return true
}

/**
 * Checks whether the given applicant can to participate in the chosen group.
 */
fun checkApplicant(applicant: Participant, competition: Competition) : Boolean {
    return competition.requirementByGroup[applicant.supposedGroup]?.checkApplicant(applicant.age) ?: false
}

/**
 * Returns ParticipantsList by applicationFiles and competition, filtering them of wrong applications and applicants.
 */
fun getParticipantsListFromApplications(applicationFiles : List<List<String>>, competition: Competition): ParticipantsList {
    val applications = applicationFiles.map { file -> file.map{ row -> row.split(",") } }
        .filterIndexed { ind, application -> checkApplicationFormat(application, ind) }
    val countSet = mutableSetOf<String>()
    applications.forEach {
        if(countSet.contains(it[0][0]))
            throw IllegalArgumentException("Two different applications have same team name.")
        countSet.add(it[0][0])
    }
    Logger.debug { "Filtered wrong applications" }


    var curId = 0
    val applicationsWithParticipants = applications.mapIndexed { applicationInd, application ->
        application.filterIndexed { applicantInd, applicant ->
            if (applicant[2].toIntOrNull() == null)
                Logger.warn { "Applicant number $applicantInd in has incorrect birth year, so he/she is not allowed to competition." }
            applicant[2].toIntOrNull() != null
        }.map { Participant(curId++, competition.year - it[2].toInt(), it[1], it[0], it[3], applications[applicationInd][0][0], it[4]) }
    }

    applicationsWithParticipants.forEachIndexed { applicationInd, application ->
        application.forEachIndexed { applicantInd, applicant ->
            if (!checkApplicant(applicant, competition)) {
                Logger.warn { "Applicant number $applicantInd in $applicationInd application don't pass group requirement, " +
                        "so he/she is not allowed to competition." }
            }
        }
    }
    return ParticipantsList(applicationsWithParticipants.map { application ->
        application.filter { checkApplicant(it, competition) } }.flatten())
}

/**
 * Generates ParticipantList and StartProtocols for all groups by applications and Competition.
 */
fun getStartConfigurationByApplications(applicationFiles: List<List<String>>, competition: Competition): Pair<ParticipantsList, List<StartingProtocol>> {
    val participantsList = getParticipantsListFromApplications(applicationFiles, competition)

    var curMinutes = 0
    val startingProtocols = competition.groups.map { group ->
        StartingProtocol(group,  participantsList.list.filter { it.supposedGroup == group }
            .map { StartingProtocolEntry(it.id, Time(12 * 3600 + (curMinutes++) * 60)) })
    }
    return Pair(participantsList, startingProtocols)
}