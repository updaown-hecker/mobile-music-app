package com.updaown.musicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(primaryKeys = ["playlistId", "songId"], tableName = "playlist_song_cross_ref")
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long
)
