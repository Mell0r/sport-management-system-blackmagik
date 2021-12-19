package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class AgeGroupBuilder(
    var label: MutableState<String> = mutableStateOf(""),
    var routeName: MutableState<String> = mutableStateOf(""),
    var ageFrom: MutableState<String> = mutableStateOf(""),
    var ageTo: MutableState<String> = mutableStateOf("")
)