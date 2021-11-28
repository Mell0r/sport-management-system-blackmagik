package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import kotlin.math.max

fun generateTeamResultsProtocol(
    groupResultProtocols: List<GroupResultProtocol>,
    participantsList: ParticipantsList,
    competitionConfig: Competition
): TeamResultsProtocol {
    val idToScore = HashMap<Int, Int>()
    groupResultProtocols.forEach { groupResultProtocol ->
        moveCalculatedScoresIntoMap(groupResultProtocol, idToScore)
    }
    val teamsToScore = groupResultProtocols
        .flatMap { it.entries }
        .groupBy { it.participant.team }
        .mapValues { (_, listParticipantAndTime) ->
            val idsOfTeam = listParticipantAndTime.map { it.participant.id }
            idsOfTeam.sumOf { idToScore[it]!! }
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
    val bestResult: Int? =
        groupResultProtocol.entries.mapNotNull { it.totalTime }
            .minOfOrNull { it.asSeconds() }
    if (bestResult == null) {
        groupResultProtocol.entries.map { it.participant.id }
            .forEach { id ->
                idToScore[id] = 0
            }
    } else {
        groupResultProtocol.entries.map { it.participant.id to it.totalTime?.asSeconds() }
            .forEach { (id, totalTime) ->
                if (totalTime != null) {
                    idToScore[id] = max(
                        0,
                        100 * (2 - totalTime.toFloat() / bestResult.toFloat()).toInt()
                    )
                } else {
                    idToScore[id] = 0
                }
            }
    }
}
