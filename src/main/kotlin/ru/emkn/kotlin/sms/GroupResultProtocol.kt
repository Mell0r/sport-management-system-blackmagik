package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent
import ru.emkn.kotlin.sms.time.Time

typealias Score = Int

data class GroupResultProtocolEntry(
    val participant: Participant,
    val totalTime: Time?, // seconds
    val placeInGroup: Int
)

class GroupResultProtocol(
    val groupName: GroupLabelT,
    val entries: List<GroupResultProtocolEntry>
    // sorted by placeInGroup
) : CsvDumpable {
    companion object : CreatableFromFileContent<GroupResultProtocol> {
        override fun readFromFileContent(fileContent: FileContent): GroupResultProtocol {
            TODO("Not yet implemented")
        }
    }

    override fun dumpToCsv(): FileContent {
        TODO("Not yet implemented")
    }
}