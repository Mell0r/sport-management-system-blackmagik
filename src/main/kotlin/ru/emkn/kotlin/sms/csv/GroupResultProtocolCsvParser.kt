package ru.emkn.kotlin.sms.csv

import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import com.github.michaelbull.result.*
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.io.FileContent
import ru.emkn.kotlin.sms.io.FileParser
import ru.emkn.kotlin.sms.results_processing.FinalParticipantResult
import ru.emkn.kotlin.sms.results_processing.GroupResultProtocol
import ru.emkn.kotlin.sms.results_processing.ParticipantWithFinalResult
import ru.emkn.kotlin.sms.time.Time

class GroupResultProtocolCsvParser(
    private val competition: Competition,
    private val participantsList: ParticipantsList,
) : FileParser<GroupResultProtocol> {
    companion object {
        const val COMMAS_IN_ENTRY_ROW = 3
    }

    override fun parse(fileContent: FileContent): ResultOrMessage<GroupResultProtocol> {
        val groupName = fileContent[0].split(",").first()
        val group = competition.getGroupByLabelOrNull(groupName)
            ?: return Err("No group with name \"$groupName\" exist.")
        val rest = fileContent.drop(2) // group row and header row
        val participantAndResultList = rest.mapIndexed { index, row ->
            parseEntry(row).mapBoth(
                success = {it},
                failure = { eMessage ->
                    val lineNumber =
                        index + 3 // 3 = 1 for zero-based indexing + 2 for the first two lines being dropped
                    val messageWithLineNumber = "Line $lineNumber: $eMessage"
                    return Err(messageWithLineNumber)
                }
            )
        }
        return Ok(GroupResultProtocol(group, participantAndResultList))
    }

    private fun parseEntry(row: String): ResultOrMessage<ParticipantWithFinalResult> {
        val tokens = row.split(",")
        if (tokens.size != COMMAS_IN_ENTRY_ROW) {
            return Err("Not $COMMAS_IN_ENTRY_ROW comma separated values.")
        }
        val (_, idString, timeString) = tokens
        val id = idString.toIntOrNull()
            ?: return Err("ID is not a valid integer.")
        val participant = participantsList.getParticipantById(id)
            ?: return Err("No participant with ID = $id")
        val result = when (timeString) {
            "снят" -> FinalParticipantResult.Disqualified()
            else -> FinalParticipantResult.Finished(Time.fromString(timeString))
        }
        return Ok(ParticipantWithFinalResult(participant, result))
    }
}