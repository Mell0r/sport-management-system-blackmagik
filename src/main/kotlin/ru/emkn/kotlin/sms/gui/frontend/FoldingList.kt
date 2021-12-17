package ru.emkn.kotlin.sms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(100.dp))
                    DisplayElement(element)
                    Button()
                }
            }

            list.forEach {
                DisplayRow(it) {
                    Button(
                        onClick = { list.remove(it) },
                        content = { Text(redCross) }
                    )
                }
            }

            Button(onClick = {
                val newValue = newElement()
                list.add(newValue)
            }) {
                Text(plus)
            }
        }
    }
    FoldingObject(Header, Content)
}