package ru.emkn.kotlin.sms.csv

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import ru.emkn.kotlin.sms.*

/*
The line must start with a route type id surrounded with dollar signs.
The $0$ is optional for backwards compatibility.
Example (ChP stands for checkpoint here):
$0$orderedRouteName,firstChP, secondChP,thirdChP
$1$atLeastKRouteName,k,firstChP,secondChP,thirdChP
 */
@ExperimentalStdlibApi
fun readRouteFromLine(line: String): ResultOrMessage<Route> {
    if (!line.startsWith("\$")) return readOrderedRouteCheckpoint(line)
    val match = """\$(\d+)\$""".toRegex().matchAt(line, 0)
        ?: return errAndLog("Bad format: there is no second dollar sign in line.")
    val prefixLength =
        match.range.last - match.range.first + 1 // plus one as both ends should be included
    val clearLine = line.drop(prefixLength)
    val routeTypeId = match.groups[1]?.value?.toInt()
        ?: throw InternalError("The regex in readRouteFromLine is broken.")
    return when (routeTypeId) {
        0 -> readOrderedRouteCheckpoint(clearLine)
        1 -> readAtLeastKCheckpointsRoute(clearLine)
        else -> return Err("Unsupported route id: $routeTypeId")
    }
}

private fun readAtLeastKCheckpointsRoute(line: String): ResultOrMessage<AtLeastKCheckpointsRoute> {
    val tokens = line.split(',').filter { it.isNotEmpty() }
    if (tokens.isEmpty()) return Err("Empty line in 'Route_description.")
    val name = tokens[0]
    val k = tokens[1].toIntOrNull()
        ?: return Err("Bad k (not a number): ${tokens[1]}")
    val droppedNameAndK = tokens.drop(2)
    val checkpoints = droppedNameAndK.toMutableSet()
    return Ok(AtLeastKCheckpointsRoute(name, checkpoints, k))
}

private fun readOrderedRouteCheckpoint(line: String): ResultOrMessage<OrderedCheckpointsRoute> {
    val tokens = line.split(',').filter { it.isNotEmpty() }
    if (tokens.isEmpty()) return Err("Empty line in 'Route_description.")
    return Ok(
        OrderedCheckpointsRoute(
            tokens[0],
            tokens.drop(1).toMutableList()
        )
    )
}