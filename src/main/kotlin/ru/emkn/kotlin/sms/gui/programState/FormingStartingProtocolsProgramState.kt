package ru.emkn.kotlin.sms.gui.programState

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.builders.MutableStartingTimes
import ru.emkn.kotlin.sms.gui.builders.ParticipantsListBuilder
import ru.emkn.kotlin.sms.gui.competitionModel.CompetitionModel

/**
 * Mode 2 of the program:
 * with set competition, form a participants list and decide on starting times.
 *
 * [competition] is fixed and given via constructor.
 * [participantsList] can be edited via [ParticipantsListBuilder].
 * [startingTimes] is an instance of [MutableStartingTimes],
 * which means the starting times can be changed.
 * [competitionModel] is empty and cannot be changed.
 */
class FormingStartingProtocolsProgramState(
    override val competition: Competition
) : ProgramState() {
    val participantsListBuilder = ParticipantsListBuilder()
    override val participantsList: ParticipantsList
        get() = participantsListBuilder.build()

    override val startingTimes = MutableStartingTimes()

    override val competitionModel = CompetitionModel(this)

    init {
        Logger.info { "Initialized FormingStartingProtocolsProgramState." }
    }

    override fun nextProgramState() = OnGoingCompetitionProgramState(
        competition = competition,
        participantsList = participantsList,
        startingTimes = startingTimes.toFixedStartingTimes(),
    )
}