package ru.emkn.kotlin.sms

import org.junit.Test
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class LiveParticipantResultTests {
    /* Test data */
    private val finished1a = LiveParticipantResult.Finished(Time(1))
    private val finished1b = LiveParticipantResult.Finished(Time(1))
    private val finished2 = LiveParticipantResult.Finished(Time(2))
    private val finished3 = LiveParticipantResult.Finished(Time(3))
    private val inProcess21 = LiveParticipantResult.InProcess(2, Time(1))
    private val inProcess22a = LiveParticipantResult.InProcess(2, Time(2))
    private val inProcess22b = LiveParticipantResult.InProcess(2, Time(2))
    private val inProcess11 = LiveParticipantResult.InProcess(1, Time(1))
    private val inProcess12 = LiveParticipantResult.InProcess(1, Time(2))
    private val disqualified1 = LiveParticipantResult.Disqualified()
    private val disqualified2 = LiveParticipantResult.Disqualified()

    @Test
    fun `LiveParticipantResult equality test`() {
        assertEquals<LiveParticipantResult>(finished1a, finished1a)
        assertEquals<LiveParticipantResult>(finished1a, finished1b)
        assertNotEquals<LiveParticipantResult>(finished1a, finished2)
        assertNotEquals<LiveParticipantResult>(finished1a, finished3)
        assertNotEquals<LiveParticipantResult>(finished1a, inProcess11)
        assertNotEquals<LiveParticipantResult>(finished1a, inProcess12)
        assertNotEquals<LiveParticipantResult>(finished1a, inProcess22a)
        assertNotEquals<LiveParticipantResult>(finished1a, inProcess22b)
        assertNotEquals<LiveParticipantResult>(finished1a, disqualified1)
        assertNotEquals<LiveParticipantResult>(finished1a, disqualified2)
        assertNotEquals<LiveParticipantResult>(finished2, finished3)
        assertNotEquals<LiveParticipantResult>(inProcess11, inProcess12)
        assertNotEquals<LiveParticipantResult>(inProcess11, inProcess22a)
        assertEquals<LiveParticipantResult>(inProcess22a, inProcess22b)
        assertNotEquals<LiveParticipantResult>(inProcess22a, inProcess21)
        assertNotEquals<LiveParticipantResult>(inProcess21, inProcess11)
        assertNotEquals<LiveParticipantResult>(inProcess11, disqualified1)
        assertNotEquals<LiveParticipantResult>(inProcess12, inProcess22b)
        assertNotEquals<LiveParticipantResult>(inProcess12, disqualified2)
        assertEquals<LiveParticipantResult>(disqualified1, disqualified2)
    }

    @Test
    fun `LiveParticipantResult comparison test`() {
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
}