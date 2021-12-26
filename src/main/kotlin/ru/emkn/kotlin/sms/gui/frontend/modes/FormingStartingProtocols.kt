@file:Suppress("FunctionName")

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
import org.jetbrains.exposed.sql.Database
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.csv.ParticipantsListCsvParser
import ru.emkn.kotlin.sms.db.readers.ParticipantsListDbReader
import ru.emkn.kotlin.sms.db.util.safeConnectToPath
import ru.emkn.kotlin.sms.db.writers.ParticipantsListDbWriter
import ru.emkn.kotlin.sms.gui.builders.ApplicantBuilder
import ru.emkn.kotlin.sms.gui.builders.ApplicationBuilder
import ru.emkn.kotlin.sms.gui.frontend.elements.*
import ru.emkn.kotlin.sms.gui.programState.FormingParticipantsListProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.gui.safeCSVDumpableToFile
import ru.emkn.kotlin.sms.gui.writeCSVDumpablesToDirectory
import ru.emkn.kotlin.sms.startcfg.Application
import ru.emkn.kotlin.sms.startcfg.ApplicationProcessor
import ru.emkn.kotlin.sms.startcfg.LinearStartingTimeAssigner
import ru.emkn.kotlin.sms.successOrNothing
import java.io.File

private val errorDialogMessage: MutableState<String?> = mutableStateOf(null)
private val successDialogMessage: MutableState<String?> = mutableStateOf(null)

@Composable
fun FormingParticipantsList(programState: MutableState<ProgramState>) {
    val state =
        programState.value as? FormingParticipantsListProgramState ?: return
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

        LoadApplicationsFromCSVButton(applicationBuilders)
        LoadReadyStartingConfigurationFromCSVButton(programState, state)
        LoadReadyStartingConfigurationFromSQLButton(programState, state)
        SaveAndNextButton(programState, state, applicationBuilders)
        SaveAndExportToCSVAndNextButton(programState, state, applicationBuilders)
        SaveAndExportToSQLAndNextButton(programState, state, applicationBuilders)
    }
    SuccessDialog(successDialogMessage)
    ErrorDialog(errorDialogMessage)
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


private fun loadReadyStartingConfigurationFromCSV(
    programState: MutableState<ProgramState>,
    state: FormingParticipantsListProgramState,
) {
    Logger.debug { "User pressed load ready start configuration." }
    val participantsListFile = safeOpenSingleFileOrNull("Выберите список участников (participants-list.csv)")
        ?: return
    val participantsList = ParticipantsListCsvParser(state.competition).readAndParse(participantsListFile).successOrNothing {
        errorDialogMessage.value = it
        return
    }
    state.participantsList = participantsList
    programState.value = state.nextProgramState()
}

@Composable
private fun LoadReadyStartingConfigurationFromCSVButton(
    programState: MutableState<ProgramState>,
    state: FormingParticipantsListProgramState,
) {
    Button(
        onClick = { loadReadyStartingConfigurationFromCSV(programState, state) },
        content = { Text(text = "Загрузить готовый список учасников из CSV и перейти далее.") },
    )
}

private fun loadReadyStartingConfigurationFromSQL(
    programState: MutableState<ProgramState>,
    state: FormingParticipantsListProgramState,
) {
    val files = openFileDialog("Выберите местоположение базы данных", false)
    if (files.size != 1) return
    val file = files.single()
    val pathToDb = try {
        getDbPathFromFile(file)
    } catch (e: NoSuchElementException) {
        errorDialogMessage.value = "Некорректный путь к базе данных!"
        return
    }
    val database = Database.safeConnectToPath(pathToDb).successOrNothing {
        errorDialogMessage.value = "Не удалось подключиться к базе данных. Возникла следующая ошибка:\n$it"
        return
    }
    val reader = ParticipantsListDbReader(database, state.competition)
    val participantsList = reader.read().successOrNothing {
        errorDialogMessage.value = "Не удалось загрузить список участников из базы данных. Возникла следующая ошибка:\n$it"
        return
    }

    state.participantsList = participantsList
    programState.value = state.nextProgramState()
}

@Composable
private fun LoadReadyStartingConfigurationFromSQLButton(
    programState: MutableState<ProgramState>,
    state: FormingParticipantsListProgramState,
) {
    Button(
        onClick = { loadReadyStartingConfigurationFromSQL(programState, state) },
        content = { Text(text = "Загрузить готовый список учасников из базы данных (SQL) и перейти далее.") },
    )
}

private fun loadApplicationsFromCSV(
    applicationBuilders: SnapshotStateList<ApplicationBuilder>,
) {
    val files = openFileDialog(
        title = "Загрузить заявки из CSV",
        allowMultiSelection = true
    ).toList()
    if (files.isEmpty()) return

    val applications = Application.readAndParseAll(files).successOrNothing {
        errorDialogMessage.value = it
        return
    }

    // add all applications
    applicationBuilders.addAll(
        applications.map(ApplicationBuilder::fromApplication)
    )

    successDialogMessage.value = "Все заявки были успешно загружены!"
}

@Composable
private fun LoadApplicationsFromCSVButton(
    applicationBuilders: SnapshotStateList<ApplicationBuilder>,
) {
    Button(
        onClick = { loadApplicationsFromCSV(applicationBuilders) },
        content = { Text(text = "Загрузить заявки из CSV") },
    )
}

private enum class SaveAndNextExportMode {
    NO_EXPORT,
    EXPORT_TO_CSV,
    EXPORT_TO_SQL,
}

private fun saveAndNext(
    programState: MutableState<ProgramState>,
    state: FormingParticipantsListProgramState,
    applications: SnapshotStateList<ApplicationBuilder>,
    exportMode: SaveAndNextExportMode = SaveAndNextExportMode.NO_EXPORT,
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

    // form participant list
    val participantsList = try {
        val applicationProcessor = ApplicationProcessor(state.competition, actualApplications.toMutableList())
        val processedApplicants = applicationProcessor.process()
        val startingTimeAssigner = LinearStartingTimeAssigner()
        startingTimeAssigner.assign(processedApplicants)
    } catch (e: IllegalArgumentException) {
        Logger.error { "Could not form starting configuration, following exception occurred:\n${e.message}" }
        errorDialogMessage.value = e.message
        return
    }

    state.participantsList = participantsList

    if (exportMode == SaveAndNextExportMode.EXPORT_TO_CSV) {
        Logger.debug {"Saving participants list and starting protocols to CSV."}

        val participantsListFile = safeOpenSingleFileOrNull("Выберите файл для сохранения списка участников (participants-list.csv)")
        if (participantsListFile == null) {
            // No failure window is required because user probably just selected cancel
            Logger.warn("Participants list was not selected. Aborting.")
            return
        }
        val folder: File? = pickFolderDialog()
        if (folder == null) {
            Logger.warn("No folder was selected. Aborting")
            return
        }

        safeCSVDumpableToFile(participantsList, participantsListFile.absolutePath)
        writeCSVDumpablesToDirectory(participantsList.toStartingProtocols(), folder)
    } else if (exportMode == SaveAndNextExportMode.EXPORT_TO_SQL) {
        val files = openFileDialog("Выберите местоположение базы данных", false)
        if (files.size != 1) return
        val file = files.single()
        val pathToDb = try {
            getDbPathFromFile(file)
        } catch (e: NoSuchElementException) {
            errorDialogMessage.value = "Некорректный путь к базе данных!"
            return
        }
        val database = Database.safeConnectToPath(pathToDb).successOrNothing {
            errorDialogMessage.value = "Не удалось подключиться к базе данных. Возникла следующая ошибка:\n$it"
            return
        }
        val writer = ParticipantsListDbWriter(database)
        writer.overwrite(participantsList)
    }

    programState.value = state.nextProgramState()
}

@Composable
private fun SaveAndNextButton(
    programState: MutableState<ProgramState>,
    state: FormingParticipantsListProgramState,
    applications: SnapshotStateList<ApplicationBuilder>,
) {
    Button(
        onClick = { saveAndNext(programState, state, applications, SaveAndNextExportMode.NO_EXPORT) },
        content = { Text(text = "Сохранить и далее") },
    )
}

@Composable
private fun SaveAndExportToCSVAndNextButton(
    programState: MutableState<ProgramState>,
    state: FormingParticipantsListProgramState,
    applications: SnapshotStateList<ApplicationBuilder>,
) {
    Button(
        onClick = { saveAndNext(programState, state, applications, SaveAndNextExportMode.EXPORT_TO_CSV) },
        content = { Text(text = "Сохранить, экспортировать в CSV и далее") },
    )
}

@Composable
private fun SaveAndExportToSQLAndNextButton(
    programState: MutableState<ProgramState>,
    state: FormingParticipantsListProgramState,
    applications: SnapshotStateList<ApplicationBuilder>,
) {
    Button(
        onClick = { saveAndNext(programState, state, applications, SaveAndNextExportMode.EXPORT_TO_SQL) },
        content = { Text(text = "Сохранить, экспортировать в базу данных (SQL) и далее") },
    )
}

@Composable
fun DisplayApplication(
    state: FormingParticipantsListProgramState,
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
    state: FormingParticipantsListProgramState,
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
