package ru.emkn.kotlin.sms

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.unwrap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

@OptIn(ExperimentalStdlibApi::class)
internal class RouteReadingTests {

    @Test
    fun `properly reads ordered checkpoints routes`() {
        fun checkRouteLine(routeLine: String) {
            val route = readRouteFromLine(routeLine)
            assertIs<Ok<OrderedCheckpointsRoute>>(route)
            assertEquals(
                listOf("chp1", "chp2", "chp3"),
                route.value.orderedCheckpoints
            )
            assertEquals("name1", route.value.name)
        }
        checkRouteLine("\$0\$name1,chp1,chp2,chp3")
        checkRouteLine("name1,chp1,chp2,chp3")
    }

    @Test
    fun `Properly writes and reads back ordered checkpoints routes`() {
        fun checkRoute(route: OrderedCheckpointsRoute) {
            assertEquals(
                route,
                readRouteFromLine(route.dumpToCsvString()).unwrap()
            )
        }
        checkRoute(OrderedCheckpointsRoute("orderedRoute1", mutableListOf("c1", "c2", "c3")))
        checkRoute(OrderedCheckpointsRoute("orderedRoute2", mutableListOf("c2", "c1", "c3")))
        checkRoute(OrderedCheckpointsRoute("orderedRoute3", mutableListOf("c1", "c2", "c1", "c4", "d")))
        checkRoute(OrderedCheckpointsRoute("n", mutableListOf("c")))
    }

    @Test
    fun `properly reads at least k checkpoints routes`() {
        val routeLine = ("\$1\$name1,2,chp1,chp2,chp3")
        val route = readRouteFromLine(routeLine)
        assertIs<Ok<AtLeastKCheckpointsRoute>>(route)
        assertEquals(setOf("chp1", "chp2", "chp3"), route.value.checkpoints)
        assertEquals("name1", route.value.name)
        assertEquals(2, route.value.threshold)
    }

    @Test
    fun `Properly writes and reads back at least k checkpoints routes`() {
        fun checkRoute(route: AtLeastKCheckpointsRoute) {
            assertEquals(
                route,
                readRouteFromLine(route.dumpToCsvString()).unwrap()
            )
        }
        checkRoute(AtLeastKCheckpointsRoute("atLeastKRoute1", mutableSetOf("c1", "c2", "c3"), 1))
        checkRoute(AtLeastKCheckpointsRoute("atLeastKRoute2", mutableSetOf("c2", "c1", "c3"), 2))
        checkRoute(AtLeastKCheckpointsRoute("atLeastKRoute3", mutableSetOf("c1", "c2", "c1", "c4", "d"), 3))
        checkRoute(AtLeastKCheckpointsRoute("n", mutableSetOf("c"), 1))
    }

    @Test
    fun `fails on k greater than the number of checkpoints`() {
        assertFailsWith<IllegalArgumentException> {
            val routeLine = ("\$1\$name1,1000,chp1,chp2,chp3")
            readRouteFromLine(routeLine)
        }
    }
}