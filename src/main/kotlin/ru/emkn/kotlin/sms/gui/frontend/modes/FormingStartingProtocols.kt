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
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.getStartConfigurationByApplications
import ru.emkn.kotlin.sms.gui.builders.ApplicantBuilder
import ru.emkn.kotlin.sms.gui.builders.ApplicationBuilder
import ru.emkn.kotlin.sms.gui.frontend.FoldingList
import ru.emkn.kotlin.sms.gui.frontend.LabeledDropdownMenu
import ru.emkn.kotlin.sms.gui.programState.FormingStartingProtocolsProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState

@Composable
fun FormingStartingProtocols(programState: MutableState<ProgramState>) {
    val state = programState.value as? FormingStartingProtocolsProgramState ?: return
    val applications = remember { mutableStateListOf<ApplicationBuilder>() }

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
            applications,
            { applicationBuilder -> DisplayApplication(state, applicationBuilder) },
            { ApplicationBuilder() },
            majorListsFontSize
        )

        Button(
            onClick = {
                // form applications
                // if something went wrong, do not succeed to the next mode
                val applicationBuilders = applications.toList()
                val actualApplications = try {
                    applicationBuilders.map { it.build() }
                } catch (e: IllegalArgumentException) {
                    Logger.error {"Could not form applications, following exception occurred:\n${e.message}"}
                    return@Button
                }
                val (participantsList, startingProtocols) = try {
                    getStartConfigurationByApplications(
                        applications = actualApplications,
                        competition = state.competition,
                    )
                } catch (e: IllegalArgumentException) {
                    Logger.error {"Could not form starting configuration, following exception occurred:\n${e.message}"}
                    return@Button
                }
                // form participant list and starting times
                state.participantsListBuilder.replaceFromParticipantsList(participantsList)
                state.startingTimes.replaceFromStartingProtocolsAndParticipantsList(startingProtocols, participantsList)
                state.startingTimes.startingTimesMapping.forEach { participant, time ->
                    Logger.trace {"$participant $time"}
                }
                programState.value = state.nextProgramState()
            },
            content = { Text(text = "Сохранить и далее") },
        )
    }

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
                { applicationBuilder -> ShowApplicantBuilder(state, applicationBuilder) },
                { ApplicantBuilder() }
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
