package ru.emkn.kotlin.sms.gui.competitionModel

import ru.emkn.kotlin.sms.ParticipantCheckpointTime

interface CompetitionModelListener {
    fun modelChanged(timestamps: List<ParticipantCheckpointTime>)
}