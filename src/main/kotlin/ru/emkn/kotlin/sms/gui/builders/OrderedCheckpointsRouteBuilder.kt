package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.OrderedCheckpointsRoute
import ru.emkn.kotlin.sms.Route

class OrderedCheckpointsRouteBuilder(
    var name: String,
    val orderedCheckpoints: SnapshotStateList<MutableState<CheckpointLabelT>>
) {
    fun toOrderedCheckpointsRoute() = OrderedCheckpointsRoute(name, orderedCheckpoints.map { it.value }.toMutableList())
}