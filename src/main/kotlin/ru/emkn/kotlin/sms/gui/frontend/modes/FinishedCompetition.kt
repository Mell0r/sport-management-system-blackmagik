@file:Suppress("FunctionName")

package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.gui.frontend.elements.pickFolderDialog
import ru.emkn.kotlin.sms.gui.frontend.elements.saveFileDialog
import ru.emkn.kotlin.sms.gui.programState.FinishedCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState

@Composable
fun FinishedCompetition(programState: MutableState<ProgramState>) {
    val state = programState.value as? FinishedCompetitionProgramState ?: return
    Column {
        ExportGroupResultProtocolsToCSVButton(state)
        Spacer(Modifier.width(16.dp))
        ExportTeamResultProtocolToCSVButton(state)
    }
}

private fun exportGroupResultProtocolsToCSV(
    state: FinishedCompetitionProgramState,
) {
    val outputDirectory = pickFolderDialog() ?: return
    Logger.debug {
        "Output folder for group result protocols: " +
                "\"${outputDirectory.absolutePath}\"."
    }
    state.writeGroupResultProtocolsToCSV(outputDirectory)
}

@Composable
private fun ExportGroupResultProtocolsToCSVButton(
    state: FinishedCompetitionProgramState,
) {
    Button(onClick = { exportGroupResultProtocolsToCSV(state) }) {
        Text("Сохранить протокoлы результатов по группам в CSV")
    }
}

private fun exportTeamResultsProtocolToCSV(
    state: FinishedCompetitionProgramState,
) {
    val files = saveFileDialog(
        title = "Выберите файл протокола результатов для команд",
        allowMultiSelection = false,
    )
    if (files.size != 1) {
        return
    }
    val file = files.single()

    Logger.debug {
        "Output file for team result protocol: " +
                "\"${file.absolutePath}\"."
    }
    state.writeTeamResultsProtocolToCSV(file)
}

@Composable
private fun ExportTeamResultProtocolToCSVButton(
    state: FinishedCompetitionProgramState,
) {
    Button(onClick = { exportTeamResultsProtocolToCSV(state) }) {
        Text("Сохранить протокoл результатов по командам в CSV")
    }
}