package ru.emkn.kotlin.sms.results_processing

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList

fun checkInputCorrectnessParticipantTimestamps(
    participantsList: ParticipantsList,
    participantTimestampsProtocols: List<ParticipantTimestampsProtocol>,
    @Suppress("UNUSED_PARAMETER") competition: Competition,
) {
    checkInputCorrectness(participantsList)
    val allIdsCovered = participantTimestampsProtocols.map { it.id }
        .toSet() == participantsList.list.map { it.id }
        .toSet()
    if (!allIdsCovered) {
        Logger.warn {
            "Participant timestamps protocols should cover all ids." +
                    " People with their id missing in protocols will be disqualified."
        }
    }
    // TODO check for timestamp id correctness
}

fun checkInputCorrectnessCheckpointTimestamps(
    participantsList: ParticipantsList,
    checkpointTimestampsProtocol: List<CheckpointTimestampsProtocol>,
    competitionConfig: Competition
) {
    checkInputCorrectness(participantsList)
    val checkpointsUsedInCompetition = competitionConfig.routes
        .flatMap { it.checkpoints }.toSet()
    val checkpointsCoveredByProtocols =
        checkpointTimestampsProtocol.map { it.checkpointLabel }
    val uncoveredCheckpoints =
        checkpointsUsedInCompetition subtract checkpointsCoveredByProtocols.toSet()
    if (uncoveredCheckpoints.isNotEmpty()) {
        Logger.warn {
            "Some checkpoints were uncovered by the " +
                    "checkpoint protocols: $uncoveredCheckpoints;\n" +
                    "If this is not intended, consider rechecking your protocols."
        }
    }
    val coveredButNotExistingCheckpoints =
        checkpointsCoveredByProtocols.toSet() subtract checkpointsUsedInCompetition
    if (coveredButNotExistingCheckpoints.isNotEmpty()) {
        Logger.warn {
            "Some non-existing checkpoints were covered in " +
                    "protocols (maybe typo(s)?): $coveredButNotExistingCheckpoints"
        }
    }
    // TODO check for participants ID correctness
}


private fun checkInputCorrectness(
    participantsList: ParticipantsList,
) {
    require(participantsList.list.size == participantsList.list.distinct().size) { "Participant lists should not have repeated ids." }
}