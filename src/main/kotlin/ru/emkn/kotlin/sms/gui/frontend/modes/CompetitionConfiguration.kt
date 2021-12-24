package ru.emkn.kotlin.sms.gui.frontend.modes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.michaelbull.result.*
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.gui.builders.AgeGroupBuilder
import ru.emkn.kotlin.sms.gui.builders.CompetitionBuilder
import ru.emkn.kotlin.sms.gui.builders.OrderedCheckpointsRouteBuilder
import ru.emkn.kotlin.sms.gui.frontend.elements.*
import ru.emkn.kotlin.sms.gui.programState.ConfiguringCompetitionProgramState
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.io.saveCompetition
import java.io.File

private val errorDialogMessage: MutableState<String?> = mutableStateOf(null)
private val successDialogMessage: MutableState<String?> = mutableStateOf(null)

val ages = (0..99).map { "$it" }.toMutableStateList()

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DisplayCompetitionTextFields(
    competitionBuilder: CompetitionBuilder,
    width: Dp
) {
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
                competitionBuilder.year.value =
                    newValue.toIntOrNull() ?: CompetitionBuilder.INCORRECT_YEAR
            },
            modifier = Modifier.width(width),
            label = { Text("Год проведения") }
        )

        AnimatedVisibility(isYearIncorrect) {
            Text(
                "Год проведения соревнования должен быть числом",
                color = Color.Red
            )
        }
        BindableTextField("Дата", competitionBuilder.date)
    }
}

@Composable
fun DisplayRoute(route: OrderedCheckpointsRouteBuilder) {
    fun ShowCheckpoint(): @Composable (MutableState<CheckpointLabelT>) -> Unit =
        { checkpoint ->
            TextField(checkpoint.value, { checkpoint.value = it; })
        }

    Column(modifier = Modifier.padding(10.dp)) {
        Row(modifier = Modifier.padding(10.dp)) {
            OutlinedTextField(
                route.name.value,
                onValueChange = { route.name.value = it },
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
fun DisplayGroup(
    group: AgeGroupBuilder,
    availableRoutes: SnapshotStateList<OrderedCheckpointsRouteBuilder>
) {
    fun checkAge(a: String, b: String): Boolean =
        (a.toIntOrNull() ?: 0) > (b.toIntOrNull() ?: -1)

    fun routesToStrings(availableRoutes: SnapshotStateList<OrderedCheckpointsRouteBuilder>) =
        availableRoutes.map { it.name.value }.toMutableStateList()

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                group.label.value,
                onValueChange = { group.label.value = it },
                label = { Text("Название группы") })

            Spacer(Modifier.width(16.dp))

            LabeledDropdownMenu("Возраст от", ages, group.ageFrom, 150.dp)

            Spacer(Modifier.width(16.dp))

            LabeledDropdownMenu("Возраст до", ages, group.ageTo, 150.dp)

            Spacer(Modifier.width(16.dp))

            LabeledDropdownMenu(
                "Маршрут",
                routesToStrings(availableRoutes),
                group.routeName,
                250.dp
            )
        }
        AnimatedVisibility(checkAge(group.ageFrom.value, group.ageTo.value)) {
            Text(
                "'Возраст от' не должен превышать 'Возраст до'!",
                color = Color.Red
            )
        }
    }
}

@Composable
fun CompetitionConfiguration(
    programState: MutableState<ProgramState>,
    dialogSize: DpSize
) {
    val state = programState.value as? ConfiguringCompetitionProgramState ?: return
    val competitionBuilder = state.competitionBuilder

    SuccessDialog(successDialogMessage)
    ErrorDialog(errorDialogMessage)

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState(0))
            .padding(16.dp)
    ) {
        Text(
            "В этом окне вам нужно настроить ваше соревнование",
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 35.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            DisplayCompetitionTextFields(
                competitionBuilder,
                dialogSize.width / 2
            )
            Column {
                Button(
                    onClick = {
                        programState.value = state.nextProgramState()
                    },
                    content = { Text("Сохранить и далее") },
                    modifier = Modifier.padding(start = dialogSize.width / 8)
                        .size(dialogSize.width / 4, dialogSize.height / 4)
                )
                Spacer(modifier = Modifier.height(dialogSize.height / 20))
                LoadCompetitionButton(competitionBuilder, dialogSize)
                Spacer(modifier = Modifier.height(dialogSize.height / 20))
                ExportCompetitionButton(competitionBuilder, dialogSize)
            }
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
            { DisplayRoute(it) },
            {
                OrderedCheckpointsRouteBuilder(
                    mutableStateOf(""),
                    mutableStateListOf()
                )
            },
            majorListsFontSize
        )

        FoldingList(
            {
                Text(
                    "Группы",
                    modifier = Modifier.width(150.dp),
                    textAlign = TextAlign.Center,
                    fontSize = majorListsFontSize
                )
            },
            competitionBuilder.groups,
            { group -> DisplayGroup(group, competitionBuilder.routes) },
            ::AgeGroupBuilder,
            majorListsFontSize
        )
    }
}

private fun exportCompetition(
    competitionBuilder: CompetitionBuilder,
) {
    val folder: File? = pickFolderDialog()
    if (folder == null) {
        // No failure window is required because user probably just selected cancel
        Logger.warn("No folder was selected; Aborting")
        return
    }
    saveCompetition(competitionBuilder.build(), folder.absolutePath)
        .onSuccess {
            successDialogMessage.value = "Соревнование успешно экспортировано в папку \"${folder.path}\"!"
        }
        .onFailure { message ->
            Logger.error("Failed to save competition.\n$message\nAborting.")
            errorDialogMessage.value = "Соревнование не было экспортировано. Прозошла следующая ошибка.\n" +
                    message
        }
}

@Composable
private fun ExportCompetitionButton(
    competitionBuilder: CompetitionBuilder,
    dialogSize: DpSize,
) {
    Button(
        onClick = { exportCompetition(competitionBuilder) },
        modifier = Modifier.padding(start = dialogSize.width / 8)
            .size(dialogSize.width / 4, dialogSize.height / 10)
    ) { Text("Сохранить соревнование в папку (CSV)") }
}

private fun loadCompetition(
    competitionBuilder: CompetitionBuilder,
) {
    val folder: File? = pickFolderDialog()
    if (folder == null) {
        // No failure window is required because user probably just selected cancel
        Logger.warn("No folder was selected; Aborting")
        return
    }
    competitionBuilder.replaceFromFilesInFolder(folder.absolutePath)
        .onSuccess {
            successDialogMessage.value = "Соревнование из папки \"${folder.path}\" успешно загружено!"
        }
        .onFailure { message ->
            Logger.error("Failed to initialize competition.\n$message\nAborting.")
            errorDialogMessage.value = "Соревнование из папки \\\"${folder.path}\\\" не было загружено! Произошла следующая ошибка.\n" +
                message
        }
}

@Composable
private fun LoadCompetitionButton(
    competitionBuilder: CompetitionBuilder,
    dialogSize: DpSize
) {
    Button(
        onClick = { loadCompetition(competitionBuilder) },
        modifier = Modifier.padding(start = dialogSize.width / 8)
            .size(dialogSize.width / 4, dialogSize.height / 10)
    ) { Text("Загрузить соревнование из папки (CSV)") }
}