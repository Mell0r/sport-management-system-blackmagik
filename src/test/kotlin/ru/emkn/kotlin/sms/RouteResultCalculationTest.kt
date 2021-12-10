package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.CheckpointLabelAndTime
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.Test
import kotlin.test.assertEquals

class RouteResultCalculationTest {
    @Test
    fun `result on properly passed ordered checkpoints protocols are proper`() {
        val route = OrderedCheckpointsRoute("", listOf("1", "2", "3"))
        val passTime = route.calculateResultingTime(
            listOf(
                CheckpointLabelAndTime("1", Time(5)),
                CheckpointLabelAndTime("2", Time(7)),
                CheckpointLabelAndTime("3", Time(9)),
            ), Time(1)
        )
        assertEquals(Time(8), passTime)
    }

    @Test
    fun `result on ordered checkpoints protocols with wrong order are proper`() {
        val route = OrderedCheckpointsRoute("", listOf("1", "2", "3"))
        val passTime = route.calculateResultingTime(
            listOf(
                CheckpointLabelAndTime("1", Time(5)),
                CheckpointLabelAndTime("2", Time(11)),
                CheckpointLabelAndTime("3", Time(9)),
            ), Time(1)
        )
        assertEquals(null, passTime)
    }

    @Test
    fun `result on ordered checkpoints protocols with false start are proper`() {
        val route = OrderedCheckpointsRoute("", listOf("1", "2", "3"))
        val passTime = route.calculateResultingTime(
            listOf(
                CheckpointLabelAndTime("1", Time(5)),
                CheckpointLabelAndTime("2", Time(11)),
                CheckpointLabelAndTime("3", Time(9)),
            ), Time(100)
        )
        assertEquals(null, passTime)
    }

    @Test
    fun `result on ordered checkpoints protocols with repeated checkpoints are proper`() {
        val route = OrderedCheckpointsRoute("", listOf("1", "2", "3", "1"))
        val passTime = route.calculateResultingTime(
            listOf(
                CheckpointLabelAndTime("1", Time(5)),
                CheckpointLabelAndTime("2", Time(7)),
                CheckpointLabelAndTime("3", Time(9)),
                CheckpointLabelAndTime("1", Time(11)),
            ), Time(1)
        )
        assertEquals(Time(10), passTime)
    }

    @Test
    fun `result on at least k checkpoints protocols when k are visited are proper`() {
        val route = AtLeastKCheckpointsRoute("", setOf("1", "2", "3", "4"), 2)
        val passTime = route.calculateResultingTime(
            listOf(
                CheckpointLabelAndTime("1", Time(5)),
                CheckpointLabelAndTime("3", Time(7)),
            ), Time(1)
        )
        assertEquals(Time(6), passTime)
    }

    // the checkpoints after the k_th should be ignored
    // (the participant might run them for fun)
    @Test
    fun `result on at least k checkpoints protocols when more than k are visited are proper`() {
        val route = AtLeastKCheckpointsRoute("", setOf("1", "2", "3", "4"), 2)
        val passTime = route.calculateResultingTime(
            listOf(
                CheckpointLabelAndTime("1", Time(5)),
                CheckpointLabelAndTime("3", Time(7)),
                CheckpointLabelAndTime("4", Time(8)),
                CheckpointLabelAndTime("5", Time(11)),
            ), Time(1)
        )
        assertEquals(Time(6), passTime)
    }
}