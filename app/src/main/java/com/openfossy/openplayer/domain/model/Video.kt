package com.openfossy.openplayer.domain.model

import androidx.compose.runtime.Immutable

enum class LayoutMode {
    LIST, GRID
}

enum class ViewMode {
    ALL_FOLDERS, FILES, FOLDERS
}

enum class SortField {
    TITLE, DATE, PLAYED_TIME, LENGTH, SIZE
}

enum class SortDirection {
    ASCENDING, DESCENDING
}

@Immutable
data class WatchHistory(
    val uri: String,
    val lastPositionMs: Long,
    val lastPlayedAt: Long = 0L,
    val videoTitle: String? = null,
    val durationMs: Long = 0L,
    val fileSize: Long = 0L
)

@Immutable
data class Video(
    val uri: String,
    val title: String,
    val duration: Long,
    val folderName: String,
    val path: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val dateAdded: Long = 0L,
    val dateModified: Long = 0L,
    val playedTime: Long? = null,
    val lastPlayedAt: Long? = null,
    val resolution: String? = null,
    val frameRate: Float? = null,
    val dateExpires: Long? = null,
    val thumbnailUri: String? = null
)

fun List<Video>.applySort(field: SortField, direction: SortDirection): List<Video> {
    val sorted = when (field) {
        SortField.TITLE -> sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
        SortField.DATE -> sortedBy { it.dateAdded }
        SortField.PLAYED_TIME -> sortedBy { it.lastPlayedAt ?: it.playedTime ?: 0L }
        SortField.LENGTH -> sortedBy { it.duration }
        SortField.SIZE -> sortedBy { it.size }
    }
    return if (direction == SortDirection.DESCENDING) sorted.reversed() else sorted
}
