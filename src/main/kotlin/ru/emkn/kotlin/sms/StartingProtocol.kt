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
    fun getFileName() = "Starting_protocol_of_'$group'_group"

    fun getFileContent() = entries.map{ "${it.id},${it.startTime}" }
}