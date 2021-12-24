package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Group

data class LiveGroupResultProtocol(
    val group: Group,
    val entries: List<ParticipantWithLiveResult>,
    // sorted by placeInGroup
) {
    init {
        require(entries == entries.sortedBy { it.liveResult })
    }

    fun toGroupResultProtocol() = GroupResultProtocol(
        group = group,
        entries = entries.map { it.toParticipantWithFinalResult() }
    )
}
