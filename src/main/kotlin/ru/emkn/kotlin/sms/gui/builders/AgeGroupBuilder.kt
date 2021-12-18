package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ru.emkn.kotlin.sms.AgeGroup
import ru.emkn.kotlin.sms.OrderedCheckpointsRoute
import ru.emkn.kotlin.sms.Route

class AgeGroupBuilder(
    var label: MutableState<String> = mutableStateOf(""),
    var route: MutableState<Route> = mutableStateOf(OrderedCheckpointsRoute("", mutableListOf())),
    var ageFrom: MutableState<String> = mutableStateOf(""),
    var ageTo: MutableState<String> = mutableStateOf("")
) {
    fun toAgeGroup() = AgeGroup(label.value, route.value, ageTo.value.toInt(), ageTo.value.toInt())
}