package com.updaown.musicapp.data

import android.net.Uri

data class Song(
        val id: Long,
        val title: String,
        val artist: String,
        val album: String,
        val duration: Long,
        val contentUri: Uri,
        val albumArtUri: Uri,
        val folderName: String = "Unknown",
        // Custom metadata
        val customTitle: String? = null,
        val customArtist: String? = null,
        val customAlbum: String? = null,
        val customAlbumArtPath: String? = null,
        val customYear: Int? = null,
        val customGenre: String? = null
) {
        // Display properties (use custom if available, otherwise original)
        val displayTitle: String
                get() = customTitle ?: title
        val displayArtist: String
                get() = customArtist ?: artist
        val displayAlbum: String
                get() = customAlbum ?: album
        val displayAlbumArt: Uri
                get() = customAlbumArtPath?.let { Uri.parse(it) } ?: albumArtUri
}
