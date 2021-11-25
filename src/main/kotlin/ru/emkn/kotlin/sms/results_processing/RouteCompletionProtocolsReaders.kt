package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.time.Time

fun readRouteCompletionByCheckpointProtocol(fileContent: List<String>): RouteCompletionByCheckpointProtocol {
    val checkPointLabel = fileContent.first()
    val entries =
        fileContent.zip(1..fileContent.size).drop(1).map { (line, lineNumber) ->
            val parts = line.split(",")
            if (parts.size != 2)
                throw IllegalArgumentException("Строка $lineNumber: не состоит из двух частей, разделенных запятой.")
            val id = parts[0].toIntOrNull() ?: throw IllegalArgumentException(
                "Строка $lineNumber: id участника не является числом"
            )
            val time = Time.fromString(parts[1])
            RouteCompletionByCheckpointEntry(id, time)
        }
    return RouteCompletionByCheckpointProtocol(checkPointLabel, entries)
}

fun readRouteCompletionByParticipantProtocol(fileContent: List<String>): RouteCompletionByParticipantProtocol {
    val id =
        fileContent.first().toIntOrNull() ?: throw IllegalArgumentException(
            "Строка $1: id участника не является числом"
        )
    val entries =
        fileContent.zip(1..fileContent.size).drop(1).map { (line, lineNumber) ->
            val parts = line.split(",")
            if (parts.size != 2)
                throw IllegalArgumentException("Строка $lineNumber: не состоит из двух частей, разделенных запятой.")
            val checkpointLabel = parts[0]
            val time = Time.fromString(parts[1])
            RouteCompletionByParticipantEntry(checkpointLabel, time)
        }
    return RouteCompletionByParticipantProtocol(id, entries)
}