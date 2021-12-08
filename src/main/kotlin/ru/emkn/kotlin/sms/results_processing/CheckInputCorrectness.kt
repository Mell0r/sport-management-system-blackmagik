package ru.emkn.kotlin.sms.results_processing

import org.tinylog.kotlin.Logger
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
    val allIdsCovered = participantTimestampsProtocols.map { it.id }
        .toSet() == participantsList.list.map { it.id }
        .toSet()
    if (!allIdsCovered) {
        Logger.warn {
            "Participant timestamps protocols should cover all ids." +
                    " People with their id missing in protocols will be disqualified."
        }
    }
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
    val checkpointUsedInCompetition = competitionConfig.routes
        .flatMap { it.checkpoints }.toSet()
    val checkpointsCoveredByProtocols =
        checkpointTimestampsProtocol.map { it.checkpointLabel }
    val uncoveredCheckpoints =
        checkpointUsedInCompetition subtract checkpointsCoveredByProtocols.toSet()
    if (uncoveredCheckpoints.isNotEmpty()) {
        Logger.warn {
            "Some checkpoints were uncovered by the " +
                    "checkpoint protocols: $uncoveredCheckpoints;\n" +
                    "If this is not intended, consider rechecking your protocols."
        }
    }
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
    val allGroupsCovered = startingProtocols.map { it.group }
        .toSet() == competitionConfig.groups.toSet()
    if (!allGroupsCovered)
        Logger.warn { "Starting protocols should cover all groups from competition." }
}