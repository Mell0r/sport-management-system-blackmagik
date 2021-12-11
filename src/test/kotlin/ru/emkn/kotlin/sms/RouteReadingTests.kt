package ru.emkn.kotlin.sms

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

internal class RouteReadingTests {

    @Test
    fun `properly reads ordered checkpoints routes`() {
        fun checkRouteLine(routeLine: String) {
            val route = readRouteFromLine(routeLine)
            assertIs<OrderedCheckpointsRoute>(route)
            assertEquals(
                listOf("chp1", "chp2", "chp3"),
                route.orderedCheckpoints
            )
            assertEquals("name1", route.name)
        }
        checkRouteLine("\$0\$name1,chp1,chp2,chp3")
        checkRouteLine("name1,chp1,chp2,chp3")
    }

    @Test
    fun `properly reads at least k checkpoints routes`() {
        val routeLine = ("\$1\$name1,2,chp1,chp2,chp3")
        val route = readRouteFromLine(routeLine)
        assertIs<AtLeastKCheckpointsRoute>(route)
        assertEquals(setOf("chp1", "chp2", "chp3"), route.checkpoints)
        assertEquals("name1", route.name)
        assertEquals(2, route.threshold)
    }

    @Test
    fun `fails on k greater than the number of checkpoints`() {
        assertFailsWith<IllegalArgumentException> {
            val routeLine = ("\$1\$name1,1000,chp1,chp2,chp3")
            readRouteFromLine(routeLine)
        }
    }
}