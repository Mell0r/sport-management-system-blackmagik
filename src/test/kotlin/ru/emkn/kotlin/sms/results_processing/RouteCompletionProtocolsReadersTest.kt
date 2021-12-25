package ru.emkn.kotlin.sms.results_processing

import com.github.michaelbull.result.unwrap
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RouteCompletionProtocolsReadersTest {

    @Test
    fun testCheckpointTimestampsReading() {
        val protocol = "1km,\n1,12:00:00\n2,12:00:02".split("\n")
        assertEquals(
            CheckpointTimestampsProtocol(
                "1km", listOf(
                    IdAndTime(1, Time(12, 0, 0)),
                    IdAndTime(2, Time(12, 0, 2)),
                )
            ),
            CheckpointTimestampsProtocol.readFromCsvContent(protocol).unwrap()
        )
    }

    @Test
    fun testParticipantTimestampsReading() {
        val protocol = "243,\n1km,12:00:00\n2km,12:00:02".split("\n")
        assertEquals(
            ParticipantTimestampsProtocol(
                243, listOf(
                    CheckpointAndTime("1km", Time(12, 0, 0)),
                    CheckpointAndTime("2km", Time(12, 0, 2)),
                )
            ),
            ParticipantTimestampsProtocol.readFromCsvContent(protocol).unwrap()
        )
    }
}