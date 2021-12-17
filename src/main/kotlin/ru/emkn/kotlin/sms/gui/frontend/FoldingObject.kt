package ru.emkn.kotlin.sms.gui.frontend

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


val downArrow = getEmojiByUnicode(11167)
val rightArrow = getEmojiByUnicode(11166)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FoldingObject(
    Header: @Composable() () -> Unit,
    Content: @Composable() () -> Unit
) {
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