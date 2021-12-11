package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.CreatableFromFileContent
import ru.emkn.kotlin.sms.logErrorAndThrow
import ru.emkn.kotlin.sms.time.Time

typealias FileContent = List<String>

data class CheckpointLabelAndTime(
    val checkpointLabel: CheckpointLabelT,
    val time: Time
)

data class ParticipantTimestampsProtocol(
    val id: Int,
    val checkpointTimes: List<CheckpointLabelAndTime>
) {
    companion object : CreatableFromFileContent<ParticipantTimestampsProtocol> {
        override fun readFromFileContent(fileContent: FileContent): ParticipantTimestampsProtocol {
            val id =
                fileContent.first().split(",").first().toIntOrNull()
                    ?: logErrorAndThrow(
                        "Строка 1: id участника не является числом"
                    )
            val entries =
                fileContent.zip(1..fileContent.size).drop(1)
                    .map { (line, lineNumber) ->
                        val parts = line.split(",")
                        if (parts.size != 2)
                            logErrorAndThrow("Строка $lineNumber: не состоит из двух частей, разделенных запятой.")

                        val checkpointLabel = parts[0]
                        val time = Time.fromString(parts[1])
                        CheckpointLabelAndTime(checkpointLabel, time)
                    }
            return ParticipantTimestampsProtocol(id, entries)
        }
    }
}

data class IdAndTime(
    val id: Int,
    val time: Time
)

data class CheckpointTimestampsProtocol(
    val checkpointLabel: CheckpointLabelT,
    val participantTimes: List<IdAndTime>
) {
    companion object : CreatableFromFileContent<CheckpointTimestampsProtocol> {
        override fun readFromFileContent(fileContent: FileContent): CheckpointTimestampsProtocol {
            val checkPointLabel = fileContent.first().split(",").first()
            val entries =
                fileContent.zip(1..fileContent.size).drop(1)
                    .map { (line, lineNumber) ->
                        val parts = line.split(",")
                        if (parts.size != 2)
                            logErrorAndThrow("Строка $lineNumber: не состоит из двух частей, разделенных запятой.")
                        val id = parts[0].toIntOrNull() ?: logErrorAndThrow(
                            "Строка $lineNumber: id участника не является числом"
                        )
                        val time = Time.fromString(parts[1])
                        IdAndTime(id, time)
                    }
            return CheckpointTimestampsProtocol(checkPointLabel, entries)
        }
    }
}