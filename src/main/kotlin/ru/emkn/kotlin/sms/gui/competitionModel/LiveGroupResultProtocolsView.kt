package ru.emkn.kotlin.sms.gui.competitionModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.results_processing.*

class LiveGroupResultProtocolsView(
    state: ProgramState,
) : CompetitionModelListener {
    var protocols: MutableState<List<LiveGroupResultProtocol>> =
        mutableStateOf(listOf())

    private val generator = LiveGroupResultProtocolGenerator(state.participantsList)

    override fun modelChanged(timestamps: List<ParticipantCheckpointTime>) {
        protocols.value = generator.generate(timestamps)
    }

    fun getGroupResultProtocols(): List<GroupResultProtocol> =
        protocols.value.map { it.toGroupResultProtocol() }
}