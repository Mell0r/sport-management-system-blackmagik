package ru.emkn.kotlin.sms.gui.competitonModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ru.emkn.kotlin.sms.LiveGroupResultProtocol
import ru.emkn.kotlin.sms.LiveParticipantResult
import ru.emkn.kotlin.sms.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.ParticipantWithLiveResult
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.time.Time

class LiveGroupResultProtocolsView(
    private val state: ProgramState,
) : CompetitionModelListener {
    var protocols: MutableState<List<LiveGroupResultProtocol>> =
        mutableStateOf(listOf())

    override fun modelChanged(timestamps: List<ParticipantCheckpointTime>) {
        val byGroups = timestamps.groupBy { it.participant.group }
            .mapValues { (_, participantCheckpointTime) ->
                val subresults =
                    participantCheckpointTime.groupBy { it.participant }
                        .map { (participant, timestamps) ->
                            val startingTime = state.startingTimes.getStartingTimeOfOrNull(participant)
                            requireNotNull(startingTime)
                            val checkpointToTimePairs = timestamps.map { it.toCheckPointAndTime() }
                            val liveResult = participant.group.route.calculateLiveResult(
                                checkpointsToTimes = checkpointToTimePairs,
                                startingTime = startingTime,
                            )
                            ParticipantWithLiveResult(
                                participant,
                                liveResult,
                            )
                        }
                subresults
            }.toMutableMap()

        state.participantsList.list.groupBy { it.group }
            .forEach { (group, participants) ->
                if (!byGroups.containsKey(group))
                    byGroups[group] = participants.map {
                        ParticipantWithLiveResult(it, LiveParticipantResult.InProcess(0, Time(0)))
                    }
                else {
                    val participantsWithAtLeastOneTimestamp =
                        byGroups[group]?.toMutableList()
                            ?: throw InternalError("Broken check for key's existense")
                    for (participant in participants) {
                        if (participantsWithAtLeastOneTimestamp.firstOrNull { it.participant === participant } == null) {
                            participantsWithAtLeastOneTimestamp.add(
                                ParticipantWithLiveResult(participant, LiveParticipantResult.InProcess(0, Time(0)))
                            )
                        }
                    }
                    byGroups[group] = participantsWithAtLeastOneTimestamp
                }
            }

        protocols.value = byGroups.map { (group, participantsWithLiveResults) ->
            LiveGroupResultProtocol(group, participantsWithLiveResults)
        }
    }
}