package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.time.Time

data class StartingProtocolEntry(
    val id: Int,
    val startTime: Time
)

data class StartingProtocol(
    val entries: List<StartingProtocolEntry>,
    val group: GroupLabelT
) {
    fun print(filePath : String) {
        TODO()
    }
}