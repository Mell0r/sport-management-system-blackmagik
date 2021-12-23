package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.startcfg.StartingProtocol
import ru.emkn.kotlin.sms.time.Time

/**
 * Part of API.
 */
fun generateResultsProtocolsOfParticipant(
    participantsList: ParticipantsList,
    participantTimestampsProtocols: List<ParticipantTimestampsProtocol>,
    competition: Competition,
): List<GroupResultProtocol> {
    checkInputCorrectnessParticipantTimestamps(
        participantsList,
        participantTimestampsProtocols,
        competition,
    )
    val participantTimes =
        getParticipantsTimesFromParticipantTimestampsProtocols(
            participantTimestampsProtocols,
            participantsList,
        )
    return convertToGroupResults(participantTimes, participantsList)
}

/**
 * Part of API.
 */
fun generateResultsProtocolsOfCheckpoint(
    participantsList: ParticipantsList,
    checkpointTimestampsProtocols: List<CheckpointTimestampsProtocol>,
    competitionConfig: Competition
): List<GroupResultProtocol> {
    checkInputCorrectnessCheckpointTimestamps(
        participantsList,
        checkpointTimestampsProtocols,
        competitionConfig,
    )
    val participantTimes =
        getParticipantsTimesFromCheckpointTimestampsProtocols(
            checkpointTimestampsProtocols,
            participantsList,
        )
    return convertToGroupResults(participantTimes, participantsList)
}

private fun convertToGroupResults(
    participantTimes: List<IdWithFinalResult>,
    participantsList: ParticipantsList,
): List<GroupResultProtocol> {
    val groupedByGroups = participantTimes.groupBy({ (id, _) ->
        val group = participantsList.getGroupOfId(id)
        requireNotNull(group) {
            "There is no participant with ID=$id"
        }
        group
    }) { (participant, completionTime) ->
        IdWithFinalResult(participant, completionTime)
    }
    return groupedByGroups.map { (group, participantResults) ->
        generateResultProtocolWithinAGroup(
            group,
            participantResults
        ) { id ->
            val participant = participantsList.getParticipantById(id)
            requireNotNull(participant) {
                "There is no participant with ID=$id"
            }
            participant
        }
    }
}

private fun generateResultProtocolWithinAGroup(
    groupLabel: Group,
    groupResults: List<IdWithFinalResult>,
    idToParticipantMapping: (Int) -> Participant
): GroupResultProtocol {
    val idsSorted =
        sortedGroupResultsForResultsTable(groupResults, idToParticipantMapping)
            .map { it.id }
    return GroupResultProtocol(
        groupLabel,
        idsSorted.map { id ->
            IdWithFinalResult(
                id,
                groupResults.single { it.id == id }.result
            )
        })
}

private fun sortedGroupResultsForResultsTable(
    groupResults: List<IdWithFinalResult>,
    idToParticipantMapping: (Int) -> Participant
) = groupResults
    .sortedBy { idToParticipantMapping(it.id).lastName }
    .sortedWith(
        compareBy(nullsLast()) { it.result }
    )

