package ru.emkn.kotlin.sms.gui.competitonModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.gui.programState.ProgramState

class LiveGroupResultProtocolsView(
    private val state: ProgramState,
) : CompetitionModelListener {
    var protocols: MutableState<List<LiveGroupResultProtocol>> = mutableStateOf(listOf())

    override fun modelChanged(timestamps: List<ParticipantCheckpointTime>) {
        val participantsByGroups =
            state.participantsList.list.groupBy { it.group }

        fun checkpointsOfParticipant(participant: Participant): List<CheckpointAndTime> {
            return timestamps.filter { it.participant === participant }
                .map { it.toCheckPointAndTime() }
        }

        val liveResultsWithinGroups =
            participantsByGroups.mapValues { (_, participants) ->
                participants.map {
                    val startingTimeOfParticipantOrNull =
                        state.startingTimes.getStartingTimeOfOrNull(it)
                    val liveResult = it.group.route.calculateLiveResult(
                        checkpointsToTimes = checkpointsOfParticipant(it),
                        startingTime = startingTimeOfParticipantOrNull
                            ?: throw InternalError("No starting time for participant $it")
                    )
                    ParticipantWithLiveResult(it, liveResult)
                }.sortedBy { it.liveResult }
            }

        protocols.value =
            liveResultsWithinGroups.map { (group, participantsWithLiveResults) ->
                LiveGroupResultProtocol(group, participantsWithLiveResults)
            }
    }

    fun getGroupResultProtocols(): List<GroupResultProtocol> =
        protocols.value.map { it.toGroupResultProtocol() }
}