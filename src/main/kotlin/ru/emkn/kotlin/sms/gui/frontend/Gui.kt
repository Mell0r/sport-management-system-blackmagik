package ru.emkn.kotlin.sms.gui.frontend

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.application
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.gui.frontend.modes.*
import ru.emkn.kotlin.sms.gui.programState.*


fun getEmojiByUnicode(unicode: Int): String {
    return String(Character.toChars(unicode))
}

fun gui() {
    application {
        Logger.debug { "Program started." }
        val programState: MutableState<ProgramState> =
            remember { mutableStateOf(sampleOngoingCompetition) }
        when (programState.value) {
            is ConfiguringCompetitionProgramState ->
                Dialog(
                    title = "Настройка соревнования",
                    state = DialogState(size = DpSize(800.dp, 800.dp)),
                    onCloseRequest = ::exitApplication,
                    content = { CompetitionConfiguration(programState) })
            is FormingStartingProtocolsProgramState ->
                Dialog(onCloseRequest = ::exitApplication,
                    content = { FormingStartingProtocols(programState) })
            is OnGoingCompetitionProgramState ->
                Dialog(onCloseRequest = ::exitApplication,
                    state = DialogState(size = DpSize(800.dp, 800.dp)),
                    content = { OnGoingCompetition(programState) })
            is FinishedCompetitionProgramState ->
                Dialog(onCloseRequest = ::exitApplication,
                    content = { FinishedCompetition(programState) })
        }

        //Logger.debug { "Program successfully finished." }
    }
}

