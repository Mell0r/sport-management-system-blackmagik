package ru.emkn.kotlin.sms.gui.programState

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.results_processing.GroupResultProtocol
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.results_processing.TeamResultsProtocol
import ru.emkn.kotlin.sms.gui.competitionModel.CompetitionModel
import ru.emkn.kotlin.sms.gui.competitionModel.LiveGroupResultProtocolsView
import ru.emkn.kotlin.sms.gui.safeCSVDumpableToFile
import ru.emkn.kotlin.sms.gui.writeCSVDumpablesToDirectory
import ru.emkn.kotlin.sms.results_processing.SampleTeamResultsCalculator
import java.io.File

/**
 * Mode 4 of the program:
 * competition is finished,
 * results can only be viewed and saved to file.
 *
 * [competition], [participantsList], [competitionModel]
 * are fixed and given via constructor.
 */
class FinishedCompetitionProgramState(
    override val competition: Competition,
    override val participantsList: ParticipantsList,
    override val competitionModel: CompetitionModel,
) : ProgramState() {

    override val liveGroupResultProtocolsView = LiveGroupResultProtocolsView(this)

    init {
        Logger.info { "Initialized FinishedCompetitionProgramState." }
        competitionModel.addListener(liveGroupResultProtocolsView)
    }

    override fun nextProgramState(): FinishedCompetitionProgramState = this

    private val groupResultProtocols: List<GroupResultProtocol>
        get() = liveGroupResultProtocolsView.getGroupResultProtocols()
    private val teamResultsProtocol: TeamResultsProtocol
        get() = SampleTeamResultsCalculator.calculate(groupResultProtocols)

    fun writeGroupResultProtocolsToCSV(outputDirectory: File) {
        Logger.trace { "liveGroupResultProtocols: ${liveGroupResultProtocolsView.protocols}" }
        Logger.trace { "groupResultProtocols: $groupResultProtocols" }
        writeCSVDumpablesToDirectory(groupResultProtocols, outputDirectory)
    }

    /**
     * Returns true if it successfully wrote, false otherwise.
     */
    fun writeTeamResultsProtocolToCSV(outputFile: File): Boolean {
        return safeCSVDumpableToFile(
            dumpable = teamResultsProtocol,
            filePath = outputFile.absolutePath,
        )
    }
}