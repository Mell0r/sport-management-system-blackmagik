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
            require(fileContent.isNotEmpty()) { "Starting protocol can't be empty!" }
            fileContent.drop(1).forEachIndexed { i, row ->
                require(row.count { it == ',' } == 1) { "Line number $i contains not one comma, " +
                        "but must be exactly one!" }
                val splittedRow = row.split(',')
                requireNotNull(splittedRow[0].toIntOrNull()) { "First parameter must be a number(it is ID of participant)!" }
            }
            return StartingProtocol(fileContent[0].split(',')[0], fileContent.drop(1)
                .map { row ->
                    val splittedRow = row.split(',')
                    StartingProtocolEntry(splittedRow[0].toInt(), Time.fromString(splittedRow[1]))
                })
        }

    }


    override fun dumpToCsv() = entries.map{ "${it.id},${it.startTime}" }
}