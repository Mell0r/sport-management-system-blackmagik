package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import ru.emkn.kotlin.sms.AtLeastKCheckpointsRoute
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.OrderedCheckpointsRoute
import ru.emkn.kotlin.sms.Route

enum class RouteType {
    ORDERED,
    AT_LEAST_K
}

class RouteBuilder(
    var type: MutableState<RouteType>,
    var name: MutableState<String>,
    var k: MutableState<String>,
    val checkpoints: SnapshotStateList<MutableState<CheckpointLabelT>>
) {
    fun toRoute(): Route {
        if (type.value == RouteType.ORDERED)
            return OrderedCheckpointsRoute(name.value, checkpoints.map { it.value}.toList())
        return AtLeastKCheckpointsRoute(name.value, checkpoints.map { it.value }.toSet(), k.value.toInt())
    }
}