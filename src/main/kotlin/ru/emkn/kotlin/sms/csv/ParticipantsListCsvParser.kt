package ru.emkn.kotlin.sms.csv

import com.github.michaelbull.result.*
import com.github.michaelbull.result.binding
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.io.FileContent
import ru.emkn.kotlin.sms.io.FileParser
import ru.emkn.kotlin.sms.time.Time

class ParticipantsListCsvParser(
    private val competition: Competition,
) : FileParser<ParticipantsList> {
    companion object {
        private const val SIZE_OF_PARTICIPANT_LIST_ROW = 7
    }

    override fun parse(fileContent: FileContent): ResultOrMessage<ParticipantsList> {
        return binding {
            ParticipantsList(
                fileContent.mapIndexed { index, row ->
                    readParticipantFromRow(row, competition).mapError { eMessage ->
                        val lineNumber = index + 1
                        val messageWithLineNumber = "Line $lineNumber: $eMessage"
                        messageWithLineNumber
                    }.bind()
                }
            )
        }
    }

    private fun readParticipantFromRow(
        row: String,
        competition: Competition
    ): ResultOrMessage<Participant> {
        val tokens = row.split(',')
        if (row.count { it == ',' } != SIZE_OF_PARTICIPANT_LIST_ROW) {
            return Err("Incorrect number of commas! Should be $SIZE_OF_PARTICIPANT_LIST_ROW.")
        }
        val id = tokens[0].toIntOrNull()
            ?: return Err("First argument(ID) of participant is not a number!")
        val age = tokens[1].toIntOrNull()
            ?: return Err("Second argument(age) of participant is not a number!")
        val groupLabel = tokens[4]
        val group = competition.getGroupByLabelOrNull(groupLabel)
            ?: return Err("Invalid group label \\\"$groupLabel\\\" of participant. No group with such label exist.")
        val participant = Participant(
            id = id,
            age = age,
            name = tokens[2],
            lastName = tokens[3],
            group = group,
            team = tokens[5],
            sportsCategory = tokens[6],
            startingTime = Time.fromString(tokens[7]),
        )
        return Ok(participant)
    }
}