package ru.emkn.kotlin.sms.gui.frontend.modes

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.gui.builders.FixedStartingTimes
import ru.emkn.kotlin.sms.gui.programState.OnGoingCompetitionProgramState
import ru.emkn.kotlin.sms.time.Time

val mainRoute = OrderedCheckpointsRoute("main", mutableListOf("1", "2", "3"))
val m10 = AgeGroup("M10", mainRoute, 8, 10)
val f10 = AgeGroup("F10", mainRoute, 8, 10)
val sampleCompetition = Competition(
    "",
    "name",
    0,
    "",
    listOf(m10, f10),
    listOf(mainRoute)
)
val p1 = Participant(0, 9, "A", "A", m10, "t1", "")
val p2 = Participant(1, 9, "B", "B", m10, "t1", "")
val p3 = Participant(2, 9, "C", "C", m10, "t1", "")
val q1 = Participant(0, 9, "D", "D", f10, "t1", "")
val q2 = Participant(1, 9, "E", "E", f10, "t1", "")
val q3 = Participant(2, 9, "F", "F", f10, "t1", "")
val sampleParticipants = listOf(p1, p2, p3, q1, q2, q3)
val sampleOngoingCompetition = OnGoingCompetitionProgramState(
    sampleCompetition,
    ParticipantsList(sampleParticipants),
    startingTimes = FixedStartingTimes(
        mapOf(
            p1 to Time(0),
            p2 to Time(0),
            p3 to Time(0),
            q1 to Time(0),
            q2 to Time(0),
            q3 to Time(0),
        )
    )
//).also {it.competitionModelController.addTimestamp(ParticipantCheckpointTime(p1, "1", Time(1)))}
).also {
    it.competitionModel.timestamps.add(
        ParticipantCheckpointTime(
            p1,
            "1",
            Time(1)
        )
    )
    it.competitionModel.timestamps.add(
        ParticipantCheckpointTime(
            p1,
            "1",
            Time(81)
        )
    )
    // p1 disqualified
    it.competitionModel.timestamps.add(
        ParticipantCheckpointTime(
            p2,
            "1",
            Time(40)
        )
    )
    it.competitionModel.timestamps.add(
        ParticipantCheckpointTime(
            p2,
            "2",
            Time(53)
        )
    )
    it.competitionModel.timestamps.add(
        ParticipantCheckpointTime(
            p2,
            "3",
            Time(106)
        )
    )
    it.competitionModel.timestamps.add(
        ParticipantCheckpointTime(
            p3,
            "1",
            Time(36)
        )
    )
    it.competitionModel.timestamps.add(
        ParticipantCheckpointTime(
            q2,
            "1",
            Time(39)
        )
    )
}