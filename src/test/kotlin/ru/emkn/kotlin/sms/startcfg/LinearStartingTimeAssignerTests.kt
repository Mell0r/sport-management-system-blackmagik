package ru.emkn.kotlin.sms.startcfg

import ru.emkn.kotlin.sms.AgeGroup
import ru.emkn.kotlin.sms.OrderedCheckpointsRoute
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.time.Time
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LinearStartingTimeAssignerTests {
    private val route = OrderedCheckpointsRoute("", mutableListOf())
    private val group = AgeGroup("group", route, 0, 1000, 2021)

    private fun baseTest(
        n: Int = 50,
        start: Time = Time(12, 0, 0),
        step: Time = Time(0, 1, 0),
        rnd: Random = Random(13)
    ) {
        val newApplicant = {
            ProcessedApplicant(
                rnd.nextInt(10, 50),
                "Name N${rnd.nextInt()}",
                "LastName ${rnd.nextInt()}",
                group,
                "",
                ""
            )
        }
        val assigner = LinearStartingTimeAssigner(start, step)
        val testApplicants = generateSequence(newApplicant) { newApplicant() }
            .take(n).toList()
        val participantList = assigner.assign(testApplicants)
        val assignedTimes = participantList.list
            .map(Participant::startingTime)
            .sorted()
        val expectedTimes = generateSequence(start) { prev -> prev + step }
            .take(n).toList()
        assertEquals(expectedTimes, assignedTimes)
    }

    @Test
    fun `linearAssigner test`() {
        baseTest(n = 50)
        baseTest(n = 500)
        baseTest(n = 0)
        baseTest(n = 20, step = Time(0, 2, 0))
    }
}
