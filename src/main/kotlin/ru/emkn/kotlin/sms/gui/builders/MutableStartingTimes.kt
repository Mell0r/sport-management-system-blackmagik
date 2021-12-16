package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.StartingProtocol
import ru.emkn.kotlin.sms.results_processing.FileContent
import ru.emkn.kotlin.sms.time.Time

class MutableStartingTimes (
    val startingTimesMapping: SnapshotStateMap<Participant, Time> = mutableStateMapOf(),
) : StartingTimes(startingTimesMapping) {

    /**
     * Replaces all starting times with data from [startingProtocols].
     *
     * @throws [IllegalArgumentException] if something went wrong.
     */
    fun replaceFromStartingProtocols(startingProtocols: List<StartingProtocol>) : Boolean {
        TODO()
    }

    /**
     * Replaces all starting times with data from starting protocol [fileContents],
     * consistent with [StartingProtocol.readFromFileContentAndCompetition].
     *
     * @return true if the replacement was successful, false some file content had invalid format.
     */
    fun replaceFromStartingProtocolFileContents(
        fileContents: List<FileContent>,
        competition: Competition
    ) : Boolean {
        TODO()
    }

    /**
     * Replaces all starting times with data from starting protocol files at [filePaths],
     * consistent with [StartingProtocol.readFromFileContentAndCompetition].
     *
     * @return true if the replacement was successful, false some file was not exist or had invalid format.
     */
    fun replaceFromStartingProtocolFiles(
        filePaths: List<String>,
        competition: Competition,
    ) : Boolean {
        TODO()
    }

    /**
     * Converts it to [FixedStartingTimes].
     */
    fun toFixedStartingTimes() = FixedStartingTimes(mapping)
}