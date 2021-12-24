package ru.emkn.kotlin.sms.results_processing

import org.junit.Test
import ru.emkn.kotlin.sms.results_processing.FinalParticipantResult
import ru.emkn.kotlin.sms.results_processing.LiveParticipantResult
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class LiveParticipantResultTests {
    /* Test data */
    private val finished1a = LiveParticipantResult.Finished(Time(1)) as LiveParticipantResult
    private val finished1b = LiveParticipantResult.Finished(Time(1)) as LiveParticipantResult
    private val finished2 = LiveParticipantResult.Finished(Time(2)) as LiveParticipantResult
    private val finished3 = LiveParticipantResult.Finished(Time(3)) as LiveParticipantResult
    private val inProcess21 = LiveParticipantResult.InProcess(2, Time(1)) as LiveParticipantResult
    private val inProcess22a = LiveParticipantResult.InProcess(2, Time(2)) as LiveParticipantResult
    private val inProcess22b = LiveParticipantResult.InProcess(2, Time(2)) as LiveParticipantResult
    private val inProcess11 = LiveParticipantResult.InProcess(1, Time(1)) as LiveParticipantResult
    private val inProcess12 = LiveParticipantResult.InProcess(1, Time(2)) as LiveParticipantResult
    private val disqualified1 = LiveParticipantResult.Disqualified() as LiveParticipantResult
    private val disqualified2 = LiveParticipantResult.Disqualified() as LiveParticipantResult

    @Test
    fun `LiveParticipantResult equals() test`() {
        assertTrue { finished1a == finished1a }
        assertTrue { finished1a == finished1b }
        assertTrue { finished1a != finished2 }
        assertTrue { finished1a != finished3 }
        assertTrue { finished1a != inProcess11 }
        assertTrue { finished1a != inProcess12 }
        assertTrue { finished1a != inProcess22a }
        assertTrue { finished1a != inProcess22b }
        assertTrue { finished1a != disqualified1 }
        assertTrue { finished1a != disqualified2 }
        assertTrue { finished2 != finished3 }
        assertTrue { inProcess11 != inProcess12 }
        assertTrue { inProcess11 != inProcess22a }
        assertTrue { inProcess22a == inProcess22b }
        assertTrue { inProcess22a != inProcess21 }
        assertTrue { inProcess21 != inProcess11 }
        assertTrue { inProcess11 != disqualified1 }
        assertTrue { inProcess12 != inProcess22b }
        assertTrue { inProcess12 != disqualified2 }
        assertTrue { disqualified1 == disqualified2 }
    }

    @Test
    fun `LiveParticipantResult compareTo() test`() {
        assertTrue { finished1a.compareTo(finished1a) == 0 }
        assertTrue { finished1a.compareTo(finished1b) == 0 }
        assertTrue { finished1a < finished2 }
        assertTrue { finished1a < finished3 }
        assertTrue { finished1a < inProcess11 }
        assertTrue { finished1a < inProcess12 }
        assertTrue { finished1a < inProcess22a }
        assertTrue { finished1a < inProcess22b }
        assertTrue { finished1a < inProcess21 }
        assertTrue { finished1a < disqualified1 }
        assertTrue { finished1a < disqualified2 }

        assertTrue { finished2 > finished1a }
        assertTrue { finished2 > finished1b }
        assertTrue { finished2 < finished3 }
        assertTrue { finished2 < inProcess11 }
        assertTrue { finished2 < inProcess21 }
        assertTrue { finished2 < disqualified1 }

        assertTrue { finished3.compareTo(finished3) == 0 }
        assertTrue { finished3 > finished1a }
        assertTrue { finished3 > finished2 }
        assertTrue { finished3 < inProcess12 }
        assertTrue { finished3 < inProcess22a }

        assertTrue { inProcess21 > finished2 }
        assertTrue { inProcess21 < inProcess11 }
        assertTrue { inProcess21 < inProcess12 }
        assertTrue { inProcess21 < inProcess22a }
        assertTrue { inProcess21 < disqualified1 }

        assertTrue { inProcess22a > finished1b }
        assertTrue { inProcess22a > finished2 }
        assertTrue { inProcess22a < inProcess11 }
        assertTrue { inProcess22a < inProcess12 }
        assertTrue { inProcess22a.compareTo(inProcess22a) == 0 }
        assertTrue { inProcess22a.compareTo(inProcess22b) == 0 }
        assertTrue { inProcess22a < disqualified1 }
        assertTrue { inProcess22a < disqualified2 }

        assertTrue { inProcess22b > finished3 }
        assertTrue { inProcess22b > inProcess21 }
        assertTrue { inProcess22b.compareTo(inProcess22a) == 0 }
        assertTrue { inProcess22b < disqualified1 }
        assertTrue { inProcess22b < disqualified2 }

        assertTrue { inProcess11 > finished1a }
        assertTrue { inProcess11 > finished2 }
        assertTrue { inProcess11 < inProcess12 }
        assertTrue { inProcess11 > inProcess21 }
        assertTrue { inProcess11 > inProcess22a }
        assertTrue { inProcess11 > inProcess22b }
        assertTrue { inProcess11 < disqualified1 }
        assertTrue { inProcess11 < disqualified2 }

        assertTrue { inProcess12 > finished3 }
        assertTrue { inProcess12 > inProcess11 }
        assertTrue { inProcess12 > inProcess21 }
        assertTrue { inProcess12 > inProcess22a }
        assertTrue { inProcess12 > inProcess22b }
        assertTrue { inProcess12 < disqualified2 }

        assertTrue { disqualified1 > finished1a }
        assertTrue { disqualified1 > finished1b }
        assertTrue { disqualified1 > finished2 }
        assertTrue { disqualified1 > finished3 }
        assertTrue { disqualified1 > inProcess11 }
        assertTrue { disqualified1 > inProcess12 }
        assertTrue { disqualified1 > inProcess21 }
        assertTrue { disqualified1 > inProcess22a }
        assertTrue { disqualified1 > inProcess22b }
        assertTrue { disqualified1.compareTo(disqualified2) == 0 }

        assertTrue { disqualified2.compareTo(disqualified1) == 0 }
    }

    @Test
    fun `LiveParticipantResult toFinalParticipantResult() test`() {
        assertEquals(FinalParticipantResult.Finished(Time(1)), finished1a.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Finished(Time(1)), finished1b.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Finished(Time(2)), finished2.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Finished(Time(3)), finished3.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Disqualified(), inProcess21.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Disqualified(), inProcess22a.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Disqualified(), inProcess22b.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Disqualified(), inProcess11.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Disqualified(), inProcess12.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Disqualified(), disqualified1.toFinalParticipantResult())
        assertEquals(FinalParticipantResult.Disqualified(), disqualified2.toFinalParticipantResult())
    }
}