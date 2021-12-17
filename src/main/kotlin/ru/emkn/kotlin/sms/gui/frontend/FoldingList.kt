package ru.emkn.kotlin.sms.gui.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val blackCross = getEmojiByUnicode(10006)
val redCross = getEmojiByUnicode(10060)
val plus = getEmojiByUnicode(10133)

@Composable
fun <T> FoldingList(
    Header: @Composable () -> Unit,
    list: SnapshotStateList<T>,
    DisplayElement: @Composable (T) -> Unit,
    newElement: () -> T
) {
    val Content = @Composable {
        Column {
            @Composable
            fun DisplayRow(element: T, Button: @Composable() () -> Unit) {
                Row {
                    Spacer(Modifier.width(50.dp))
                    DisplayElement(element)
                    Button()
                }
            }

            list.forEach {
                DisplayRow(it) {
                    TextButton(
                        onClick = { list.remove(it) },
                        content = { Text(redCross) }
                    )
                }
            }

            Row {
                Spacer(Modifier.width(50.dp))
                TextButton(onClick = {
                    val newValue = newElement()
                    list.add(newValue)
                }) {
                    Text(plus)
                }
            }
        }
    }
    FoldingObject(Header, Content)
}

