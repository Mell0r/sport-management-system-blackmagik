package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.CheckpointAndTime
import ru.emkn.kotlin.sms.results_processing.FinalParticipantResult
import ru.emkn.kotlin.sms.results_processing.LiveParticipantResult
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.Test
import kotlin.test.assertEquals

class RouteResultCalculationTest {
    @Test
    fun `result on properly passed ordered checkpoints protocols are proper`() {
        val route = OrderedCheckpointsRoute("", mutableListOf("1", "2", "3"))
        val passTime = route.calculateFinalResult(
            listOf(
                CheckpointAndTime("1", Time(5)),
                CheckpointAndTime("2", Time(7)),
                CheckpointAndTime("3", Time(9)),
            ), Time(1)
        )
        assertEquals(FinalParticipantResult.Finished(Time(8)), passTime)
    }

    @Test
    fun `result on ordered checkpoints protocols with wrong order are proper`() {
        val route = OrderedCheckpointsRoute("", mutableListOf("1", "2", "3"))
        val passTime = route.calculateFinalResult(
            listOf(
                CheckpointAndTime("1", Time(5)),
                CheckpointAndTime("2", Time(11)),
                CheckpointAndTime("3", Time(9)),
            ), Time(1)
        )
        assertEquals(FinalParticipantResult.Disqualified(), passTime)
    }

    @Test
    fun `result on ordered checkpoints protocols with false start are proper`() {
        val route = OrderedCheckpointsRoute("", mutableListOf("1", "2", "3"))
        val passTime = route.calculateFinalResult(
            listOf(
                CheckpointAndTime("1", Time(5)),
                CheckpointAndTime("2", Time(11)),
                CheckpointAndTime("3", Time(9)),
            ), Time(100)
        )
        assertEquals(FinalParticipantResult.Disqualified(), passTime)
    }

    @Test
    fun `result on ordered checkpoints protocols with repeated checkpoints are proper`() {
        val route = OrderedCheckpointsRoute("", mutableListOf("1", "2", "3", "1"))
        val passTime = route.calculateFinalResult(
            listOf(
                CheckpointAndTime("1", Time(5)),
                CheckpointAndTime("2", Time(7)),
                CheckpointAndTime("3", Time(9)),
                CheckpointAndTime("1", Time(11)),
            ), Time(1)
        )
        assertEquals(FinalParticipantResult.Finished(Time(10)), passTime)
    }

    @Test
    fun `result on at least k checkpoints protocols when k are visited are proper`() {
        val route = AtLeastKCheckpointsRoute("", mutableSetOf("1", "2", "3", "4"), 2)
        val passTime = route.calculateFinalResult(
            listOf(
                CheckpointAndTime("1", Time(5)),
                CheckpointAndTime("3", Time(7)),
            ), Time(1)
        )
        assertEquals(FinalParticipantResult.Finished(Time(6)), passTime)
    }

    // the checkpoints after the k_th should be ignored
    // (the participant might run them for fun)
    @Test
    fun `result on at least k checkpoints protocols when more than k are visited are proper`() {
        val route =
            AtLeastKCheckpointsRoute("", mutableSetOf("1", "2", "3", "4"), 2)
        val passTime = route.calculateFinalResult(
            listOf(
                CheckpointAndTime("1", Time(5)),
                CheckpointAndTime("3", Time(7)),
                CheckpointAndTime("4", Time(8)),
                CheckpointAndTime("5", Time(11)),
            ), Time(1)
        )
        assertEquals(FinalParticipantResult.Finished(Time(6)), passTime)
    }

    @Test
    fun `result on OrderedCheckpoints route with zero checkpoints visited is correct`() {
        val route =
            OrderedCheckpointsRoute("", mutableListOf("1", "2", "3", "4"))
        val passTime = route.calculateLiveResult(listOf(), Time(1))
        assertEquals(LiveParticipantResult.InProcess(0, Time(0)), passTime)
    }

    @Test
    fun `result on AtLeastKCheckpoints route with zero checkpoints visited is correct`() {
        val route =
            AtLeastKCheckpointsRoute("", mutableSetOf("1", "2", "3", "4"), 2)
        val passTime = route.calculateLiveResult(listOf(), Time(1))
        assertEquals(LiveParticipantResult.InProcess(0, Time(0)), passTime)
    }
}