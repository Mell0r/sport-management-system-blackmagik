package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.results_processing.FileContent

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
         *
         * @throws [IllegalArgumentException] if something went wrong.
         */
        fun fromFileContentAndCompetition(
            fileContent: FileContent,
            competition: Competition,
        ) : ParticipantsListBuilder {
            val participantsList = ParticipantsList.readFromFileContentAndCompetition(fileContent, competition)
            return fromParticipantsList(participantsList)
        }

        /**
         * Creates a new [ParticipantsListBuilder]
         * with data from file at [filePath]
         * in format consistent with [ParticipantsList.readFromFileContentAndCompetition].
         *
         * @throws [IllegalArgumentException] if something went wrong.
         */
        fun fromFileAndCompetition(
            filePath: String,
            competition: Competition,
        ) : ParticipantsListBuilder {
            TODO()
        }
    }

    fun build(): ParticipantsList {
        return ParticipantsList(list.toList())
    }
}