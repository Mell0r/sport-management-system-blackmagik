package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent
import ru.emkn.kotlin.sms.time.Time

data class StartingProtocolEntry(
    val id: Int,
    val startTime: Time
)

data class StartingProtocol(
    val group: GroupLabelT,
    val entries: List<StartingProtocolEntry>
) : CsvDumpable{
    companion object: CreatableFromFileContent<StartingProtocol> {
        override fun readFromFileContent(fileContent: FileContent): StartingProtocol {
            TODO("Not yet implemented")
        }
    }

    fun getFileName() = "Starting_protocol_of_'$group'_group"

    override fun dumpToCsv() = entries.map{ "${it.id},${it.startTime}" }
}