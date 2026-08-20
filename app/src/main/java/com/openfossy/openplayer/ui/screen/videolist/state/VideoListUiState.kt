package com.openfossy.openplayer.ui.screens.videolist.state

import com.openfossy.openplayer.domain.model.StorageVolumeInfo
import com.openfossy.openplayer.domain.model.Video
import com.openfossy.openplayer.domain.model.VideoFolder
import com.openfossy.openplayer.domain.model.ViewSettings

sealed interface ExplorerItem {
    data class FolderItem(val folder: VideoFolder) : ExplorerItem
    data class VideoItem(val video: Video) : ExplorerItem
}

data class PathSegment(val name: String, val absolutePath: String)

data class VideoListUiState(
    val videosByFolder: Map<VideoFolder, List<Video>> = emptyMap(),
    val selectedVideos: Set<Video> = emptySet(),
    val selectedFolders: Set<VideoFolder> = emptySet(),
    val selectedFolder: VideoFolder? = null,
    val viewSettings: ViewSettings = ViewSettings(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val explorerItems: List<ExplorerItem> = emptyList(),
    val currentExplorerPath: String = android.os.Environment.getExternalStorageDirectory().absolutePath,
    val searchText: String = "",
    val searchActive: Boolean = false,
    val searchSuggestions: List<String> = emptyList(),
    val availableStorages: List<StorageVolumeInfo> = emptyList(),
    val selectedStorage: StorageVolumeInfo? = null
)

