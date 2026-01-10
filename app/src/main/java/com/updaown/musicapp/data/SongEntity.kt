package com.updaown.musicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
        @PrimaryKey val id: Long,
        val title: String,
        val artist: String,
        val album: String,
        val contentUri: String,
        val albumArtUri: String,
        val duration: Long,
        val folderName: String = "Unknown",
        val dateAdded: Long = System.currentTimeMillis(),
        // Custom metadata (user-editable)
        val customTitle: String? = null,
        val customArtist: String? = null,
        val customAlbum: String? = null,
        val customAlbumArtPath: String? = null,
        val customYear: Int? = null,
        val customGenre: String? = null
)
