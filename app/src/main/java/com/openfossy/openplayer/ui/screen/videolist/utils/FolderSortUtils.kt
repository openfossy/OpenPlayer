package com.openfossy.openplayer.ui.screens.videolist.utils

import com.openfossy.openplayer.domain.model.SortDirection
import com.openfossy.openplayer.domain.model.SortField
import com.openfossy.openplayer.domain.model.Video
import com.openfossy.openplayer.domain.model.VideoFolder

fun List<VideoFolder>.applyFolderSort(
    folderMap: Map<VideoFolder, List<Video>>,
    field: SortField,
    direction: SortDirection
): List<VideoFolder> {
    val sorted = when (field) {
        SortField.TITLE -> sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
        SortField.DATE -> sortedBy { folder -> folderMap[folder]?.maxOfOrNull { it.dateAdded } ?: 0L }
        SortField.PLAYED_TIME -> sortedBy { folder -> folderMap[folder]?.maxOfOrNull { it.lastPlayedAt ?: it.playedTime ?: 0L } ?: 0L }
        SortField.LENGTH -> sortedBy { folder -> folderMap[folder]?.sumOf { it.duration } ?: 0L }
        SortField.SIZE -> sortedBy { folder -> folderMap[folder]?.sumOf { it.size } ?: 0L }
    }
    return if (direction == SortDirection.DESCENDING) sorted.reversed() else sorted
}
