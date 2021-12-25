@file:Suppress("FunctionName")

package ru.emkn.kotlin.sms.gui.frontend.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun TextBox(text: String = "Item") {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.height(32.dp)
            .width(400.dp)
            .background(color = Color(200, 0, 0, 20))
            .padding(start = 10.dp),
        contentAlignment = androidx.compose.ui.Alignment.Companion.CenterStart
    ) {
        androidx.compose.material.Text(text = text)
    }
}

interface Field<T> {
    val name: String
    val stringRepresentation: (T) -> String
    val comparator: Comparator<T>
    val width: Float
}

class FieldComparableBySelector<T, R : Comparable<R>>(
    override val name: String,
    override val stringRepresentation: (T) -> String,
    selector: (T) -> R,
    override val width: Float
) : Field<T> {
    override val comparator: Comparator<T> =
        Comparator { lhs, rhs -> selector(lhs).compareTo(selector(rhs)) }

}

@Composable
fun <T> SortableTable(values: List<T>, fields: List<Field<T>>) {
    val fieldForSorting =
        androidx.compose.runtime.mutableStateOf(fields.first())
    val valuesSorted =
        androidx.compose.runtime.mutableStateOf(values.sortedWith(fields.first().comparator))
    androidx.compose.foundation.layout.Column {
        drawButtonRow(fields, valuesSorted, fieldForSorting)
        drawValueRows(valuesSorted, fields)
    }
}

@Composable
private fun <T> drawButtonRow(
    fields: List<Field<T>>,
    valuesSorted: androidx.compose.runtime.MutableState<List<T>>,
    fieldForSorting: androidx.compose.runtime.MutableState<Field<T>>
) {
    androidx.compose.foundation.layout.Row {
        for (field in fields) {
            androidx.compose.material.Button(
                modifier = androidx.compose.ui.Modifier.width(
                    field.width.dp
                ), onClick = {
                    valuesSorted.value =
                        valuesSorted.value.sortedWith(field.comparator)
                    fieldForSorting.value = field
                }) {
                val text = field.name + when {
                    field === fieldForSorting.value -> " $downArrow"
                    else -> ""
                }
//                TextBox(text)
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier.height(32.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Companion.CenterStart
                ) {
                    androidx.compose.material.Text(text)
                }
            }
        }
    }
}

@Composable
private fun <T> drawValueRows(
    valuesSorted: androidx.compose.runtime.MutableState<List<T>>,
    fields: List<Field<T>>
) {
    for (value in valuesSorted.value) {
        androidx.compose.foundation.layout.Row {
            for (field in fields) {
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier.width(
                        field.width.dp
                    )
                ) {
                    TextBox(field.stringRepresentation(value))
                }
            }
        }
    }
}