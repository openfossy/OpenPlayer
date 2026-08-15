package com.devson.openplayer.ui.common.components.fastscroll

import com.devson.openplayer.domain.model.SortField
import com.devson.openplayer.domain.model.Video
import com.devson.openplayer.domain.model.VideoFolder
import com.devson.openplayer.ui.screens.videolist.state.ExplorerItem
import com.devson.openplayer.util.formatDuration
import com.devson.openplayer.util.formatResolutionCompact
import com.devson.openplayer.util.formatSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FastScrollSectionHelper {

    private val monthYearFormat by lazy {
        SimpleDateFormat("MMM yyyy", Locale.getDefault())
    }

    fun getVideoSectionLabel(video: Video?, sortField: SortField): String {
        if (video == null) return ""
        return when (sortField) {
            SortField.TITLE -> {
                val firstChar = video.title.trim().firstOrNull()?.uppercaseChar()
                if (firstChar != null && firstChar.isLetter()) {
                    firstChar.toString()
                } else if (firstChar != null && firstChar.isDigit()) {
                    "#"
                } else {
                    "#"
                }
            }
            SortField.DATE -> {
                formatTimestamp(video.dateAdded)
            }
            SortField.PLAYED_TIME, SortField.STATUS -> {
                val played = video.playedTime ?: 0L
                if (played > 0L) {
                    if (video.duration > 0L) {
                        val percent = (played * 100 / video.duration).coerceIn(0, 100)
                        "$percent%"
                    } else {
                        "Played"
                    }
                } else {
                    "Unplayed"
                }
            }
            SortField.LENGTH -> {
                formatDuration(video.duration)
            }
            SortField.SIZE -> {
                formatSize(video.size)
            }
            SortField.RESOLUTION -> {
                formatResolutionCompact(video.resolution) ?: "SD"
            }
            SortField.PATH -> {
                video.folderName.take(12).ifBlank { "/" }
            }
            SortField.FRAME_RATE -> {
                val fps = video.frameRate?.toInt() ?: 0
                if (fps > 0) "${fps}fps" else "30fps"
            }
            SortField.TYPE -> {
                video.title.substringAfterLast(".", "").uppercase().takeIf { it.isNotBlank() } ?: "OTHER"
            }
        }
    }

    fun getFolderSectionLabel(
        folder: VideoFolder?,
        videos: List<Video>?,
        sortField: SortField
    ): String {
        if (folder == null) return ""
        return when (sortField) {
            SortField.TITLE, SortField.PATH, SortField.RESOLUTION, SortField.FRAME_RATE, SortField.TYPE -> {
                val firstChar = folder.name.trim().firstOrNull()?.uppercaseChar()
                if (firstChar != null && firstChar.isLetter()) {
                    firstChar.toString()
                } else if (firstChar != null && firstChar.isDigit()) {
                    "#"
                } else {
                    "#"
                }
            }
            SortField.DATE -> {
                val maxDate = videos?.maxOfOrNull { it.dateAdded } ?: 0L
                formatTimestamp(maxDate)
            }
            SortField.PLAYED_TIME, SortField.STATUS -> {
                val maxPlayed = videos?.maxOfOrNull { it.playedTime ?: 0L } ?: 0L
                if (maxPlayed > 0L) "Played" else "Unplayed"
            }
            SortField.LENGTH -> {
                val totalDuration = videos?.sumOf { it.duration } ?: 0L
                formatDuration(totalDuration)
            }
            SortField.SIZE -> {
                val totalSize = videos?.sumOf { it.size } ?: 0L
                formatSize(totalSize)
            }
        }
    }

    fun getExplorerSectionLabel(
        item: ExplorerItem?,
        folderVideos: List<Video>?,
        sortField: SortField
    ): String {
        return when (item) {
            is ExplorerItem.FolderItem -> getFolderSectionLabel(item.folder, folderVideos, sortField)
            is ExplorerItem.VideoItem -> getVideoSectionLabel(item.video, sortField)
            null -> ""
        }
    }

    private fun formatTimestamp(timeMs: Long): String {
        if (timeMs <= 0L) return "Unknown"
        return try {
            val ms = if (timeMs.toString().length < 13) timeMs * 1000L else timeMs
            monthYearFormat.format(Date(ms))
        } catch (e: Exception) {
            ""
        }
    }
}
