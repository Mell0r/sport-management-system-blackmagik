package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.StartingProtocol

fun checkInputCorrectnessParticipantTimestamps(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    participantTimestampsProtocols: List<ParticipantTimestampsProtocol>,
    competitionConfig: Competition
) {
    checkInputCorrectness(
        participantsList,
        startingProtocols,
        competitionConfig
    )
    require(participantTimestampsProtocols.map { it.id }
        .toSet() == participantsList.list.toSet()) { "Route completion protocols should cover all ids." }
}

fun checkInputCorrectness(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    competitionConfig: Competition
) {
    val participantsFromStartingProtocols =
        startingProtocols.flatMap { it.entries }
            .map { it.id }
    require(participantsFromStartingProtocols.size == participantsFromStartingProtocols.distinct().size) { "Starting lists should not have repeated ids." }
    require(participantsList.list.size == participantsList.list.distinct().size) { "Participant lists should not have repeated ids." }
    require(participantsList.list.toSet() == participantsFromStartingProtocols.toSet())
    require(startingProtocols.map { it.group }
        .toSet() == competitionConfig.groups.toSet()) { "Starting protocols should cover all groups from competition." }
}