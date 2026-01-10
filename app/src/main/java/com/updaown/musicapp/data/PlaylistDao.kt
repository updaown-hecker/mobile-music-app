package com.updaown.musicapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistSongCrossRef(crossRef: PlaylistSongCrossRef)

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Transaction
    @Query("SELECT songs.* FROM songs INNER JOIN playlist_song_cross_ref ON songs.id = playlist_song_cross_ref.songId WHERE playlist_song_cross_ref.playlistId = :playlistId")
    fun getSongsForPlaylist(playlistId: Long): Flow<List<SongEntity>>
}
