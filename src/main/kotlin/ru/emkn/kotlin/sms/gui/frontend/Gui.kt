package ru.emkn.kotlin.sms.gui.frontend

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.application
import ru.emkn.kotlin.sms.gui.frontend.modes.CompetitionConfiguration
import ru.emkn.kotlin.sms.gui.frontend.modes.FinishedCompetition
import ru.emkn.kotlin.sms.gui.frontend.modes.FormingParticipantsList
import ru.emkn.kotlin.sms.gui.frontend.modes.OnGoingCompetition
import ru.emkn.kotlin.sms.gui.programState.*


fun getEmojiByUnicode(unicode: Int): String = String(Character.toChars(unicode))

fun launchGUI(
    initialProgramState: ProgramState = ConfiguringCompetitionProgramState(),
) {
    application {
        val programState: MutableState<ProgramState> =
            remember { mutableStateOf(initialProgramState) }
        when (programState.value) {
            is ConfiguringCompetitionProgramState -> {
                val windowSize = DpSize(1200.dp, 600.dp)
                Dialog(
                    title = "Настройка соревнования",
                    state = DialogState(size = windowSize),
                    onCloseRequest = ::exitApplication,
                    content = {
                        CompetitionConfiguration(programState, windowSize)
                    }
                )
            }
            is FormingParticipantsListProgramState -> Dialog(
                title = "Обработка заявок. Формирование стартовых протоколов",
                onCloseRequest = ::exitApplication,
                state = DialogState(size = DpSize(800.dp, 800.dp)),
                content = { FormingParticipantsList(programState) }
            )
            is OnGoingCompetitionProgramState -> Dialog(
                onCloseRequest = ::exitApplication,
                state = DialogState(size = DpSize(800.dp, 800.dp)),
                content = { OnGoingCompetition(programState) },
            )
            is FinishedCompetitionProgramState -> Dialog(
                title = "Соревнование завершено",
                onCloseRequest = ::exitApplication,
                state = DialogState(size = DpSize(600.dp, 200.dp)),
                content = { FinishedCompetition(programState) },
            )
        }
    }
}

