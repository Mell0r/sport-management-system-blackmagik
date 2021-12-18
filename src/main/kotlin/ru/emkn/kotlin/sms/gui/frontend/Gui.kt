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
import ru.emkn.kotlin.sms.gui.frontend.modes.CompetitionConfiguration
import ru.emkn.kotlin.sms.gui.frontend.modes.FinishedCompetition
import ru.emkn.kotlin.sms.gui.frontend.modes.FormingStartingProtocols
import ru.emkn.kotlin.sms.gui.frontend.modes.OnGoingCompetition
import ru.emkn.kotlin.sms.gui.programState.*
import javax.lang.model.element.Modifier


fun getEmojiByUnicode(unicode: Int): String {
    return String(Character.toChars(unicode))
}


fun gui() {
    application {
        Logger.debug { "Program started." }

        val programState: MutableState<ProgramState> =
            remember { mutableStateOf(ConfiguringCompetitionProgramState()) }
        when (programState.value) {
            is ConfiguringCompetitionProgramState ->
                Dialog(
                    title = "Настройка соревнования",
                    state = DialogState(size = DpSize(1000.dp, 600.dp)),
                    onCloseRequest = ::exitApplication,
                    content = { CompetitionConfiguration(programState, DpSize(1000.dp, 600.dp)) })
            is FormingStartingProtocolsProgramState ->
                Dialog(onCloseRequest = ::exitApplication,
                    content = { FormingStartingProtocols(programState) })
            is OnGoingCompetitionProgramState ->
                Dialog(onCloseRequest = ::exitApplication,
                    content = { OnGoingCompetition(programState) })
            is FinishedCompetitionProgramState ->
                Dialog(onCloseRequest = ::exitApplication,
                    content = { FinishedCompetition(programState) })
        }

        //Logger.debug { "Program successfully finished." }
    }
}

