package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.time.Time

class Helper(
    private val participantsList: ParticipantsList,
    private val competitionConfig: Competition,
    private val startingProtocols: List<StartingProtocol>
) {
    fun getRouteOf(id: Int): Route {
        return competitionConfig.groupToRouteMapping[getGroupOf(id)]!!
    }

    fun getParticipantBy(id: Int): Participant {
        return participantsList.getParticipantById(id)!!
    }

    fun getGroupOf(id: Int): GroupLabelT {
        return getParticipantBy(id).supposedGroup
    }

    fun getStartingTimeOf(id: Int): Time =
        startingProtocols.flatMap { it.entries }
            .single { it.id == id }.startTime
}

/**
 * Part of API.
 */
fun generateResultsProtocolsFromParticipantTimestamps(
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
fun generateResultsProtocolsFromCheckpointTimestamps(
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
    val groupedByGroups = participantTimes.groupBy({ (participant, _) ->
        participant.supposedGroup
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
    groupLabel: GroupLabelT,
    groupResults: List<ParticipantAndTime>,
    idToParticipantMapping: (Int) -> Participant
): GroupResultProtocol {
    val participantsSorted =
        sortedGroupResultsForResultsTable(groupResults, idToParticipantMapping)
            .map { it.participant }
    return GroupResultProtocol(
        groupLabel,
        participantsSorted.map { participant ->
            ParticipantAndTime(
                participant,
                groupResults.single { it.participant.id == participant.id }.totalTime
            )
        })
}

private fun sortedGroupResultsForResultsTable(
    groupResults: List<ParticipantAndTime>,
    idToParticipantMapping: (Int) -> Participant
) =
    groupResults.filter { it.totalTime != null }
        .sortedBy { idToParticipantMapping(it.participant.id).lastName }
        .sortedBy { it.totalTime!! } +
            groupResults.filter { it.totalTime == null }
                .sortedBy { it.participant.lastName }

