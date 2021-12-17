package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.sp
import ru.emkn.kotlin.sms.ParticipantWithLiveResult
import ru.emkn.kotlin.sms.gui.frontend.FieldComparableBySelector
import ru.emkn.kotlin.sms.gui.frontend.ImmutableFoldingList
import ru.emkn.kotlin.sms.gui.frontend.SortableTable
import ru.emkn.kotlin.sms.gui.programState.OnGoingCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import java.awt.FileDialog
import java.io.File


@Composable
fun OnGoingCompetition(programState: MutableState<ProgramState>) {
    val state = programState.value as OnGoingCompetitionProgramState
    Column {
        DisplayResults(state)
        Button(onClick = {
            val files = openFileDialog("Load participant timestamps protocols")
                .map { it.path }
            state.competitionModelController
                .addTimestampsFromProtocolFilesByParticipant(files)
        }) { Text("Load participant timestamps protocols") }
        Button(onClick = {
            val files = openFileDialog("Load checkpoint timestamps protocols")
                .map { it.path }
            state.competitionModelController
                .addTimestampsFromProtocolFilesByCheckpoint(files)
        }) { Text("Load checkpoint timestamps protocols") }
        Button(
            onClick = {
                programState.value = programState.value.nextProgramState()
            },
            content = { Text("Сохранить и далее") },
        )
    }
}

fun openFileDialog(
    title: String,
    allowMultiSelection: Boolean = true
): Set<File> {
    return FileDialog(ComposeWindow(), title, FileDialog.LOAD).apply {
        isMultipleMode = allowMultiSelection
        isVisible = true
    }.files.toSet()
}

@Composable
fun DisplayResults(
    state: OnGoingCompetitionProgramState,
) {
    val liveResultProtocols = state.liveGroupResultProtocolsView.protocols
    ImmutableFoldingList(
        { Text("Результаты", fontSize = 20.sp) },
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
