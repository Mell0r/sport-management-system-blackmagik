package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.gui.builders.AgeGroupBuilder
import ru.emkn.kotlin.sms.gui.builders.CompetitionBuilder
import ru.emkn.kotlin.sms.gui.builders.INCORRECT_YEAR
import ru.emkn.kotlin.sms.gui.builders.OrderedCheckpointsRouteBuilder
import ru.emkn.kotlin.sms.gui.frontend.FoldingList
import ru.emkn.kotlin.sms.gui.frontend.LabeledDropdownMenu
import ru.emkn.kotlin.sms.gui.programState.ConfiguringCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState

@OptIn(ExperimentalAnimationApi::class)
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
            if (!isYearIncorrect) competitionBuilder.year.value.toString() else "",
            onValueChange = { newValue ->
                isYearIncorrect = newValue.toIntOrNull() == null
                competitionBuilder.year.value = newValue.toIntOrNull() ?: INCORRECT_YEAR },
            modifier = Modifier.width(width),
            label = { Text("Год проведения") }
        )

        AnimatedVisibility(isYearIncorrect) {
            Text("Год проведения соревнования должен быть числом", color = Color.Red)
        }

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
                label = { Text("Название маршрута") }
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DisplayGroup(group: AgeGroupBuilder) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                group.label.value,
                onValueChange = { group.label.value = it },
                label = { Text("Название группы") })

            Spacer(Modifier.width(16.dp))

            val ages = (0..99).map { it.toString() }.toMutableStateList()
            val agesFrom = remember { mutableStateOf("0") }
            val agesTo = remember { mutableStateOf("0") }
            group.ageFrom = mutableStateOf(agesFrom.value.toInt())
            group.ageTo = mutableStateOf(agesTo.value.toInt())

            LabeledDropdownMenu("Возраст от", ages, agesFrom, 150.dp)

            Spacer(Modifier.width(16.dp))

            LabeledDropdownMenu("Возраст до", ages, agesTo, 150.dp)
        }
        AnimatedVisibility(group.ageFrom.value > group.ageTo.value) {
            Text("'Возраст от' не должен превышать 'Возраст до'!", color = Color.Red)
        }
    }
}

@Composable
fun CompetitionConfiguration(programState: MutableState<ProgramState>, dialogSize: DpSize) {
    if (programState.value !is ConfiguringCompetitionProgramState)
        return
    val competitionBuilder = (programState.value as ConfiguringCompetitionProgramState).competitionBuilder

    Column(modifier = Modifier.verticalScroll(rememberScrollState(0)).padding(16.dp)) {
        Text("В этом окне вам нужно настроить ваше соревнование",
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 35.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            DisplayCompetitionTextFields(competitionBuilder, dialogSize.width / 2)

            Button(
                onClick = { programState.value = programState.value.nextProgramState() },
                content = { Text("Сохранить и далее") },
                modifier = Modifier.padding(start = dialogSize.width / 8)
                    .size(dialogSize.width / 4, dialogSize.height / 4)
            )
        }

        val majorListsFontSize = 25.sp
        FoldingList(
            { Text("Маршруты",
                modifier = Modifier.width(150.dp),
                textAlign = TextAlign.Center,
                fontSize = majorListsFontSize) },
            competitionBuilder.routes,
            { route -> DisplayRoute(route) },
            { OrderedCheckpointsRouteBuilder("", mutableStateListOf()) },
            majorListsFontSize
        )

        FoldingList(
            { Text("Группы",
                modifier = Modifier.width(150.dp),
                textAlign = TextAlign.Center,
                fontSize = majorListsFontSize) },
            competitionBuilder.groups,
            { group -> DisplayGroup(group) },
            { AgeGroupBuilder() },
            majorListsFontSize
        )
    }
}