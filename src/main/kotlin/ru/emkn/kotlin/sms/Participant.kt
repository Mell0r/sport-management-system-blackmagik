package ru.emkn.kotlin.sms

typealias GroupLabelT = String

data class Participant(
    val id: Int,
    val age: Int,
    val name: String,
    val lastName: String,
    val group: GroupLabelT,
    val team: String,
    val sportsCategory: String // from fixed options from config
)

fun readFromApplication(rawCSV: List<List<String>>): List<Participant> = TODO()