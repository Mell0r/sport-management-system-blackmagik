@file:Suppress("FunctionName")

package ru.emkn.kotlin.sms.gui.frontend.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import ru.emkn.kotlin.sms.gui.frontend.getEmojiByUnicode

val downArrow = getEmojiByUnicode(11167)
val rightArrow = getEmojiByUnicode(11166)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FoldingObject(
    Header: @Composable () -> Unit,
    Content: @Composable () -> Unit,
    headerFontSize: TextUnit = TextUnit.Unspecified
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(Modifier.clickable { expanded = !expanded }) {
            AnimatedContent(targetState = expanded) { targetState ->
                if (targetState)
                    Text(downArrow, fontSize = headerFontSize)
                else
                    Text(rightArrow, fontSize = headerFontSize)
            }
            Header()
        }
        AnimatedVisibility(expanded) {
            Content()
        }
    }
}