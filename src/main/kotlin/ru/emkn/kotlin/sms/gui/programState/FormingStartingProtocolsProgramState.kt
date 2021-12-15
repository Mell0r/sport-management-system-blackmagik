package ru.emkn.kotlin.sms.gui.programState

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.builders.MutableStartingTimes
import ru.emkn.kotlin.sms.gui.builders.ParticipantsListBuilder
import ru.emkn.kotlin.sms.gui.competitonModel.CompetitionModel
import ru.emkn.kotlin.sms.gui.competitonModel.FinishedCompetitionModelController

/**
 * Mode 2 of the program:
 * with set competition, form a participants list and decide on starting times.
 *
 * [competition] is fixed and given via constructor.
 * [participantsList] can be edited via [ParticipantsListBuilder].
 * [startingTimes] is an instance of [MutableStartingTimes], which means
 * the starting times can be changed.
 * [competitionModel] is empty and cannot be changed.
 *
 * Views of [participantsList] can become a listener via [ParticipantsListBuilder.addListener] method.
 * Views of [startingTimes] can become a listener via [MutableStartingTimes.addListener] method.
 */
class FormingStartingProtocolsProgramState(override val competition: Competition) : ProgramState() {
    val participantsListBuilder = ParticipantsListBuilder()
    override val participantsList: ParticipantsList
        get() = participantsListBuilder.build()

    override val startingTimes = MutableStartingTimes()

    override val competitionModel = CompetitionModel()
    override val competitionModelController = FinishedCompetitionModelController(competitionModel)

    init {
        Logger.info {"Initialized FormingStartingProtocolsProgramState."}
    }
}