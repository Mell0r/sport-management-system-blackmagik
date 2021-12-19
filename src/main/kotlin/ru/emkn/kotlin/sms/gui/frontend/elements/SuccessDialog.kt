package ru.emkn.kotlin.sms.gui.frontend.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.gui.frontend.getEmojiByUnicode

val greenCheckMark = getEmojiByUnicode(9989)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SavingSuccessDialog() {
    val width = 300.dp
    val height = 100.dp
    val visible = remember { mutableStateOf(true) }
    AnimatedVisibility (visible.value) {
        AlertDialog(
            onDismissRequest = { visible.value = false },
            title = { Text(greenCheckMark) },
            text = { Text("Всё успешно сохранено!") },
            modifier = Modifier.size(width, height).border(3.dp, Color.Green, shape = RoundedCornerShape(3.dp)),
            buttons = {})
    }
}