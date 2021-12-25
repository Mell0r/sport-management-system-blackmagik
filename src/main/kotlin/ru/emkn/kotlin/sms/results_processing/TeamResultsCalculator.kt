package ru.emkn.kotlin.sms.results_processing

/**
 * Given a list of [GroupResultProtocol]s,
 * calculates team results
 * and complies it to [TeamResultsProtocol].
 */
interface TeamResultsCalculator {
    fun calculate(groupResultProtocols: List<GroupResultProtocol>): TeamResultsProtocol
}