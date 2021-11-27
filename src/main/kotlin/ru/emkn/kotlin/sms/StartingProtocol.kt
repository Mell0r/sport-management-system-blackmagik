package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.time.Time

typealias StartingTimeT = String

data class StartingProtocolEntry(
    val id: Int,
    val startTime: Time
)

data class StartingProtocol(
    val group: GroupLabelT,
    val entries: List<StartingProtocolEntry>
) {
    fun print(filePath : String) {
        TODO()
    }
}