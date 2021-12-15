package ru.emkn.kotlin.sms.gui.builders

import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.results_processing.FileContent

class ParticipantsListBuilder {
    private val listBuilder = UniqueListBuilder<Participant>(
        equals = { participant1, participant2 ->
            participant1.id == participant2.id
        }
    )

    private val listeners: MutableList<BuilderListener<ParticipantsListBuilder>> = mutableListOf()
    fun addListener(listener: BuilderListener<ParticipantsListBuilder>) {
        listeners.add(listener)
    }

    private fun notifyAllListeners() {
        listeners.forEach {
            it.dataChanged(this)
        }
    }

    private fun checkModification(modified: Boolean) : Boolean {
        if (modified) {
            notifyAllListeners()
        }
        return modified
    }

    /**
     * @return true if it successfully added [participant],
     * false if a participant with the same id already present in the list.
     */
    fun addParticipant(participant: Participant) : Boolean {
        TODO()
    }

    /**
     * @return true if it successfully removed [participant],
     * false if [participant] was not present in the list.
     */
    fun removeParticipant(participant: Participant) : Boolean {
        TODO()
    }

    /**
     * Replaces all data in builder with data from [participantsList].
     * Useful for loading participants list and then modifying it in GUI.
     *
     * @throws [IllegalArgumentException] if something went wrong.
     */
    fun replaceFromParticipantsList(participantsList: ParticipantsList) {
        require(listBuilder.replaceList(participantsList.list))
        notifyAllListeners()
    }

    /**
     * Replaces all data in builder with data from [fileContent]
     * in format consistent with [ParticipantsList.readFromFileContentAndCompetition].
     *
     * @return true if the replacement was successful, false if [fileContent] had invalid format.
     */
    fun replaceFromFileContentAndCompetition(
        fileContent: FileContent,
        competition: Competition,
    ) : Boolean {
        val participantsList = try {
            ParticipantsList.readFromFileContentAndCompetition(fileContent, competition)
        } catch (e: IllegalArgumentException) {
            return false
        }
        replaceFromParticipantsList(participantsList)
        return true
    }

    /**
     * Replaces all data in builder with data from file at [filePath]
     * in format consistent with [ParticipantsList.readFromFileContentAndCompetition].
     *
     * @return true if the replacement was successful, false if the file was corrupted or was not exist.
     */
    fun replaceFromFileAndCompetition(
        filePath: String,
        competition: Competition,
    ) : Boolean {
        TODO()
    }

    fun build(): ParticipantsList {
        TODO()
    }
}