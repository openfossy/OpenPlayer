package com.openfossy.openplayer.ui.screen.videolist.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfossy.openplayer.domain.model.Video
import com.openfossy.openplayer.domain.model.ViewSettings
import com.openfossy.openplayer.util.formatDate
import com.openfossy.openplayer.util.formatDuration
import com.openfossy.openplayer.util.formatRelativeTime
import com.openfossy.openplayer.util.formatResolutionCompact
import com.openfossy.openplayer.util.formatSize

@Composable
fun VideoMetadataRow(
    video: Video,
    settings: ViewSettings,
    isGrid: Boolean = false,
    lastPositionMs: Long = 0L,
    isRecentlyPlayed: Boolean = false
) {
    VideoMetadataChips(video, settings, lastPositionMs, isGrid, isRecentlyPlayed)
}
 
@Composable
fun VideoMetadataChips(
    video: Video,
    settings: ViewSettings,
    lastPositionMs: Long = 0L,
    isGrid: Boolean = false,
    isRecentlyPlayed: Boolean = false
) {
    data class MetaToken(val text: String, val isPrimary: Boolean = false)
 
    val tokens = buildList {
        if (settings.showLength && !settings.displayLengthOverThumbnail)
            add(MetaToken(formatDuration(video.duration), isPrimary = true))
        if (settings.showPlayedTime && video.lastPlayedAt != null && video.lastPlayedAt > 0)
            add(MetaToken(formatRelativeTime(LocalContext.current, video.lastPlayedAt), isPrimary = isRecentlyPlayed))
        if (settings.showResolution && !video.resolution.isNullOrEmpty())
            add(MetaToken(formatResolutionCompact(video.resolution) ?: video.resolution))
        if (settings.showFrameRate && video.frameRate != null && video.frameRate > 0f)
            add(MetaToken("${video.frameRate.toInt()} fps"))
        if (settings.showFileExtension)
            add(MetaToken(video.title.substringAfterLast('.', video.uri.substringAfterLast('.', "")).uppercase()))
        if (settings.showSize)
            add(MetaToken(formatSize(video.size)))
        if (settings.showDate && video.dateAdded > 0)
            add(MetaToken(formatDate(video.dateAdded)))
    }.filter { it.text.isNotBlank() }
 
    if (tokens.isEmpty()) return
 
    Row(
        modifier          = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tokens.take(if (isGrid) 2 else 4).forEach { token ->
            MetadataChip(
                text = token.text,
                isPrimary = token.isPrimary,
                isGrid = isGrid,
                isRecentlyPlayed = isRecentlyPlayed
            )
        }
    }
}
 
@Composable
fun MetadataChip(
    text: String,
    isPrimary: Boolean,
    isGrid: Boolean = false,
    isRecentlyPlayed: Boolean = false
) {
    val bgColor = when {
        isRecentlyPlayed && isPrimary -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        isPrimary -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    }
 
    val textColor = when {
        isRecentlyPlayed && isPrimary -> MaterialTheme.colorScheme.primary
        isPrimary -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
 
    val fontSize = if (isGrid) 9.5.sp else 10.5.sp
 
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(5.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text       = text,
            style      = MaterialTheme.typography.labelSmall,
            fontSize   = fontSize,
            fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Normal,
            color      = textColor,
            maxLines   = 1
        )
    }
}
