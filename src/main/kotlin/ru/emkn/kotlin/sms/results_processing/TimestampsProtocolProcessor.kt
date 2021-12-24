package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.ParticipantsList

/**
 * Given route completion protocols (either by a participant, or by a checkpoint),
 * converts them to a list of [ParticipantCheckpointTime] triples.
 */
class TimestampsProtocolProcessor(
    val participantsList: ParticipantsList,
) {
    /**
     * If a participant ID is invalid, returns empty list.
     */
    fun processByParticipant(protocol: ParticipantTimestampsProtocol): List<ParticipantCheckpointTime> {
        val participant = participantsList.getParticipantById(protocol.id)
            ?: return listOf()
        return protocol.checkpointTimes.map { checkpointAndTime ->
            ParticipantCheckpointTime(
                participant = participant,
                checkpoint = checkpointAndTime.checkpointLabel,
                time = checkpointAndTime.time,
            )
        }
    }

    /**
     * If some participants' ID is invalid, skips its protocol.
     */
    fun processByParticipant(protocols: List<ParticipantTimestampsProtocol>): List<ParticipantCheckpointTime> {
        return protocols.flatMap { processByParticipant(it) }
    }

    /**
     * If some participants' ID is invalid, skips the timestamp.
     */
    fun processByCheckpoint(protocol: CheckpointTimestampsProtocol): List<ParticipantCheckpointTime> {
        return protocol.participantTimes.mapNotNull { (participantID, time) ->
            val participant = participantsList.getParticipantById(participantID)
            if (participant == null) {
                null
            } else {
                ParticipantCheckpointTime(
                    participant = participant,
                    checkpoint = protocol.checkpointLabel,
                    time = time,
                )
            }
        }
    }

    /**
     * If some participants' ID is invalid, skips the timestamp.
     */
    fun processByCheckpoint(protocols: List<CheckpointTimestampsProtocol>): List<ParticipantCheckpointTime> {
        return protocols.flatMap { processByCheckpoint(it) }
    }
}