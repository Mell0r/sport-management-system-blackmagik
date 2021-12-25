package ru.emkn.kotlin.sms.gui.programState

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.results_processing.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.competitionModel.CompetitionModel
import ru.emkn.kotlin.sms.gui.competitionModel.LiveGroupResultProtocolsView

/**
 * Mode 3 of the program:
 * with actual competition in the process,
 * keep a watch on the process via [competitionModel],
 * aka a list of [ParticipantCheckpointTime] triples,
 * which can be modified through [competitionModelController].
 *
 * [competition] and [participantsList] are fixed and given via constructor.
 * [competitionModel] can be changed via [competitionModelController].
 *
 * Views of [competitionModel] can become a listener of it via [CompetitionModel.addListener] method.
 */
class OnGoingCompetitionProgramState(
    override val competition: Competition,
    override val participantsList: ParticipantsList,
) : ProgramState() {
    override val competitionModel = CompetitionModel(this)
    val competitionModelController = competitionModel.Controller()

    override val liveGroupResultProtocolsView = LiveGroupResultProtocolsView(this)

    init {
        Logger.info { "Initialized OnGoingCompetitionProgramState." }
        competitionModel.addListener(liveGroupResultProtocolsView)
    }

    override fun nextProgramState() = FinishedCompetitionProgramState(
        competition = competition,
        participantsList = participantsList,
        competitionModel = competitionModel,
    )
}