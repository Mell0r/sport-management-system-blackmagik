package ru.emkn.kotlin.sms.gui.programState

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.builders.CompetitionBuilder
import ru.emkn.kotlin.sms.gui.competitionModel.CompetitionModel
import ru.emkn.kotlin.sms.gui.competitionModel.LiveGroupResultProtocolsView

/**
 * Mode 1 of the program: configure [competition].
 *
 * [competition] can be edited through [competitionBuilder].
 * All other properties are fixed and empty.
 */
class ConfiguringCompetitionProgramState : ProgramState() {
    val competitionBuilder = CompetitionBuilder()
    override val competition: Competition
        get() = competitionBuilder.build()

    // following properties are always empty in this program mode
    override val participantsList = ParticipantsList(listOf())
    override val competitionModel = CompetitionModel(this)
    override val liveGroupResultProtocolsView = LiveGroupResultProtocolsView(this)

    init {
        Logger.info { "Initialized ConfiguredCompetitionProgramState." }
    }

    override fun nextProgramState() =
        FormingParticipantsListProgramState(competition)
}