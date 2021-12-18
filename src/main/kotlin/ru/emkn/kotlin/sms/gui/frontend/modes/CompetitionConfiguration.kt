package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.gui.builders.CompetitionBuilder
import ru.emkn.kotlin.sms.gui.builders.INCORRECT_YEAR
import ru.emkn.kotlin.sms.gui.builders.OrderedCheckpointsRouteBuilder
import ru.emkn.kotlin.sms.gui.frontend.FoldingList
import ru.emkn.kotlin.sms.gui.programState.ConfiguringCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState


@Composable
fun CompetitionConfiguration(programState: MutableState<ProgramState>) {
    val state = programState.value as? ConfiguringCompetitionProgramState ?: return
    val competitionBuilder = state.competitionBuilder
    val dialogSize = DpSize(800.dp, 800.dp)

    Column(modifier = Modifier.verticalScroll(rememberScrollState(0))) {
        Text("В этом окне вам нужно настроить ваше соревнование",
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 35.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            DisplayCompetitionTextFields(competitionBuilder, dialogSize.width / 2)

            Button(
                onClick = {
                    programState.value = programState.value.nextProgramState()
                },
                content = { Text("Сохранить и далее") },
                modifier = Modifier.padding(dialogSize.width / 8).size(dialogSize.width / 4, dialogSize.height / 4)
            )
        }

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
            competitionBuilder.routes,
            { route -> DisplayRoute(route) },
            { OrderedCheckpointsRouteBuilder("", mutableStateListOf()) },
            majorListsFontSize
        )
    }
}

@Composable
private fun DisplayCompetitionTextFields(competitionBuilder: CompetitionBuilder, width: Dp) {
    Column(horizontalAlignment = Alignment.Start) {
        var isYearIncorrect by remember { mutableStateOf(true) }

        @Composable
        fun BindableTextField(
            name: String,
            string: MutableState<String>
        ) {
            OutlinedTextField(
                string.value,
                modifier = Modifier.width(width),
                onValueChange = { string.value = it },
                label = { Text(name) }
            )
        }
        BindableTextField("Дисциплина", competitionBuilder.discipline)
        BindableTextField("Название", competitionBuilder.name)
        OutlinedTextField(
                if (competitionBuilder.year.value != INCORRECT_YEAR) competitionBuilder.year.value.toString() else "",
                onValueChange = { newValue ->
                    isYearIncorrect = newValue.toIntOrNull() == null
                    competitionBuilder.year.value = newValue.toIntOrNull() ?: INCORRECT_YEAR },
                modifier = Modifier.width(width),
                label = { Text("Год проведения") }
            )
        if (isYearIncorrect)
            Text(
                "Год проведения соревнования должен быть числом",
                color = Color.Red,
            )
        BindableTextField("Дата", competitionBuilder.date)
    }
}

@Composable
fun DisplayRoute(route: OrderedCheckpointsRouteBuilder) {
    fun ShowCheckpoint(): @Composable (MutableState<CheckpointLabelT>) -> Unit = { checkpoint ->
        TextField(checkpoint.value, { checkpoint.value = it; })
    }

    Column(modifier = Modifier.padding(10.dp)) {
        Row(modifier = Modifier.padding(10.dp)) {
            var nameState by remember { mutableStateOf(route.name) }
            OutlinedTextField(
                nameState,
                onValueChange = { nameState = it; route.name = it },
                label = { Text(text = "Название маршрута") }
            )
        }
        FoldingList(
            {
                Text(
                    "Контрольные точки",
                    modifier = Modifier.width(200.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            },
            route.orderedCheckpoints,
            ShowCheckpoint(),
            { mutableStateOf(String()) }
        )
    }
}