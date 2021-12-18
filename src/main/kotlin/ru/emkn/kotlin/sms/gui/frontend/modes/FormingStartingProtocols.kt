package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import ru.emkn.kotlin.sms.gui.frontend.FoldingList
import ru.emkn.kotlin.sms.gui.programState.FormingStartingProtocolsProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState

class ApplicationBuilder(
    var team: String = "",
    val applicants: SnapshotStateList<ApplicantBuilder> = mutableStateListOf()
)

class ApplicantBuilder(
    val supposedGroupLabel: MutableState<String> = mutableStateOf(""),
    val lastName: MutableState<String> = mutableStateOf(""),
    val name: MutableState<String> = mutableStateOf(""),
    val birthYear: MutableState<String> = mutableStateOf(""),
    val sportsCategory: MutableState<String> = mutableStateOf(""),
)

@Composable
fun FormingStartingProtocols(programState: MutableState<ProgramState>) {
    val state = programState.value as FormingStartingProtocolsProgramState
    val applications = remember { mutableStateListOf<ApplicationBuilder>() }

    val majorListsFontSize = 25.sp
    FoldingList(
        {
            Text(
                "Маршруты",
                modifier = Modifier.width(150.dp),
                textAlign = TextAlign.Center,
                fontSize = majorListsFontSize
            )
        },
        applications,
        { applicationBuilder -> DisplayApplication(applicationBuilder) },
        { ApplicationBuilder() },
        majorListsFontSize
    )
}

@Composable
fun DisplayApplication(applicationBuilder: ApplicationBuilder) {
    Card(backgroundColor = Color.LightGray) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.padding(10.dp)) {
                var nameState by remember { mutableStateOf(applicationBuilder.team) }
                OutlinedTextField(
                    nameState,
                    onValueChange = {
                        nameState = it; applicationBuilder.team = it
                    },
                    label = { Text(text = "Название команды") }
                )
            }
            FoldingList(
                {
                    Text(
                        "Участники",
                        modifier = Modifier.width(200.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                },
                applicationBuilder.applicants,
                { applicationBuilder -> ShowApplicantBuilder(applicationBuilder) },
                { ApplicantBuilder() }
            )
        }
    }
}

@Composable
fun ShowApplicantBuilder(applicantBuilder: ApplicantBuilder) {
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
    BindableTextField("Фамилия", applicantBuilder.lastName, 100f)
    BindableTextField("Имя", applicantBuilder.name, 100f)
    BindableTextField("Год рождения", applicantBuilder.birthYear, 100f)
}
