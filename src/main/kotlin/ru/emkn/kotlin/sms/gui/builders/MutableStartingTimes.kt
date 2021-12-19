package ru.emkn.kotlin.sms.gui.builders

import com.github.michaelbull.result.*
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.io.readAndParseAllFilesOrErrorMessage
import ru.emkn.kotlin.sms.time.Time
import java.io.File

class MutableStartingTimes(
    val startingTimesMapping: SnapshotStateMap<Participant, Time> = mutableStateMapOf(),
) : StartingTimes(startingTimesMapping) {

    /**
     * Replaces all starting times with data from [startingProtocols]
     * with the help of [participantsList].
     *
     * If the data is not correct, the corresponding error message is put in the result.
     */
    fun replaceFromStartingProtocolsAndParticipantsList(
        startingProtocols: List<StartingProtocol>,
        participantsList: ParticipantsList,
    ): UnitOrMessage {
        startingTimesMapping.clear()
        startingProtocols.forEach { startingProtocol ->
            startingProtocol.entries.forEach { (participantID, startingTime) ->
                val participant = participantsList.getParticipantById(participantID)
                        ?: return Err("There is participant with ID=$participantID in participants list.")
                startingTimesMapping[participant] = startingTime
            }
        }
        participantsList.list.forEach { participant ->
            if (participant !in startingTimesMapping) {
                return Err("There is no starting time for participant $participant")
            }
        }
        return Ok(Unit)
    }

    /**
     * Replaces all starting times with data from starting protocol files at [files],
     * consistent with [StartingProtocol.readFromFileContentAndCompetition].
     *
     * If some error happens, the message is printed in the message.
     */
    fun replaceFromStartingProtocolFilesAndParticipantsList(
        files: List<File>,
        competition: Competition,
        participantsList: ParticipantsList,
    ): UnitOrMessage {
        val startingProtocols = readAndParseAllFilesOrErrorMessage(
            files = files,
            competition = competition,
            parser = StartingProtocol::readFromFileContentAndCompetition,
        ).mapBoth(
            success = { it },
            failure = { return Err(it) },
        )
        return replaceFromStartingProtocolsAndParticipantsList(startingProtocols, participantsList)
    }

    /**
     * Converts it to [FixedStartingTimes].
     */
    fun toFixedStartingTimes() = FixedStartingTimes(mapping)
}