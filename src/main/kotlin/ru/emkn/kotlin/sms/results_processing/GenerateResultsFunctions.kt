package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.time.Time

class Helper(
    private val participantsList: ParticipantsList,
    private val competitionConfig: Competition,
    private val startingProtocols: List<StartingProtocol>
) {
    fun getRouteOf(id: Int): Route {
        val group = getGroupOf(id)
        return group.route
    }

    fun getParticipantBy(id: Int): Participant {
        return participantsList.getParticipantById(id)
            ?: throw InternalError("Bad input checker: participant with id \"$id\" wasn't found!")
    }

    fun getGroupOf(id: Int): Group {
        return getParticipantBy(id).group
    }

    fun getStartingTimeOf(id: Int): Time =
        startingProtocols.flatMap { it.entries }
            .single { it.id == id }.startTime
}

/**
 * Part of API.
 */
fun generateResultsProtocolsOfParticipant(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    participantTimestampsProtocols: List<ParticipantTimestampsProtocol>,
    competitionConfig: Competition
): List<GroupResultProtocol> {
    checkInputCorrectnessParticipantTimestamps(
        participantsList,
        startingProtocols,
        participantTimestampsProtocols,
        competitionConfig
    )
    val helper = Helper(participantsList, competitionConfig, startingProtocols)
    val participantTimes =
        getParticipantsTimesFromParticipantTimestampsProtocols(
            participantTimestampsProtocols,
            helper
        )
    return convertToGroupResults(participantTimes, helper)
}

/**
 * Part of API.
 */
fun generateResultsProtocolsOfCheckpoint(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    checkpointTimestampsProtocols: List<CheckpointTimestampsProtocol>,
    competitionConfig: Competition
): List<GroupResultProtocol> {
    checkInputCorrectnessCheckpointTimestamps(
        participantsList,
        startingProtocols,
        checkpointTimestampsProtocols,
        competitionConfig
    )
    val helper = Helper(participantsList, competitionConfig, startingProtocols)
    val participantTimes =
        getParticipantsTimesFromCheckpointTimestampsProtocols(
            checkpointTimestampsProtocols,
            helper
        )
    return convertToGroupResults(participantTimes, helper)
}

private fun convertToGroupResults(
    participantTimes: List<ParticipantAndTime>,
    helper: Helper
): List<GroupResultProtocol> {
    val groupedByGroups = participantTimes.groupBy({ (id, _) ->
        helper.getGroupOf(id)
    }) { (participant, completionTime) ->
        ParticipantAndTime(participant, completionTime)
    }
    return groupedByGroups.map { (groupLabel, participantResults) ->
        generateResultProtocolWithinAGroup(
            groupLabel,
            participantResults
        ) { id -> helper.getParticipantBy(id) }
    }
}

private fun generateResultProtocolWithinAGroup(
    groupLabel: Group,
    groupResults: List<ParticipantAndTime>,
    idToParticipantMapping: (Int) -> Participant
): GroupResultProtocol {
    val idsSorted =
        sortedGroupResultsForResultsTable(groupResults, idToParticipantMapping)
            .map { it.id }
    return GroupResultProtocol(
        groupLabel,
        idsSorted.map { id ->
            ParticipantAndTime(
                id,
                groupResults.single { it.id == id }.totalTime
            )
        })
}

private fun sortedGroupResultsForResultsTable(
    groupResults: List<ParticipantAndTime>,
    idToParticipantMapping: (Int) -> Participant
) = groupResults
    .sortedBy { idToParticipantMapping(it.id).lastName }
    .sortedWith(
        compareBy(nullsLast()) { it.totalTime }
    )

