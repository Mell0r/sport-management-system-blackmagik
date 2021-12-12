package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent
import ru.emkn.kotlin.sms.time.Time

data class StartingProtocolEntry(
    val id: Int,
    val startTime: Time
)

data class StartingProtocol(
    val group: Group,
    val entries: List<StartingProtocolEntry>
) : CsvDumpable {
    companion object :
        CreatableFromFileContentAndCompetition<StartingProtocol> {
        override fun readFromFileContentAndCompetition(
            fileContent: FileContent,
            competition: Competition
        ): StartingProtocol {
            require(fileContent.isNotEmpty()) { "Starting protocol can't be empty!" }
            fileContent.drop(1).forEachIndexed { i, row ->
                require(row.count { it == ',' } == 1) {
                    "Line number $i contains not one comma, " +
                            "but must be exactly one!"
                }
                val tokens = row.split(',')
                requireNotNull(tokens[0].toIntOrNull()) { "First parameter must be a number(it is ID of participant)!" }
            }
            val groupLabel = fileContent[0].split(',')[0]
            val group = competition.getGroupByLabelOrNull(groupLabel)
            requireNotNull(group) {
                "Group with label \"$groupLabel\" does not exist."
            }
            val entries = fileContent
                .drop(1)
                .mapIndexed { index, row ->
                    try {
                        readEntryFromRow(row)
                    } catch (e: java.lang.IllegalArgumentException) {
                        val lineNumber =
                            index + 2 // 2 = 1 for zero-based indexing + 1 for the first line being dropped.
                        val messageWithLineNumber =
                            "Line $lineNumber: ${e.message}"
                        logErrorAndThrow(messageWithLineNumber)
                    }
                }
            return StartingProtocol(
                group,
                entries
            )
        }

        private fun readEntryFromRow(row: String): StartingProtocolEntry {
            val tokens = row.split(',')
            return StartingProtocolEntry(
                tokens[0].toIntOrNull()
                    ?: throw IllegalArgumentException("Id should be an integer value"),
                Time.fromString(tokens[1])
            )
        }

    }

    override fun dumpToCsv() =
        listOf("${group.label},") + entries.map { "${it.id},${it.startTime}" }
}