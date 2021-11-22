package ru.emkn.kotlin.sms

typealias Score = Int

data class GroupResultProtocolEntry(
    val participant: Participant,
    val totalTime: Int, // seconds
    val placeInGroup: Int
)

class GroupResultProtocol(
    val groupName: GroupLabelT,
    val entries: List<GroupResultProtocolEntry>
    // sorted by placeInGroup
)