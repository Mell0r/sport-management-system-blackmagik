package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.gui.frontend.openFileDialog
import ru.emkn.kotlin.sms.gui.frontend.pickFolderDialog
import ru.emkn.kotlin.sms.gui.frontend.saveFileDialog
import ru.emkn.kotlin.sms.gui.programState.FinishedCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState

@Composable
fun FinishedCompetition(programState: MutableState<ProgramState>) {
    val state = programState.value as? FinishedCompetitionProgramState ?: return
    Column {
        val errorMessage = remember { mutableStateOf<String?>(null) }
        SaveGroupResultProtocolsToCSVButton(state, errorMessage)
        //SaveTeamResultProtocolToCSVButton(state, errorMessage)
    }
}

@Composable
private fun SaveGroupResultProtocolsToCSVButton(
    state: FinishedCompetitionProgramState,
    errorMessage: MutableState<String?>,
) {
    Button(
        onClick = {
            val outputDirectory = pickFolderDialog()
            if (outputDirectory == null) {
                errorMessage.value = "Выберите ровно одну папку!"
                return@Button
            }
            Logger.debug {"Output folder for group result protocols: \"${outputDirectory.absolutePath}\"."}
            state.writeGroupResultProtocolsToCSV(outputDirectory)
            errorMessage.value = null
        }
    ) {
        Text("Сохранить протокoлы резльтатов по группам в CSV")
    }
}