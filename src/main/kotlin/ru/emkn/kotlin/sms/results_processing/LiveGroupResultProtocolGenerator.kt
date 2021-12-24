package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantsList

/**
 * Given [ParticipantCheckpointTime] triples,
 * turns them into [LiveGroupResultProtocol]s.
 */
class LiveGroupResultProtocolGenerator(
    private val participantsList: ParticipantsList,
) {
    fun generate(timestamps: List<ParticipantCheckpointTime>): List<LiveGroupResultProtocol> {
        val participantsByGroups =
            participantsList.list.groupBy { it.group }

        fun checkpointsOfParticipant(participant: Participant): List<CheckpointAndTime> {
            return timestamps.filter { it.participant === participant }
                .map { it.toCheckPointAndTime() }
        }

        val liveResultsWithinGroups =
            participantsByGroups.mapValues { (_, participants) ->
                participants.map { participant ->
                    val liveResult = participant.group.route.calculateLiveResult(
                        checkpointsToTimes = checkpointsOfParticipant(participant),
                        startingTime = participant.startingTime,
                    )
                    ParticipantWithLiveResult(participant, liveResult)
                }.sortedBy { it.liveResult }
            }

        return liveResultsWithinGroups.map { (group, participantsWithLiveResults) ->
            LiveGroupResultProtocol(group, participantsWithLiveResults)
        }
    }
}