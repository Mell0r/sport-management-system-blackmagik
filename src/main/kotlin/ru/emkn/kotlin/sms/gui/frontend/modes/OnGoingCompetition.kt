package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import ru.emkn.kotlin.sms.ParticipantWithLiveResult
import ru.emkn.kotlin.sms.gui.frontend.elements.FieldComparableBySelector
import ru.emkn.kotlin.sms.gui.frontend.elements.ImmutableFoldingList
import ru.emkn.kotlin.sms.gui.frontend.elements.SortableTable
import ru.emkn.kotlin.sms.gui.frontend.elements.openFileDialog
import ru.emkn.kotlin.sms.gui.programState.OnGoingCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState


@Composable
fun OnGoingCompetition(programState: MutableState<ProgramState>) {
    val state = programState.value as? OnGoingCompetitionProgramState ?: return
    Column {
        DisplayResults(state)

        val errorMessage = remember { mutableStateOf<String?>(null) }

        LoadParticipantsTimestampsButton(state, errorMessage)

        LoadCheckpointsTimestampsButton(state, errorMessage)

        Button(
            onClick = {
                programState.value = state.nextProgramState()
            },
            content = { Text("Сохранить и далее") },
        )
        val errorMessageFrozen = errorMessage.value
        if (errorMessageFrozen != null) {
            Text(errorMessageFrozen, fontSize = 15.sp, color = Color.Red)
        }
    }
}

@Composable
private fun LoadParticipantsTimestampsButton(
    state: OnGoingCompetitionProgramState,
    errorMessage: MutableState<String?>
) {
    Button(onClick = {
        val files = openFileDialog("Загрузить протоколы прохождения участников")
            .map { it.path }
        state.competitionModelController
            .addTimestampsFromProtocolFilesByParticipant(files)
            .onSuccess { errorMessage.value = null }
            .onFailure { errorMessage.value = it }
    }) { Text("Загрузить протоколы прохождения участников") }
}

@Composable
private fun LoadCheckpointsTimestampsButton(
    state: OnGoingCompetitionProgramState,
    errorMessage: MutableState<String?>
) {
    Button(onClick = {
        val files =
            openFileDialog("Загрузить протоколы отметок на контрольных точках")
                .map { it.path }
        state.competitionModelController
            .addTimestampsFromProtocolFilesByCheckpoint(files)
            .onSuccess { errorMessage.value = null }
            .onFailure { errorMessage.value = it }
    }) { Text("Загрузить протоколы отметок на контрольных точках") }
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
