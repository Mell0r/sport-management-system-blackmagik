package ru.emkn.kotlin.sms.gui.programState

import androidx.compose.runtime.mutableStateOf
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.builders.CompetitionBuilder
import ru.emkn.kotlin.sms.gui.builders.FixedStartingTimes
import ru.emkn.kotlin.sms.gui.competitonModel.CompetitionModel

/**
 * Mode 1 of the program: configure [competition].
 *
 * [competition] can be edited through [competitionBuilder].
 * All other properties are fixed and empty.
 */
class ConfiguringCompetitionProgramState : ProgramState() {
    val competitionBuilder = mutableStateOf(CompetitionBuilder())
    override val competition: Competition
        get() = competitionBuilder.value.build()

    // following properties are always empty in this program mode
    override val participantsList = ParticipantsList(listOf())
    override val startingTimes = FixedStartingTimes()
    override val competitionModel = CompetitionModel(this)

    init {
        Logger.info { "Initialized ConfiguredCompetitionProgramState." }
    }

    override fun nextProgramState() =
        FormingStartingProtocolsProgramState(competition)
}