package com.openfossy.openplayer.ui.screens.videolist.utils

import android.net.Uri
import com.openfossy.openplayer.domain.model.Video
import com.openfossy.openplayer.ui.screen.videolist.components.common.VideoWatchState
import com.openfossy.openplayer.ui.screen.videolist.components.common.getWatchState

/**
 * Common extension functions for Video objects and lists inside the VideoList screen.
 */

fun Video.getWatchStatus(lastPositionMs: Long, daysThreshold: Int = 7): VideoWatchState {
    return getWatchState(lastPositionMs, this.duration, this.dateAdded, daysThreshold)
}

fun List<Video>.getUris(): List<Uri> {
    return this.mapNotNull {
        runCatching { Uri.parse(it.uri) }.getOrNull()
    }
}

fun Long.formatAsDuration(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
