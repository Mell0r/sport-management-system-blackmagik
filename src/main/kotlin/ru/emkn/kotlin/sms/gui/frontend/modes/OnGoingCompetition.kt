package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import ru.emkn.kotlin.sms.LiveParticipantResult
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.ParticipantWithLiveResult
import ru.emkn.kotlin.sms.gui.frontend.FieldComparableBySelector
import ru.emkn.kotlin.sms.gui.frontend.ImmutableFoldingList
import ru.emkn.kotlin.sms.gui.frontend.SortableTable
import ru.emkn.kotlin.sms.gui.programState.OnGoingCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.time.Time

@Composable
fun OnGoingCompetition(programState: MutableState<ProgramState>) {
    val state = programState.value as OnGoingCompetitionProgramState

    DisplayResults(state.competitionModel.timestamps, state)
}

@Composable
fun DisplayResults(
    timestamps: MutableList<ParticipantCheckpointTime>,
    state: OnGoingCompetitionProgramState,
) {

    val byGroups = timestamps.groupBy { it.participant.group }
        .mapValues { (_, participantCheckpointTime) ->
            val subresults =
                participantCheckpointTime.groupBy { it.participant }
                    .map { (participant, timestamps) ->
                        val startingTime = state.startingTimes.getStartingTimeOf(participant)
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
    ImmutableFoldingList(
        { Text("Result") },
        byGroups.toList(),
        @Composable { (group, participants) ->
            val participantField = FieldComparableBySelector(
                "Участник",
                { it: ParticipantWithLiveResult -> it.participant.toString() },
                { it.participant.toString() },
                200f
            )
            val liveResultField = FieldComparableBySelector(
                name = "Результат",
                stringRepresentation = { it: ParticipantWithLiveResult ->
                    it.liveResult.toString()
                },
                { it.liveResult },
                width = 400f
            )
            Column {
                Text("Группа $group")
                SortableTable(
                    participants,
                    listOf(participantField, liveResultField)
                )
            }
        })
}
