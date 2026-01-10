package com.updaown.musicapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY title ASC") fun getAllSongs(): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertSongs(songs: List<SongEntity>)

    @Delete suspend fun deleteSong(song: SongEntity)

    @Query("DELETE FROM songs") suspend fun clearlibrary()

    @Query(
            "UPDATE songs SET customTitle = :customTitle, customArtist = :customArtist, customAlbum = :customAlbum, customAlbumArtPath = :customAlbumArtPath WHERE id = :songId"
    )
    suspend fun updateSongMetadata(
            songId: Long,
            customTitle: String?,
            customArtist: String?,
            customAlbum: String?,
            customAlbumArtPath: String?
    )

    @Query(
            "UPDATE songs SET title = :title, artist = :artist, album = :album, albumArtUri = :albumArtUri, duration = :duration WHERE id = :songId"
    )
    suspend fun updateCoreMetadata(
            songId: Long,
            title: String,
            artist: String,
            album: String,
            albumArtUri: String,
            duration: Long
    )
}
