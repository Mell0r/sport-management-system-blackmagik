package ru.emkn.kotlin.sms.gui.frontend.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.gui.frontend.getEmojiByUnicode

val redCross = getEmojiByUnicode(10060)
val plus = getEmojiByUnicode(10133)

@Composable
fun <T> FoldingList(
    Header: @Composable () -> Unit,
    list: SnapshotStateList<T>,
    DisplayElement: @Composable (T) -> Unit,
    newElement: () -> T,
    headerFontSize: TextUnit = TextUnit.Unspecified
) {
    val Content = @Composable {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            @Composable
            fun DisplayRow(element: T, Button: @Composable () -> Unit) {
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
    FoldingObject(Header, Content, headerFontSize)
}

