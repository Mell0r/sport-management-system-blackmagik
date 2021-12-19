package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.michaelbull.result.*
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Application
import ru.emkn.kotlin.sms.getStartConfigurationByApplications
import ru.emkn.kotlin.sms.gui.builders.ApplicantBuilder
import ru.emkn.kotlin.sms.gui.builders.ApplicationBuilder
import ru.emkn.kotlin.sms.gui.builders.ParticipantsListBuilder
import ru.emkn.kotlin.sms.gui.frontend.elements.*
import ru.emkn.kotlin.sms.gui.programState.FormingStartingProtocolsProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.io.*
import java.io.File

private val errorDialogMessage: MutableState<String?> = mutableStateOf(null)
private val successDialogMessage: MutableState<String?> = mutableStateOf(null)

@Composable
fun FormingStartingProtocols(programState: MutableState<ProgramState>) {
    val state =
        programState.value as? FormingStartingProtocolsProgramState ?: return
    val applicationBuilders =
        remember { mutableStateListOf<ApplicationBuilder>() }

    val majorListsFontSize = 25.sp
    Column {
        FoldingList(
            {
                Text(
                    text = "Заявки",
                    modifier = Modifier.width(150.dp),
                    textAlign = TextAlign.Center,
                    fontSize = majorListsFontSize,
                )
            },
            applicationBuilders,
            { applicationBuilder ->
                DisplayApplication(state, applicationBuilder)
            },
            { ApplicationBuilder() },
            majorListsFontSize
        )

        LoadApplicationsFromCSVButton(state, applicationBuilders)
        LoadReadyStartingConfigurationButton(programState, state)
        SaveAndNextButton(
            programState,
            state,
            applicationBuilders,
        )

        SuccessDialog(successDialogMessage)
        ErrorDialog(errorDialogMessage)
    }

}

fun safeOpenSingleFileOrNull(title: String): File? {
    val files = openFileDialog(title, false)
    if (files.size != 1) {
        Logger.info { "User did not select exactly one file." }
        if (files.size > 1) {
            // User probably did something wrong, open failure window
            errorDialogMessage.value = "Please select exactly one file."
        }
        return null
    }
    return files.single()
}


private fun loadReadyStartingConfiguration(
    programState: MutableState<ProgramState>,
    state: FormingStartingProtocolsProgramState,
) {
    Logger.debug { "User pressed load ready start configuration." }

    val participantsListFile = safeOpenSingleFileOrNull("Выберите список участников (participants-list.csv)")
        ?: return
    val startingProtocolFiles = openFileDialog(
        title = "Выберите файлы стартовых протоколов",
        allowMultiSelection = true,
    ).toList()

    val participantsListBuilder = ParticipantsListBuilder.fromFileAndCompetition(
        filePath = participantsListFile.absolutePath,
        competition = state.competition,
    ).mapBoth(
        success = { it },
        failure = { errorMessage ->
            errorDialogMessage.value = errorMessage
            return
        },
    )

    // successfully read participants list and starting protocols
    state.participantsListBuilder.replaceFromParticipantsListBuilder(participantsListBuilder)
    state.startingTimes.replaceFromStartingProtocolFilesAndParticipantsList(
        files = startingProtocolFiles,
        competition = state.competition,
        participantsList = state.participantsList,
    ).mapBoth(
        success = {},
        failure = { errorMessage ->
            errorDialogMessage.value = errorMessage
            return
        },
    )

    programState.value = state.nextProgramState()
}

@Composable
private fun LoadReadyStartingConfigurationButton(
    programState: MutableState<ProgramState>,
    state: FormingStartingProtocolsProgramState,
) {
    Button(
        onClick = { loadReadyStartingConfiguration(programState, state) },
        content = { Text(text = "Загрузить готовые список учасников и стартовые протоколы из CSV и перейти далее.") },
    )
}

private fun loadApplicationsFromCSV(
    state: FormingStartingProtocolsProgramState,
    applicationBuilders: SnapshotStateList<ApplicationBuilder>,
) {
    val files = openFileDialog(
        title = "Загрузить заявки из CSV",
        allowMultiSelection = true
    ).toList()
    if (files.isEmpty()) return

    val applications = readAndParseAllFilesOrErrorMessage(
        files = files,
        competition = state.competition,
        parser = Application::readFromFileContentAndCompetition,
    ).mapBoth(
        success = { it },
        failure = { errorMessage ->
            errorDialogMessage.value = errorMessage
            return
        }
    )
    // add all applications
    applicationBuilders.addAll(
        applications.map(ApplicationBuilder::fromApplication)
    )

    successDialogMessage.value = "Все заявки были успешно загружены!"
}

@Composable
private fun LoadApplicationsFromCSVButton(
    state: FormingStartingProtocolsProgramState,
    applicationBuilders: SnapshotStateList<ApplicationBuilder>,
) {
    Button(
        onClick = { loadApplicationsFromCSV(state, applicationBuilders) },
        content = { Text(text = "Загрузить заявки из CSV") },
    )
}

private fun saveAndNext(
    programState: MutableState<ProgramState>,
    state: FormingStartingProtocolsProgramState,
    applications: SnapshotStateList<ApplicationBuilder>,
) {
    // form applications
    // if something went wrong, do not succeed to the next mode
    val applicationBuilders = applications.toList()
    val actualApplications = try {
        applicationBuilders.map { it.build() }
    } catch (e: IllegalArgumentException) {
        Logger.error { "Could not form applications, following exception occurred:\n${e.message}" }
        errorDialogMessage.value = e.message
        return
    }

    val (participantsList, startingProtocols) = try {
        getStartConfigurationByApplications(
            applications = actualApplications,
            competition = state.competition,
        )
    } catch (e: IllegalArgumentException) {
        Logger.error { "Could not form starting configuration, following exception occurred:\n${e.message}" }
        errorDialogMessage.value = e.message
        return
    }

    // form participant list and starting times
    state.participantsListBuilder.replaceFromParticipantsList(
        participantsList
    )
    state.startingTimes.replaceFromStartingProtocolsAndParticipantsList(
        startingProtocols,
        participantsList
    )
    programState.value = state.nextProgramState()
}

@Composable
private fun SaveAndNextButton(
    programState: MutableState<ProgramState>,
    state: FormingStartingProtocolsProgramState,
    applications: SnapshotStateList<ApplicationBuilder>,
) {
    Button(
        onClick = { saveAndNext(programState, state, applications) },
        content = { Text(text = "Сохранить и далее") },
    )
}

@Composable
fun DisplayApplication(
    state: FormingStartingProtocolsProgramState,
    applicationBuilder: ApplicationBuilder
) {
    Card(backgroundColor = Color.LightGray) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.padding(10.dp)) {
                OutlinedTextField(
                    applicationBuilder.team.value,
                    onValueChange = {
                        applicationBuilder.team.value = it
                    },
                    label = { Text(text = "Название команды") }
                )
            }
            FoldingList(
                {
                    Text(
                        "Участники",
                        modifier = Modifier.width(250.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                },
                applicationBuilder.applicants,
                { applicationBuilder ->
                    ShowApplicantBuilder(
                        state,
                        applicationBuilder
                    )
                },
                ::ApplicantBuilder
            )
        }
    }
}

@Composable
fun ShowApplicantBuilder(
    state: FormingStartingProtocolsProgramState,
    applicantBuilder: ApplicantBuilder
) {
    val groups = state.competition.groups.map { it.label }.toMutableStateList()

    @Composable
    fun BindableTextField(
        name: String,
        string: MutableState<String>,
        width: Float
    ) {
        OutlinedTextField(
            string.value,
            modifier = Modifier.width(width.dp),
            onValueChange = { string.value = it },
            label = { Text(name) }
        )
    }

    Column {
        Row {
            BindableTextField("Фамилия", applicantBuilder.lastName, 100f)
            BindableTextField("Имя", applicantBuilder.name, 100f)
            BindableTextField("Год рождения", applicantBuilder.birthYear, 150f)
            LabeledDropdownMenu(
                name = "Группа",
                suggestions = groups,
                selectedText = applicantBuilder.supposedGroupLabel,
                width = 100.dp,
            )
            BindableTextField("Разряд", applicantBuilder.sportsCategory, 100f)
        }
        if (applicantBuilder.birthYear.value.toIntOrNull() == null)
            Text(text = "Год рождения должен быть числом!", color = Color.Red)
    }
}
