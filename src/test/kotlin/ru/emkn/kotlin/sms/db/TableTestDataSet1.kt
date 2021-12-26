package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.schema.GroupsTable
import ru.emkn.kotlin.sms.db.schema.ParticipantsListTable
import ru.emkn.kotlin.sms.time.s

object TableTestDataSet1 {
    private const val competitionYear = 2021

    val rLong = OrderedCheckpointsRoute("rLong", listOf("c1", "c2", "c3", "c4", "c5"))
    val rShort = OrderedCheckpointsRoute("rShort", listOf("c0", "c1", "c2"))
    val rSet = AtLeastKCheckpointsRoute("rSet", setOf("c0", "c1", "c4", "c5"), 3)

    val gLong = AgeGroup("gLong", rLong, 10, 100, competitionYear)
    val gShort = AgeGroup("gShort", rShort, 10, 100, competitionYear)
    val gSet = AgeGroup("gSet", rSet, 10, 100, competitionYear)
    val gYoung = AgeGroup("gYoung", rShort, 10, 18, competitionYear)

    val competition = Competition(
        discipline = "",
        name = "",
        year = competitionYear,
        date = "01.01",
        groups = listOf(gLong, gShort, gSet, gYoung),
        routes = listOf(rLong, rShort, rSet),
    )

    val p1 = Participant(1, 18, "Lillian", "Lewis", gLong, "team1", "", 0.s())
    val p2 = Participant(2, 25, "Anna","Russel", gLong, "team1", "", 0.s())
    val p3 = Participant(3, 30, "Madeline","Wilson", gShort, "team1", "", 0.s())
    val p4 = Participant(4, 21, "Fiona","Avery", gSet, "team1", "", 0.s())
    val p5 = Participant(5, 12, "Gavin","Simpson", gYoung, "team1", "", 0.s())

    val p6 = Participant(6, 20, "Sarah","Nash", gLong, "team2", "", 0.s())
    val p7 = Participant(7, 24, "Piers","Cornish", gShort, "team2", "", 0.s())
    val p8 = Participant(8, 26, "Wanda","Ogden", gSet, "team2", "", 0.s())
    val p9 = Participant(9, 18, "Michael","McGrath", gSet, "team2", "", 0.s())

    val participantsList = ParticipantsList(listOf(p1, p2, p3, p4, p5, p6, p7, p8, p9))

    val participantsLists = listOf(
        participantsList,
        ParticipantsList(listOf()),
        ParticipantsList(listOf(p2, p1, p8, p7)),
        ParticipantsList(listOf(p6, p5, p8, p2)),
        ParticipantsList(listOf(p6, p3, p8, p9, p2)),
        ParticipantsList(listOf(p5, p9, p8, p1)),
        ParticipantsList(listOf(p8, p2, p1, p9, p3, p4)),
        ParticipantsList(listOf(p5, p4, p7, p8, p1, p3)),
        ParticipantsList(listOf(p3, p8, p5, p6, p2)),
    )

    fun writeAllGroups(db: Database) {
        val writer = CompetitionDbWriter(db, competition)
        writer.writeGroups()
    }

    fun clearAll(api: TestDbApi) {
        api.connectDB()
        api.deleteTable(ParticipantsListTable)
        api.deleteTable(GroupsTable)
    }
}