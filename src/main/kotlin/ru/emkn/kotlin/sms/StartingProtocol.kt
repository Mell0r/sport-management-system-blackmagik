package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.time.Time

data class ParticipantStart(
    val participant: Participant,
    val startTime: Time
)

data class StartingProtocol(
    val group: GroupLabelT,
    val timetable: List<ParticipantStart>
)