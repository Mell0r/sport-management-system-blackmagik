package ru.emkn.kotlin.sms

class Competition(
    val discipline: String,
    val name: String,
    val year: Int,
    val date: String,
    val groups: List<Group>,
    val routes: List<Route>,
) {
    fun getGroupByLabelOrNull(label: String): Group? =
        groups.find { it.label == label }
}