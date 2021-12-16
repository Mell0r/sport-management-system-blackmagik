package ru.emkn.kotlin.sms

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.application
import androidx.compose.ui.window.singleWindowApplication
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.gui.programState.*
import kotlin.system.exitProcess

fun getEmojiByUnicode(unicode: Int): String {
    return String(Character.toChars(unicode))
}

val downArrow = getEmojiByUnicode(11167)
val rightArrow = getEmojiByUnicode(11166)
val redCross = getEmojiByUnicode(10060)
val plus = getEmojiByUnicode(10133)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FoldingObject(Header: @Composable() () -> Unit, Content: @Composable() () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.clickable { expanded = !expanded }) {
        Row {
            AnimatedContent(targetState = expanded) { targetState ->
                if (targetState)
                    Text(downArrow)
                else
                    Text(rightArrow)
            }
            Header()
        }
        AnimatedVisibility(expanded) {
            Content()
        }
    }
}

@Composable
fun <T> FoldingList(
    Header: @Composable () -> Unit,
    list: MutableList<T>,
    DisplayElement: @Composable (T) -> Unit,
    newElement: () -> T)
{
    val stateList = list.toMutableStateList()
    val Content = @Composable { Column {
        @Composable
        fun DisplayRow(element: T, Button: @Composable() () -> Unit) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DisplayElement(element)
                Button()
            }
        }

        stateList.forEach {
            DisplayRow(it) { Button(
                onClick = { list.remove(it); stateList.remove(it) },
                content = { Text(redCross) }
            ) }
        }

        Button(onClick = {
            val newValue = newElement()
            list.add(newValue)
            stateList.add(newValue)
        }) {
            Text(plus)
        }
    }}
    FoldingObject(Header, Content)
}

enum class WorkingMode {
    COMPETITION_CONFIGURATION,
    APPLICATIONS
}

@Composable
fun CompetitionConfiguration(programState: MutableState<ProgramState>) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.End) {
//                @Composable
//                fun ConfigRow(fieldName: String, )
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text("Дисциплина:")
                    TextField("", onValueChange = {
                        (programState.value as ConfiguringCompetitionProgramState).competitionBuilder.discipline
                    })
                }
                Row (verticalAlignment = Alignment.CenterVertically) { Text("Название:"); TextField("", onValueChange = {}) }
                Row (verticalAlignment = Alignment.CenterVertically) { Text("Год:"); TextField("", onValueChange = {}) }
                Row (verticalAlignment = Alignment.CenterVertically) { Text("Дата:"); TextField("", onValueChange = {}) }
            }
            Button(onClick = { programState.value = programState.value.nextProgramState() },
                content = { Text("Сохранить и далее") },
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp))
        }
    }
}

fun main(args: Array<String>) = application {
    Logger.debug { "Program started." }

    val programState: MutableState<ProgramState> = remember { mutableStateOf(ConfiguringCompetitionProgramState()) }
    when (programState.value) {
        is ConfiguringCompetitionProgramState ->
            Dialog(title = "Настройка соревнования",
                onCloseRequest = ::exitApplication,
                content = { CompetitionConfiguration(programState) })
        is FormingStartingProtocolsProgramState ->
            Dialog(onCloseRequest = ::exitApplication,
                content = { TODO() })
        is OnGoingCompetitionProgramState ->
            Dialog(onCloseRequest = ::exitApplication,
                content = { TODO() })
        is FinishedCompetitionProgramState ->
            Dialog(onCloseRequest = ::exitApplication,
                content = { TODO() })
    }

    Logger.debug { "Program successfully finished." }
}