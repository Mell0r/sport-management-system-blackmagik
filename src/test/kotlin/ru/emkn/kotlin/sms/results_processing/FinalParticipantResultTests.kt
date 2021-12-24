package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.time.Time
import kotlin.test.Test
import kotlin.test.assertTrue

internal class FinalParticipantResultTests {
    /* Test data */
    private val finished10 = FinalParticipantResult.Finished(Time(10)) as FinalParticipantResult
    private val finished25a = FinalParticipantResult.Finished(Time(25)) as FinalParticipantResult
    private val finished25b = FinalParticipantResult.Finished(Time(25)) as FinalParticipantResult
    private val finished40 = FinalParticipantResult.Finished(Time(40)) as FinalParticipantResult
    private val disqualified1 = FinalParticipantResult.Disqualified() as FinalParticipantResult
    private val disqualified2 = FinalParticipantResult.Disqualified() as FinalParticipantResult

    @Test
    fun `FinalParticipantsResult equals() test`() {
        assertTrue { finished10 == finished10 }
        assertTrue { finished10 != finished25a }
        assertTrue { finished10 != finished25b }
        assertTrue { finished10 != finished40 }
        assertTrue { finished10 != disqualified1 }
        assertTrue { finished10 != disqualified2 }

        assertTrue { finished25a == finished25a }
        assertTrue { finished25a == finished25b }
        assertTrue { finished25a != finished40 }
        assertTrue { finished25a != disqualified1 }
        assertTrue { finished25a != disqualified2 }

        assertTrue { finished25b == finished25a }
        assertTrue { finished25b != finished10 }
        assertTrue { finished25b != finished40 }
        assertTrue { finished25b != disqualified1 }
        assertTrue { finished25b != disqualified2 }

        assertTrue { finished40 != finished10 }
        assertTrue { finished40 != finished25a }
        assertTrue { finished40 == finished40 }
        assertTrue { finished40 != disqualified1 }

        assertTrue { disqualified1 != finished10 }
        assertTrue { disqualified1 != finished25a }
        assertTrue { disqualified1 == disqualified1 }
        assertTrue { disqualified1 == disqualified2 }

        assertTrue { disqualified2 == disqualified1 }
        assertTrue { disqualified2 != finished25b }
        assertTrue { disqualified2 != finished40 }
    }

    @Test
    fun `FinalParticipantsResult compareTo() test`() {
        assertTrue { finished10.compareTo(finished10) == 0 }
        assertTrue { finished10 < finished25a }
        assertTrue { finished10 < finished25b }
        assertTrue { finished10 < finished40 }
        assertTrue { finished10 < disqualified1 }
        assertTrue { finished10 < disqualified2 }

        assertTrue { finished25a.compareTo(finished25b) == 0 }
        assertTrue { finished25a > finished10 }
        assertTrue { finished25a < finished40 }
        assertTrue { finished25a < disqualified1 }

        assertTrue { finished25b.compareTo(finished25a) == 0 }
        assertTrue { finished25b < disqualified2 }

        assertTrue { finished40 > finished10 }
        assertTrue { finished40 > finished25a }
        assertTrue { finished40.compareTo(finished40) == 0 }
        assertTrue { finished40 < disqualified2 }

        assertTrue { disqualified1 > finished10 }
        assertTrue { disqualified1 > finished25a }
        assertTrue { disqualified1 > finished25b }
        assertTrue { disqualified1 > finished40 }
        assertTrue { disqualified1.compareTo(disqualified1) == 0 }
        assertTrue { disqualified1.compareTo(disqualified2) == 0 }

        assertTrue { disqualified2 > finished10 }
        assertTrue { disqualified2 > finished25a }
        assertTrue { disqualified2 > finished25b }
        assertTrue { disqualified2 > finished40 }
        assertTrue { disqualified2.compareTo(disqualified1) == 0 }
        assertTrue { disqualified2.compareTo(disqualified2) == 0 }
    }

}