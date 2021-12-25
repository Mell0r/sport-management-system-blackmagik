package ru.emkn.kotlin.sms.gui.programState

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.competitionModel.CompetitionModel
import ru.emkn.kotlin.sms.gui.competitionModel.LiveGroupResultProtocolsView

/**
 * Mode 2 of the program:
 * with set competition, form a participants list and decide on starting times.
 *
 * [competition] is fixed and given via constructor.
 * [participantsList] is mutable and can be replaced when necessary.
 * [competitionModel] is empty and cannot be changed.
 */
class FormingParticipantsListProgramState(
    override val competition: Competition
) : ProgramState() {
    override var participantsList: ParticipantsList = ParticipantsList(listOf())

    override val competitionModel = CompetitionModel(this)
    override val liveGroupResultProtocolsView = LiveGroupResultProtocolsView(this)

    init {
        Logger.info { "Initialized FormingStartingProtocolsProgramState." }
    }

    override fun nextProgramState() = OnGoingCompetitionProgramState(
        competition = competition,
        participantsList = participantsList,
    )
}