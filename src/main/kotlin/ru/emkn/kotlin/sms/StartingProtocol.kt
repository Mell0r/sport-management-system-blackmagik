package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.time.Time

data class ParticipantStart(
    val participant: Participant,
    val startTime: Time
)

class StartingProtocol(
    val group: GroupLabelT,
    val timetable: List<ParticipantStart>
) {
    fun print(filePath: String) {
        TODO()
    }
}