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
import ru.emkn.kotlin.sms.gui.frontend.elements.*
import ru.emkn.kotlin.sms.gui.programState.OnGoingCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState

private val errorDialogMessage: MutableState<String?> = mutableStateOf(null)
private val successDialogMessage: MutableState<String?> = mutableStateOf(null)

@Composable
fun OnGoingCompetition(programState: MutableState<ProgramState>) {
    val state = programState.value as? OnGoingCompetitionProgramState ?: return
    Column {
        DisplayResults(state)
        LoadParticipantsTimestampsButton(state)
        LoadCheckpointsTimestampsButton(state)
        Button(
            onClick = {
                programState.value = state.nextProgramState()
            },
            content = { Text("Сохранить и далее") },
        )
    }
    SuccessDialog(successDialogMessage)
    ErrorDialog(errorDialogMessage)
}

private fun loadParticipantsTimestamps(
    state: OnGoingCompetitionProgramState,
) {
    val files = openFileDialog("Загрузить протоколы прохождения участников") .map { it.path }
    state.competitionModelController
        .addTimestampsFromProtocolFilesByParticipant(files)
        .onSuccess {
            successDialogMessage.value = "Протоколы прохождения дистанции были успешно загружены!"
        }
        .onFailure { message ->
            errorDialogMessage.value = "Протоколы прохождения дистанции не были загружены! Произошла следующая ошибка.\n" +
                    message
        }
}

@Composable
private fun LoadParticipantsTimestampsButton(
    state: OnGoingCompetitionProgramState,
) {
    Button(onClick = { loadParticipantsTimestamps(state) }) {
        Text("Загрузить протоколы прохождения участников")
    }
}

private fun loadCheckpointTimestampsButton(
    state: OnGoingCompetitionProgramState,
) {
    val files = openFileDialog("Загрузить протоколы отметок на контрольных точках") .map { it.path }
    state.competitionModelController
        .addTimestampsFromProtocolFilesByCheckpoint(files)
        .onSuccess {
            successDialogMessage.value = "Протоколы прохождения дистанции были успешно загружены!"
        }
        .onFailure { message ->
            errorDialogMessage.value = "Протоколы прохождения дистанции не были загружены! Произошла следующая ошибка.\n" +
                    message
        }
}

@Composable
private fun LoadCheckpointsTimestampsButton(
    state: OnGoingCompetitionProgramState,
) {
    Button(onClick = { loadCheckpointTimestampsButton(state) }) {
        Text("Загрузить протоколы отметок на контрольных точках")
    }
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
                { (participant, _): ParticipantWithLiveResult -> "$participant" },
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
