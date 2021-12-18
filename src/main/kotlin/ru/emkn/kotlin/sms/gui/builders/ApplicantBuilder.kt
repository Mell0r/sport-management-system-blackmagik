package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class ApplicantBuilder(
    val supposedGroupLabel: MutableState<String> = mutableStateOf(""),
    val lastName: MutableState<String> = mutableStateOf(""),
    val name: MutableState<String> = mutableStateOf(""),
    val birthYear: MutableState<String> = mutableStateOf(""),
    val sportsCategory: MutableState<String> = mutableStateOf(""),
)

