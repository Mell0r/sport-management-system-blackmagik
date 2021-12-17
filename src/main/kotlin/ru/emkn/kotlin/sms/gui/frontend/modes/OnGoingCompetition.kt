package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import ru.emkn.kotlin.sms.ParticipantWithLiveResult
import ru.emkn.kotlin.sms.gui.frontend.FieldComparableBySelector
import ru.emkn.kotlin.sms.gui.frontend.ImmutableFoldingList
import ru.emkn.kotlin.sms.gui.frontend.SortableTable
import ru.emkn.kotlin.sms.gui.programState.OnGoingCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState

@Composable
fun OnGoingCompetition(programState: MutableState<ProgramState>) {
    val state = programState.value as OnGoingCompetitionProgramState
    DisplayResults(state)
}

@Composable
fun DisplayResults(
    state: OnGoingCompetitionProgramState,
) {
    val liveResultProtocols = state.liveGroupResultProtocolsView.protocols

    ImmutableFoldingList(
        { Text("Result") },
        liveResultProtocols.value,
        @Composable { liveResultProtocol ->
            val group = liveResultProtocol.group
            val participantsWithLiveResults = liveResultProtocol.entries
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
                    participantsWithLiveResults,
                    listOf(participantField, liveResultField)
                )
            }
        })
}
