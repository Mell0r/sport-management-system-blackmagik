package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantCheckpointTime
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
    state: OnGoingCompetitionProgramState
) {
    // the class below duplicates the LiveResult class, might be a good idea to refactor it.
    data class ParticipantIntermediateResult(
        val participant: Participant,
        val checkpointsPassed: Int,
        val time: Time? // null if false start or wrong checkpoints order
    )

    val byGroups = timestamps.groupBy { it.participant.group }
        .mapValues { (_, participantCheckpointTime) ->
            val subresults =
                participantCheckpointTime.groupBy { it.participant }
                    .map { (participant, timestamps) ->
                        val timeUsed =
                            Time(timestamps.maxOf { it.time } - state.startingTimes.getStartingTimeOf(
                                participant
                            )) // probably should be delegated to Route
                        ParticipantIntermediateResult(
                            participant,
                            timestamps.size,
                            timeUsed
                        )
                    }
            subresults
        }.toMutableMap()
    state.participantsList.list.groupBy { it.group }
        .forEach { (group, participants) ->
            if (!byGroups.containsKey(group))
                byGroups[group] = participants.map {
                    ParticipantIntermediateResult(it, 0, Time(0))
                }
            else {
                val participantsWithAtLeastOneTimestamp =
                    byGroups[group]?.toMutableList()
                        ?: throw InternalError("Broken check for key's existense")
                for (participant in participants) {
                    if (participantsWithAtLeastOneTimestamp.firstOrNull { it.participant === participant } == null) {
                        participantsWithAtLeastOneTimestamp.add(
                            ParticipantIntermediateResult(
                                participant,
                                0,
                                Time(0)
                            )
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
                { it: ParticipantIntermediateResult -> it.participant.toString() },
                { it.participant.toString() },
                200f
            )
            val checkpointNumberField = FieldComparableBySelector(
                "Пройдено кп",
                { it: ParticipantIntermediateResult -> it.checkpointsPassed.toString() },
                { -it.checkpointsPassed },
                200f
            )
            val timeField = FieldComparableBySelector(
                "Время прохождения",
                { it: ParticipantIntermediateResult -> it.time.toString() },
                { it.time ?: Time(Int.MAX_VALUE) },
                250f
            )
            // TODO : sort in result order
            Column {
                Text("Группа $group")
                SortableTable(
                    participants,
                    listOf(participantField, checkpointNumberField, timeField)
                )
            }
        })
}
