package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.time.Time

private fun getRouteById(
    idToParticipantMapping: (Int) -> Participant,
    id: Int,
    competition: Competition
): Route {
    val group = idToParticipantMapping(
        id
    ).supposedGroup
    val route = competition.groupToRouteMapping[group]
    return route ?: logErrorAndThrow(
        "Group $group does not " +
                "have a route mapped to it"
    )
}

fun generateResultsProtocolsFromParticipantTimestamps(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    participantTimestampsProtocols: List<ParticipantTimestampsProtocol>,
    competitionConfig: Competition
): List<GroupResultProtocol> {
    val idToRoute = { id: Int ->
        competitionConfig.groupToRouteMapping[participantsList.getParticipantById(
            id
        )!!.supposedGroup]!!
    }
    val idToParticipantMapping =
        { id: Int -> participantsList.getParticipantById(id)!! }
    val idToStartingTime = { id: Int ->
        startingProtocols.flatMap { it.entries }
            .single { it.id == id }.startTime
    }
    val results =
        getParticipantsTimesFromParticipantTimestampsProtocols(
            participantTimestampsProtocols,
            idToRoute,
            idToStartingTime
        )
    val idToTimePairs = results.entries.toList()
    val groupedByGroups = idToTimePairs.groupBy({ (id, _) ->
        participantsList.getParticipantById(id)!!.supposedGroup
    }) { (id, completionTime) ->
        ParticipantResult(id,
            completionTime?.let { Time(it) })
    }
    return groupedByGroups.map { (groupLabel, participantResults) ->
        generateResultProtocolWithinAGroup(
            participantResults,
            idToParticipantMapping,
            groupLabel
        )
    }
}

private fun sortedGroupResultsForResultsTable(
    groupResults: List<ParticipantResult>,
    idToParticipantMapping: (Int) -> Participant
) =
    groupResults.filter { it.routeCompletionTime != null }
        .sortedBy { idToParticipantMapping(it.participantId).lastName }
        .sortedBy { it.routeCompletionTime!! } +
            groupResults.filter { it.routeCompletionTime == null }
                .sortedBy { idToParticipantMapping(it.participantId).lastName }


private fun generateResultProtocolWithinAGroup(
    groupResults: List<ParticipantResult>,
    idToParticipantMapping: (Int) -> Participant,
    groupLabel: GroupLabelT
): GroupResultProtocol {
    val idsSorted =
        sortedGroupResultsForResultsTable(groupResults, idToParticipantMapping)
            .map { it.participantId }
    var placeCounter = 1
    var cnt = 0
    return GroupResultProtocol(groupLabel, idsSorted.map { id ->
        GroupResultProtocolEntry(
            idToParticipantMapping(id),
            groupResults.single { it.participantId == id }.routeCompletionTime,
            cnt++
        )
    })
}


fun generateResultsProtocolsFromCheckpointTimestamps(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    checkpointTimestampsProtocols: List<CheckpointTimestampsProtocol>,
    competitionConfig: Competition
): List<GroupResultProtocol> {
    TODO("Not yet implemented")
}