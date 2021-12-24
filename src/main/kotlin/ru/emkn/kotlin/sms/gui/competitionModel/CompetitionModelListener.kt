package ru.emkn.kotlin.sms.gui.competitionModel

import ru.emkn.kotlin.sms.results_processing.ParticipantCheckpointTime

interface CompetitionModelListener {
    fun modelChanged(timestamps: List<ParticipantCheckpointTime>)
}