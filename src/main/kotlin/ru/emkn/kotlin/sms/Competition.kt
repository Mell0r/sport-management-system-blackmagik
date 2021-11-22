package ru.emkn.kotlin.sms

import kotlinx.datetime.LocalDate

class Competition(
    val name: String,
    val discipline: String,
    val date: LocalDate,
    val groupCondition: Map<String, (Participant) -> Boolean>,
    val groupToRouteMapping: Map<String, Route>,
    val groups: List<String>
)