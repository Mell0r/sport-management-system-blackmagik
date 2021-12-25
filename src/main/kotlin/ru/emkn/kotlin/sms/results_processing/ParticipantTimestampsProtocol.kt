package ru.emkn.kotlin.sms.results_processing

import com.github.michaelbull.result.Ok
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.csv.CreatableFromCsv
import ru.emkn.kotlin.sms.errAndLog
import ru.emkn.kotlin.sms.io.FileContent
import ru.emkn.kotlin.sms.time.Time

data class ParticipantTimestampsProtocol(
    val id: Int,
    val checkpointTimes: List<CheckpointAndTime>
) {
    companion object : CreatableFromCsv<ParticipantTimestampsProtocol> {
        override fun readFromCsvContent(fileContent: FileContent): ResultOrMessage<ParticipantTimestampsProtocol> {
            val id =
                fileContent.first().split(",").first().toIntOrNull()
                    ?: return errAndLog(
                        "Строка 1: id участника не является числом"
                    )
            val entries = fileContent.zip(1..fileContent.size).drop(1)
                    .map { (line, lineNumber) ->
                        val parts = line.split(",")
                        if (parts.size != 2)
                            return errAndLog("Строка $lineNumber: не состоит из двух частей, разделенных запятой.")

                        val checkpointLabel = parts[0]
                        val time = Time.fromString(parts[1])
                        CheckpointAndTime(checkpointLabel, time)
                    }
            return Ok(ParticipantTimestampsProtocol(id, entries))
        }
    }
}