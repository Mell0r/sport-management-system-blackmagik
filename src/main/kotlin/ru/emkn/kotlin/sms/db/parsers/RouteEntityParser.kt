package ru.emkn.kotlin.sms.db.parsers

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.schema.RouteEntity

/**
 * Parses a [Route] from [RouteEntity].
 */
object RouteEntityParser : EntityParser<String, RouteEntity, Route> {
    override fun parse(entity: RouteEntity): ResultOrMessage<Route> {
        val name = entity.name
        val checkpoints = entity.commaSeparatedCheckpoints.split(",")
        if (checkpoints.isEmpty()) {
            return Err("No checkpoints in route \"$name\".")
        }
        when (entity.type) {
            RouteType.ORDERED_CHECKPOINTS -> {
                if (entity.threshold != null) {
                    return Err("Ordered checkpoints route \"$name\" has a non-null threshold value.")
                }
                return Ok(OrderedCheckpointsRoute(name, checkpoints))
            }
            RouteType.AT_LEAST_K_CHECKPOINTS -> {
                val threshold = entity.threshold ?: return Err(
                    "At least k checkpoints route \"$name\" has null threshold value."
                )
                return Ok(AtLeastKCheckpointsRoute(name, checkpoints.toSet(), threshold))
            }
        }
    }
}