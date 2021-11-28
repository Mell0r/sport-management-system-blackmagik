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
        .toSet() == participantsList.list.map { it.id }
        .toSet()) { "Participant timestamps protocols should cover all ids." }
}

fun checkInputCorrectnessCheckpointTimestamps(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    checkpointTimestampsProtocol: List<CheckpointTimestampsProtocol>,
    competitionConfig: Competition
) {
    checkInputCorrectness(
        participantsList,
        startingProtocols,
        competitionConfig
    )
    val allCheckpoints = competitionConfig.routes.flatMap { it.route }.toSet()
    require(allCheckpoints == checkpointTimestampsProtocol.map { it.checkpointLabel }
        .toSet()) { "Participant timestamps protocols should cover all checkpoints." }
}


private fun checkInputCorrectness(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    competitionConfig: Competition
) {
    val participantsFromStartingProtocols =
        startingProtocols.flatMap { it.entries }
            .map { it.id }
    require(participantsFromStartingProtocols.size == participantsFromStartingProtocols.distinct().size) { "Starting lists should not have repeated ids." }
    require(participantsList.list.size == participantsList.list.distinct().size) { "Participant lists should not have repeated ids." }
    require(startingProtocols.map { it.group }
        .toSet() == competitionConfig.groups.toSet()) { "Starting protocols should cover all groups from competition." }
}