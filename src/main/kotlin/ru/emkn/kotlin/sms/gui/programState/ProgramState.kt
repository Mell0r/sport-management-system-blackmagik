package ru.emkn.kotlin.sms.gui.programState

import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.competitionModel.CompetitionModel
import ru.emkn.kotlin.sms.gui.competitionModel.LiveGroupResultProtocolsView

/**
 * Abstract class with all backend
 * properties and methods needed for GUI.
 *
 * All program modes are implemented by classes inherited from [ProgramState].
 * They are:
 * 1. ConfiguringCompetitionProgramState:
 *     Competition is being edited.
 *     Everything else is empty and cannot be changed.
 * 2. FormingStartingProtocolsProgramState:
 *     Competition is fixed and cannot be changed.
 *     Participants list is being edited.
 *     Everything else is empty and cannot be changed.
 * 3. OnGoingCompetitionProgramState:
 *     Competition,
 *     Participants list is fixed and cannot be changed.
 *     CompetitionModel, aka list of ParticipantCheckpointTime triples, is being edited.
 * 4. FinishedCompetitionProgramState:
 *     Everything is fixed and cannot be changed.
 */
abstract class ProgramState {
    // Mode 1
    abstract val competition: Competition

    // Mode 2
    abstract val participantsList: ParticipantsList

    // Mode 3
    abstract val competitionModel: CompetitionModel
    abstract val liveGroupResultProtocolsView: LiveGroupResultProtocolsView

    abstract fun nextProgramState(): ProgramState // moves on to the next program state
}