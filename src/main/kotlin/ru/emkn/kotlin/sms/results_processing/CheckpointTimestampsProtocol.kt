package ru.emkn.kotlin.sms.results_processing

import com.github.michaelbull.result.Ok
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.csv.CreatableFromCsv
import ru.emkn.kotlin.sms.errAndLog
import ru.emkn.kotlin.sms.io.FileContent
import ru.emkn.kotlin.sms.time.Time

data class CheckpointTimestampsProtocol(
    val checkpointLabel: CheckpointLabelT,
    val participantTimes: List<IdAndTime>
) {
    companion object : CreatableFromCsv<CheckpointTimestampsProtocol> {
        override fun readFromCsvContent(fileContent: FileContent): ResultOrMessage<CheckpointTimestampsProtocol> {
            val checkPointLabel = fileContent.first().split(",").first()
            val entries =
                fileContent.zip(1..fileContent.size).drop(1)
                    .map { (line, lineNumber) ->
                        val parts = line.split(",")
                        if (parts.size != 2)
                            return errAndLog("Строка $lineNumber: не состоит из двух частей, разделенных запятой.")
                        val id = parts[0].toIntOrNull() ?: return errAndLog(
                            "Строка $lineNumber: id участника не является числом"
                        )
                        val time = Time.fromString(parts[1])
                        IdAndTime(id, time)
                    }
            return Ok(CheckpointTimestampsProtocol(checkPointLabel, entries))
        }
    }
}
