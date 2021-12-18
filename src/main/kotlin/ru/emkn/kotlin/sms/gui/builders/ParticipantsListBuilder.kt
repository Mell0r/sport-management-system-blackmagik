package ru.emkn.kotlin.sms.gui.builders

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapEither
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.io.ReadFailException
import ru.emkn.kotlin.sms.io.WrongFormatException
import ru.emkn.kotlin.sms.io.readAndParseFile
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File

class ParticipantsListBuilder(
    val list: SnapshotStateList<Participant> = mutableStateListOf(),
) {
    fun replaceFromParticipantsList(participantsList: ParticipantsList) {
        list.clear()
        list.addAll(participantsList.list)
    }

    companion object {
        fun fromParticipantsList(participantsList: ParticipantsList) = ParticipantsListBuilder(
            list = participantsList.list.toMutableStateList(),
        )

        /**
         * Creates a new [ParticipantsListBuilder]
         * with data from [fileContent] and [competition]
         * in format consistent with [ParticipantsList.readFromFileContentAndCompetition].
         */
        fun fromFileContentAndCompetition(
            fileContent: FileContent,
            competition: Competition,
        ) : Result<ParticipantsListBuilder, String?> {
            val participantsListOrError = com.github.michaelbull.result.runCatching {
                ParticipantsList.readFromFileContentAndCompetition(fileContent, competition)
            }
            return participantsListOrError.mapEither(
                success = {
                    fromParticipantsList(it)
                },
                failure = { exception ->
                    when (exception) {
                        is IllegalArgumentException -> exception.message
                        else -> throw exception // propagate the exception if we cannot handle it here
                    }
                }
            )
        }

        /**
         * Creates a new [ParticipantsListBuilder]
         * with data from file at [filePath]
         * in format consistent with [ParticipantsList.readFromFileContentAndCompetition].
         */
        fun fromFileAndCompetition(
            filePath: String,
            competition: Competition,
        ) : Result<ParticipantsListBuilder, String?> {
            val fileContentOrError = com.github.michaelbull.result.runCatching {
                readAndParseFile(
                    file = File(filePath),
                    competition = competition,
                    parser = ParticipantsList::readFromFileContentAndCompetition,
                )
            }
            return fileContentOrError.mapEither(
                success = {
                    fromParticipantsList(it)
                },
                failure = { exception ->
                    when (exception) {
                        is ReadFailException -> exception.message
                        is WrongFormatException -> exception.message
                        else -> throw exception // propagate the exception if we cannot handle it here
                    }
                }
            )
        }
    }

    fun build(): ParticipantsList {
        return ParticipantsList(list.toList())
    }
}