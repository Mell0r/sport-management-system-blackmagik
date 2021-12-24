package ru.emkn.kotlin.sms.startcfg

import ru.emkn.kotlin.sms.csv.CsvDumpable
import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.Participant

/**
 * This class is now only a view of participants list,
 * only needed for output.
 */
data class StartingProtocol(
    val group: Group,
    val entries: List<Participant>
) : CsvDumpable {
    override fun dumpToCsv() =
        listOf("${group.label},") + entries.map { "${it.id},${it.startingTime}" }

    override fun defaultCsvFileName() =
        "starting-protocol-of-group-${group}.csv"
}