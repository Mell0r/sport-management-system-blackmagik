package ru.emkn.kotlin.sms

typealias StartingTimeT = String

data class StartingProtocolEntry(
    val id: Int,
    val startTime: StartingTimeT
)

data class StartingProtocol(
    val entries: List<StartingProtocolEntry>,
    val group: GroupLabelT
)