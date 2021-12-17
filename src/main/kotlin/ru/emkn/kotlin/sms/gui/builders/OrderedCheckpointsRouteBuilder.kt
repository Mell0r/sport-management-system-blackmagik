package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import ru.emkn.kotlin.sms.CheckpointLabelT

data class OrderedCheckpointsRouteBuilder(
    var name: String,
    val orderedCheckpoints: SnapshotStateList<MutableState<CheckpointLabelT>>
)