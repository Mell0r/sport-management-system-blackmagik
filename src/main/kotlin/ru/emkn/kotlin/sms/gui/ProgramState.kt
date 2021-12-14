package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList

/**
 * Abstract class with all properties and methods
 * needed for GUI.
 */
abstract class ProgramState {
    abstract val competition: Competition
    abstract val participantsList: ParticipantsList

    abstract val competitionModel: CompetitionModel
    val groupResultProtocolsView = GroupResultProtocolsView()
    val teamResultsProtocolView = TeamResultsProtocolView()
    init {
        competitionModel.addListener(groupResultProtocolsView)
        competitionModel.addListener(teamResultsProtocolView)
    }
}