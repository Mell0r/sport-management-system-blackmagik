package ru.emkn.kotlin.sms.gui.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.application
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.gui.builders.CompetitionBuilder
import ru.emkn.kotlin.sms.gui.builders.OrderedCheckpointsRouteBuilder
import ru.emkn.kotlin.sms.gui.programState.*


fun getEmojiByUnicode(unicode: Int): String {
    return String(Character.toChars(unicode))
}

@Composable
fun DisplayRoute(route: OrderedCheckpointsRouteBuilder) {
    fun ShowCheckpoint(): @Composable (MutableState<CheckpointLabelT>) -> Unit =
        { checkpoint ->
            TextField(checkpoint.value, { checkpoint.value = it; })
        }

    @Composable
    fun ShowName() {
        var nameLabel by remember { mutableStateOf(route.name) }
        TextField(nameLabel, { nameLabel = it; route.name = it })
    }

    FoldingList(
        { ShowName() },
        route.orderedCheckpoints,
        ShowCheckpoint(),
        { mutableStateOf(String()) }
    )
}


@Composable
fun CompetitionConfiguration(programState: MutableState<ProgramState>) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState(0))) {
        val configCompetitionState =
            programState.value as ConfiguringCompetitionProgramState

        val competitionBuilder = configCompetitionState.competitionBuilder

        DisplayCompetition(competitionBuilder, programState)

    }
}

@Composable
private fun DisplayCompetition(
    competitionBuilder: CompetitionBuilder,
    programState: MutableState<ProgramState>
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        DisplayCompetition(competitionBuilder)

        Button(
            onClick = {
                programState.value = programState.value.nextProgramState()
            },
            content = { Text("Сохранить и далее") },
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                .padding(16.dp)
        )
    }
    @Composable
    fun DisplayRoutes() {
        FoldingList(
            { Text("Маршруты") },
            competitionBuilder.routes,
            { route -> DisplayRoute(route) },
            { OrderedCheckpointsRouteBuilder("", mutableStateListOf()) },
        )
    }

    DisplayRoutes()
}

@Composable
private fun DisplayCompetition(
    competitionBuilder: CompetitionBuilder
) {
    Column(horizontalAlignment = Alignment.End) {
        var isYearIncorrect by remember { mutableStateOf(true) }

        @Composable
        fun BindableTextField(
            name: String,
            string: MutableState<String>
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$name:")
                TextField(string.value, onValueChange = {
                    string.value = it
                })
            }
        }
        BindableTextField(
            "Дисциплина",
            competitionBuilder.discipline
        )
        BindableTextField(
            "Название",
            competitionBuilder.name
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Год:")
            TextField(
                if (competitionBuilder.year.value != -1000) competitionBuilder.year.value.toString() else "",
                onValueChange = { newValue ->
                    isYearIncorrect = newValue.toIntOrNull() == null
                    competitionBuilder.year.value =
                        newValue.toIntOrNull() ?: -1000
                })
        }
        if (isYearIncorrect)
            Text(
                "Год соревнования должен быть числом",
                color = Color.Red
            )
        BindableTextField(
            "Дата",
            competitionBuilder.date
        )
    }
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
                    state = DialogState(size = DpSize(800.dp, 800.dp)),
                    onCloseRequest = ::exitApplication,
                    content = { CompetitionConfiguration(programState) })
            is FormingStartingProtocolsProgramState ->
                Dialog(onCloseRequest = ::exitApplication,
                    content = { Text("Joke") })
            is OnGoingCompetitionProgramState ->
                Dialog(onCloseRequest = ::exitApplication,
                    content = { TODO() })
            is FinishedCompetitionProgramState ->
                Dialog(onCloseRequest = ::exitApplication,
                    content = { TODO() })
        }

        //Logger.debug { "Program successfully finished." }
    }
}