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
import ru.emkn.kotlin.sms.gui.frontend.elements.FoldingList
import ru.emkn.kotlin.sms.gui.frontend.elements.LabeledDropdownMenu
import ru.emkn.kotlin.sms.gui.frontend.elements.openFileDialog
import ru.emkn.kotlin.sms.gui.frontend.elements.safeOpenSingleFileOrNull
import ru.emkn.kotlin.sms.gui.programState.FormingStartingProtocolsProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.io.*
import ru.emkn.kotlin.sms.io.ReadFailException
import ru.emkn.kotlin.sms.io.WrongFormatException

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

        val errorMessage = remember { mutableStateOf<String?>(null) }

        LoadApplicationsFromCSVButton(state, applicationBuilders, errorMessage)
        LoadReadyStartingConfigurationButton(programState, state)
        SaveAndNextButton(
            programState,
            state,
            applicationBuilders,
            errorMessage
        )

        val errorMessageFrozen = errorMessage.value
        if (errorMessageFrozen != null) {
            Text(errorMessageFrozen, fontSize = 15.sp, color = Color.Red)
        }
    }

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
            // TODO failure window
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
            // TODO failure window
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

@Composable
private fun LoadApplicationsFromCSVButton(
    state: FormingStartingProtocolsProgramState,
    applicationBuilders: SnapshotStateList<ApplicationBuilder>,
    errorMessage: MutableState<String?>,
) {
    Button(
        onClick = onClick@{
            val files = openFileDialog(
                title = "Загрузить заявки из CSV",
                allowMultiSelection = true
            ).toList()
            val applications = try {
                readAndParseAllFiles(
                    files = files,
                    competition = state.competition,
                    parser = Application::readFromFileContentAndCompetition,
                )
            } catch (e: ReadFailException) {
                Logger.error { "Could not read applications. Following exception occurred:\n${e.message}" }
                errorMessage.value = e.message
                return@onClick
            } catch (e: WrongFormatException) {
                Logger.error { "Some application had wrong format. Following exception occurred:\n${e.message}" }
                errorMessage.value = e.message
                return@onClick
            }
            // add all applications
            applicationBuilders.addAll(
                applications.map(ApplicationBuilder.Companion::fromApplication)
            )
        },
        content = { Text(text = "Загрузить заявки из CSV") },
    )
}

@Composable
private fun SaveAndNextButton(
    programState: MutableState<ProgramState>,
    state: FormingStartingProtocolsProgramState,
    applications: SnapshotStateList<ApplicationBuilder>,
    errorMessage: MutableState<String?>,
) {
    Button(
        onClick = onClick@{
            // form applications
            // if something went wrong, do not succeed to the next mode
            val applicationBuilders = applications.toList()
            val actualApplications = try {
                applicationBuilders.map { it.build() }
            } catch (e: IllegalArgumentException) {
                Logger.error { "Could not form applications, following exception occurred:\n${e.message}" }
                errorMessage.value = e.message
                return@onClick
            }
            val (participantsList, startingProtocols) = try {
                getStartConfigurationByApplications(
                    applications = actualApplications,
                    competition = state.competition,
                )
            } catch (e: IllegalArgumentException) {
                Logger.error { "Could not form starting configuration, following exception occurred:\n${e.message}" }
                errorMessage.value = e.message
                return@onClick
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
        },
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
