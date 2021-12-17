package ru.emkn.kotlin.sms.gui.frontend.modes

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
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.gui.builders.CompetitionBuilder
import ru.emkn.kotlin.sms.gui.builders.INCORRECT_YEAR
import ru.emkn.kotlin.sms.gui.builders.OrderedCheckpointsRouteBuilder
import ru.emkn.kotlin.sms.gui.frontend.FoldingList
import ru.emkn.kotlin.sms.gui.programState.ConfiguringCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState

@Composable
fun CompetitionConfiguration(programState: MutableState<ProgramState>) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState(0))) {
        val configCompetitionState =
            programState.value as ConfiguringCompetitionProgramState

        val competitionBuilder = configCompetitionState.competitionBuilder

        DisplayCompetition(competitionBuilder) { programState.value = programState.value.nextProgramState() }
    }
}

@Composable
private fun DisplayCompetition(competitionBuilder: CompetitionBuilder, changeProgramState: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        DisplayCompetitionTextFields(competitionBuilder)

        Button(
            onClick = { changeProgramState() },
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
private fun DisplayCompetitionTextFields(competitionBuilder: CompetitionBuilder) {
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
                if (competitionBuilder.year.value != INCORRECT_YEAR) competitionBuilder.year.value.toString() else "",
                onValueChange = { newValue ->
                    isYearIncorrect = newValue.toIntOrNull() == null
                    competitionBuilder.year.value =
                        newValue.toIntOrNull() ?: INCORRECT_YEAR
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

@Composable
fun DisplayRoute(route: OrderedCheckpointsRouteBuilder) {
    fun ShowCheckpoint(): @Composable (MutableState<CheckpointLabelT>) -> Unit = { checkpoint ->
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