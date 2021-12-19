package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.OrderedCheckpointsRoute

class OrderedCheckpointsRouteBuilder(
    var name: MutableState<String>,
    val orderedCheckpoints: SnapshotStateList<MutableState<CheckpointLabelT>>
) {
    fun toOrderedCheckpointsRoute() = OrderedCheckpointsRoute(
        name.value,
        orderedCheckpoints.map { it.value }.toMutableList(),
    )
}