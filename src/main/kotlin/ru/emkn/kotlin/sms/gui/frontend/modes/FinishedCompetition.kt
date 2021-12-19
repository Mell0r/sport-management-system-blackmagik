package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tinylog.kotlin.Logger
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
        Spacer(Modifier.width(16.dp))
        SaveTeamResultProtocolToCSVButton(state, errorMessage)

        val errorMessageFrozen = errorMessage.value
        if (errorMessageFrozen != null) {
            Text(errorMessageFrozen, fontSize = 15.sp, color = Color.Red)
        }
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
            Logger.debug { "Output folder for group result protocols: " +
                    "\"${outputDirectory.absolutePath}\"." }
            state.writeGroupResultProtocolsToCSV(outputDirectory)
            errorMessage.value = null
        }
    ) {
        Text("Сохранить протокoлы результатов по группам в CSV")
    }
}

@Composable
private fun SaveTeamResultProtocolToCSVButton(
    state: FinishedCompetitionProgramState,
    errorMessage: MutableState<String?>,
) {
    Button(
        onClick = {
            val files = saveFileDialog(
                title = "Выберите файл протокола результатов для команд",
                allowMultiSelection = false,
            )
            if (files.size != 1) {
                errorMessage.value = "Выберите ровно один файл!"
                return@Button
            }
            val file = files.single()
            Logger.debug { "Output file for team result protocol: " +
                    "\"${file.absolutePath}\"." }
            state.writeTeamResultsProtocolToCSV(file)
            errorMessage.value = null
        }
    ) {
        Text("Сохранить протокoл результатов по командам в CSV")
    }
}