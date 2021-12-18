package ru.emkn.kotlin.sms.gui.frontend

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LabeledDropdownMenu(
    name: String,
    suggestions: SnapshotStateList<String>,
    selectedText: MutableState<String>,
    width: Dp
) {
    var expanded by remember { mutableStateOf(false) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column {
        OutlinedTextField(
            value = selectedText.value,
            onValueChange = { selectedText.value = it },
            readOnly = true,
            modifier = Modifier.width(width).border(3.dp, MaterialTheme.colors.primary, RoundedCornerShape(6.dp)),
            label = { Text(name) },
            trailingIcon = {
                Icon(icon,"Arrow to open and close menu", Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(width).padding(top = 5.dp)
                .border(3.dp, MaterialTheme.colors.primary, RoundedCornerShape(3.dp))
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText.value = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}