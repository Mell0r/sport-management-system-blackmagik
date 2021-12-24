package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import kotlin.math.roundToInt

fun generateTeamResultsProtocol(
    groupResultProtocols: List<GroupResultProtocol>,
    participantsList: ParticipantsList
): TeamResultsProtocol {
    val idToScore = HashMap<Int, Int>()
    groupResultProtocols.forEach { groupResultProtocol ->
        moveCalculatedScoresIntoMap(groupResultProtocol, idToScore)
    }
    val teamsToScore = groupResultProtocols
        .flatMap { it.entries }
        .groupBy {
            it.participant.team
        }
        .mapValues { (_, listParticipantAndTime) ->
            val idsOfTeam = listParticipantAndTime.map { it.participant.id }
            idsOfTeam.sumOf {
                idToScore[it]
                    ?: throw InternalError("HashMap idToScore is not complete: it doesn't contain id \"$it\"!")
            }
        }
    return TeamResultsProtocol(teamsToScore.entries.map { (team, score) ->
        TeamToScore(
            team,
            score
        )
    })
}

private fun moveCalculatedScoresIntoMap(
    groupResultProtocol: GroupResultProtocol,
    idToScore: HashMap<Int, Int>
) {
    val bestResult = groupResultProtocol
        .entries
        .map { it.result }
        .filterIsInstance<FinalParticipantResult.Finished>()
        .minOfOrNull { it.totalTime.asSeconds() }
    if (bestResult == null) {
        groupResultProtocol.entries.map { it.participant.id }
            .forEach { id -> idToScore[id] = 0 }
    } else {
        groupResultProtocol.entries.map { it.participant.id to it.result }
            .forEach { (id, result) ->
                val score = when (result) {
                    is FinalParticipantResult.Disqualified -> 0
                    is FinalParticipantResult.Finished ->
                        (100 * (2 - result.totalTime.asSeconds()
                            .toFloat() / bestResult.toFloat())).roundToInt()
                }
                idToScore[id] = score
            }
    }
}
