package ru.emkn.kotlin.sms.gui.programState

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.builders.FixedStartingTimes
import ru.emkn.kotlin.sms.gui.competitonModel.CompetitionModel
import ru.emkn.kotlin.sms.gui.competitonModel.FinishedCompetitionModelController

/**
 * Mode 4 of the program:
 * competition is finished,
 * results can only be viewed and saved to file.
 *
 * [competition], [participantsList], [startingTimes], [competitionModel]
 * are fixed and given via constructor.
 */
class FinishedCompetitionProgramState(
    override val competition: Competition,
    override val participantsList: ParticipantsList,
    override val startingTimes: FixedStartingTimes,
    override val competitionModel: CompetitionModel,
) : ProgramState() {
    override val competitionModelController = FinishedCompetitionModelController(competitionModel)

    init {
        Logger.info {"Initialized FinishedCompetitionProgramState."}
        competitionModel.addListener(super.groupResultProtocolsView)
        competitionModel.addListener(super.teamResultsProtocolView)
    }
}