package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.ParticipantsList

/**
 * Given [ParticipantCheckpointTime] triples,
 * turns them into [GroupResultProtocol]s.
 */
class GroupResultProtocolGenerator(
    participantsList: ParticipantsList,
) {
    private val liveGenerator = LiveGroupResultProtocolGenerator(participantsList)

    fun generate(timestamps: List<ParticipantCheckpointTime>): List<GroupResultProtocol> {
        return liveGenerator.generate(timestamps).map { it.toGroupResultProtocol() }
    }
}