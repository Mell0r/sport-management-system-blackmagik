package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import ru.emkn.kotlin.sms.gui.programState.FormingStartingProtocolsProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState

@Composable
fun FormingStartingProtocols(programState: MutableState<ProgramState>) {
    val state = programState.value as? FormingStartingProtocolsProgramState ?: return
    state.participantsListBuilder
    TextField("", onValueChange = {}, isError = true, )
}