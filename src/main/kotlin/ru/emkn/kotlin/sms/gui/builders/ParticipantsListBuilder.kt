package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.github.michaelbull.result.*
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.io.readAndParseFileOrErrorMessage
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File

class ParticipantsListBuilder(
    val list: SnapshotStateList<Participant> = mutableStateListOf(),
) {
    fun replaceFromParticipantsList(participantsList: ParticipantsList) {
        list.clear()
        list.addAll(participantsList.list)
    }
    fun replaceFromParticipantsListBuilder(participantsListBuilder: ParticipantsListBuilder) {
        list.clear()
        list.addAll(participantsListBuilder.list)
    }

    companion object {
        fun fromParticipantsList(participantsList: ParticipantsList) =
            ParticipantsListBuilder(
                list = participantsList.list.toMutableStateList(),
            )

        /**
         * Creates a new [ParticipantsListBuilder]
         * with data from file at [filePath] and with the help of [competition]
         * in format consistent with [ParticipantsList.readFromFileContentAndCompetition].
         *
         * If some exception happens, it is in the result message.
         */
        fun fromFileAndCompetition(
            filePath: String,
            competition: Competition,
        ): ResultOrMessage<ParticipantsListBuilder> {
            return readAndParseFileOrErrorMessage(
                file = File(filePath),
                competition = competition,
                parser = ParticipantsList::readFromFileContentAndCompetition,
            ).map { participantsList -> fromParticipantsList(participantsList) }
        }
    }

    fun build(): ParticipantsList {
        return ParticipantsList(list.toList())
    }
}