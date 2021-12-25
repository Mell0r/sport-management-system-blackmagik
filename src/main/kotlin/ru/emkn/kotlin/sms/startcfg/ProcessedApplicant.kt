package ru.emkn.kotlin.sms.startcfg

import ru.emkn.kotlin.sms.*

/**
 * A [ProcessedApplicant] is someone who was admitted to competition
 * and is awaiting assignment (id and startingTime) to become a [Participant].
 */
data class ProcessedApplicant(
    val age: Int,
    val name: String,
    val lastName: String,
    val group: Group,
    val team: String,
    val sportsCategory: String,
)