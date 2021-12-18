package ru.emkn.kotlin.sms.gui.programState

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.GroupResultProtocol
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.TeamResultsProtocol
import ru.emkn.kotlin.sms.gui.builders.FixedStartingTimes
import ru.emkn.kotlin.sms.gui.competitonModel.CompetitionModel
import ru.emkn.kotlin.sms.gui.getDefaultCSVDumpablePathInDir
import ru.emkn.kotlin.sms.gui.safeCSVDumpableToFile
import ru.emkn.kotlin.sms.gui.writeCSVDumpablesToDirectory
import ru.emkn.kotlin.sms.results_processing.generateTeamResultsProtocol
import java.io.File

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

    init {
        Logger.info {"Initialized FinishedCompetitionProgramState."}
        competitionModel.addListener(super.liveGroupResultProtocolsView)
    }

    override fun nextProgramState() : FinishedCompetitionProgramState = this

    val groupResultProtocols: List<GroupResultProtocol>
        get() = super.liveGroupResultProtocolsView.getGroupResultProtocols()
    val teamResultsProtocol: TeamResultsProtocol
        get() = generateTeamResultsProtocol(
            groupResultProtocols = groupResultProtocols,
            participantsList = participantsList,
        )

    fun writeGroupResultProtocolsToCSV(outputDirectory: File) {
        writeCSVDumpablesToDirectory(groupResultProtocols, outputDirectory)
    }

    /**
     * Returns true if it successfully wrote, false otherwise.
     */
    fun writeTeamResultsProtocolToCSV(outputDirectory: File) : Boolean {
        return safeCSVDumpableToFile(
            dumpable = teamResultsProtocol,
            filePath = getDefaultCSVDumpablePathInDir(teamResultsProtocol, outputDirectory)
        )
    }
}