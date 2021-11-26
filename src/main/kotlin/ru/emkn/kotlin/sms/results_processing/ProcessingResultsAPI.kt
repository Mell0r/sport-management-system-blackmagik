package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.GroupLabelT
import ru.emkn.kotlin.sms.Participant

typealias FileContent = List<String>

class FromStartProtocols(startProtocols: List<FileContent>) {
    /**
     * Assumes that no completion protocols are missing.
     * @return Pairs groupName to FileContent with results of the respective group
     */
    fun generateResultFilesFromByParticipantsRouteCompletionProtocols(
        completionProtocols: List<FileContent>,
        idToParticipantMapping: (Int) -> Participant,
        competition: Competition
    ): Map<GroupLabelT, FileContent> = TODO()

    fun generateResultFileFromByCheckpointRouteCompletionProtocols(
        completionProtocols: List<FileContent>,
        idToParticipantMapping: (Int) -> Participant,
        competition: Competition
    ): Map<GroupLabelT, FileContent> = TODO()
}