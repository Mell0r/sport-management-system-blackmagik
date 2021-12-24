@file:Suppress("FunctionName", "LocalVariableName")

package ru.emkn.kotlin.sms.gui.frontend.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun <T> ImmutableFoldingList(
    Header: @Composable () -> Unit,
    list: List<T>,
    DisplayElement: @Composable (T) -> Unit
) {
    val Content = @Composable {
        Column {
            list.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(100.dp))
                    DisplayElement(it)
                }
            }
        }
    }
    FoldingObject(Header, Content)
}