package ru.emkn.kotlin.sms.gui.frontend

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.gui.programState.FormingParticipantsListProgramState
import ru.emkn.kotlin.sms.gui.programState.OnGoingCompetitionProgramState
import ru.emkn.kotlin.sms.results_processing.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.time.Time

const val sampleCompetitionYear = 2021
val mainRoute = OrderedCheckpointsRoute("main", mutableListOf("1", "2", "3"))
val m10 = AgeGroup("M10", mainRoute, 8, 48, sampleCompetitionYear)
val f10 = AgeGroup("F10", mainRoute, 8, 50, sampleCompetitionYear)
val sampleCompetition = Competition(
    "",
    "name",
    sampleCompetitionYear,
    "",
    listOf(m10, f10),
    listOf(mainRoute)
)
val p1 = Participant(0, 9, "A", "A", m10, "t1", "", Time(0))
val p2 = Participant(1, 9, "B", "B", m10, "t1", "", Time(0))
val p3 = Participant(2, 9, "C", "C", m10, "t1", "", Time(0))
val q1 = Participant(3, 9, "D", "D", f10, "t1", "", Time(0))
val q2 = Participant(4, 9, "E", "E", f10, "t1", "", Time(0))
val q3 = Participant(5, 9, "F", "F", f10, "t1", "", Time(0))
val sampleParticipants = listOf(p1, p2, p3, q1, q2, q3)
val sampleOngoingCompetition = OnGoingCompetitionProgramState(
    sampleCompetition,
    ParticipantsList(sampleParticipants),
).apply {
    competitionModelController.addTimestamp(
        ParticipantCheckpointTime(p1, "1", Time(1))
    )
    competitionModelController.addTimestamp(
        ParticipantCheckpointTime(p1, "1", Time(81))
    )
    // p1 disqualified
    competitionModelController.addTimestamp(
        ParticipantCheckpointTime(p2, "1", Time(40))
    )
    competitionModelController.addTimestamp(
        ParticipantCheckpointTime(p2, "2", Time(53))
    )
    competitionModelController.addTimestamp(
        ParticipantCheckpointTime(p2, "3", Time(106))
    )
    competitionModelController.addTimestamp(
        ParticipantCheckpointTime(p3, "1", Time(36))
    )
    competitionModelController.addTimestamp(
        ParticipantCheckpointTime(q2, "1", Time(39))
    )
}

val sampleFormingStartingProtocolProgramState =
    FormingParticipantsListProgramState(
        sampleCompetition
    )

val sampleFinishedCompetitionState = sampleOngoingCompetition.nextProgramState()
