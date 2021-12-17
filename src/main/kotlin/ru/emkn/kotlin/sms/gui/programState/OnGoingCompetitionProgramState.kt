package ru.emkn.kotlin.sms.gui.programState

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.competitonModel.CompetitionModel
import ru.emkn.kotlin.sms.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.gui.builders.FixedStartingTimes

/**
 * Mode 3 of the program:
 * with actual competition in the process,
 * keep a watch on the process via [competitionModel],
 * aka a list of [ParticipantCheckpointTime] triples,
 * which can be modified through [competitionModelController].
 *
 * [competition], [participantsList] and [startingTimes]
 * are fixed and given via constructor.
 * [competitionModel] can be changed via [competitionModelController].
 *
 * Views of [competitionModel] can become a listener of it via [CompetitionModel.addListener] method.
 */
class OnGoingCompetitionProgramState(
    override val competition: Competition,
    override val participantsList: ParticipantsList,
    override val startingTimes: FixedStartingTimes,
) : ProgramState() {
    override val competitionModel = CompetitionModel(this)
    val competitionModelController = competitionModel.Controller()

    init {
        Logger.info{"Initialized OnGoingCompetitionProgramState."}
        competitionModel.addListener(super.liveGroupResultProtocolsView)
        competitionModel.addListener(super.teamResultsProtocolView)
    }

    override fun nextProgramState() = FinishedCompetitionProgramState(
        competition = competition,
        participantsList = participantsList,
        startingTimes = startingTimes,
        competitionModel = competitionModel,
    )
}