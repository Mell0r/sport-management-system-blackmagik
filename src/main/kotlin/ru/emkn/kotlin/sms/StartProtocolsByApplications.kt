package ru.emkn.kotlin.sms

import org.tinylog.Logger
import ru.emkn.kotlin.sms.time.Time

const val SIZE_OF_APPLICATION_ROW = 5

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

fun checkApplicant(applicant: Participant, competition: Competition) : Boolean {
    return competition.requirementByGroup[applicant.group]?.checkApplicant(applicant.age) ?: false
}

fun getParticipantsListFromApplications(applicationFiles : List<List<String>>, competition: Competition): ParticipantsList {
    val applications = applicationFiles.map { file -> file.map{ row -> row.split(",") } }
        .filterIndexed { ind, application -> checkApplicationFormat(application, ind) }
    Logger.debug { "Filtered wrong applications" }

    var curId = 0
    val applicationsWithParticipants = applications.mapIndexed { applicationInd, application ->
        application.map { Participant(curId++, it[2].toInt(), it[1], it[0], it[3], applications[applicationInd][0][0], it[4])} }

    applicationsWithParticipants.forEachIndexed { applicationInd, application ->
        application.forEachIndexed { applicantInd, applicant ->
            if (!checkApplicant(applicant, competition)) {
                Logger.warn { "Applicant number $applicantInd in $applicationInd application has wrong format, " +
                        "so he/she is not allowed to competition" }
            }
        }
    }
    return ParticipantsList(applicationsWithParticipants.map { application ->
        application.filter { checkApplicant(it, competition) } }.flatten())
}

fun getStartConfigurationByApplications(applicationFiles: List<List<String>>, competition: Competition) {
    val participantsList = getParticipantsListFromApplications(applicationFiles, competition)
   participantsList.print(TODO())

    var curMinutes = 0
    val startingProtocols = competition.groups.map { group ->
        StartingProtocol(group,  participantsList.list.filter { it.group == group }
            .map { ParticipantStart(it, Time(12 * 3600 + (curMinutes++) * 60)) })
    }
    startingProtocols.forEach { it.print(TODO()) }
}