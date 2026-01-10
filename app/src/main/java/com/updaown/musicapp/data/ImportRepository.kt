package com.updaown.musicapp.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImportRepository(private val context: Context, private val database: AppDatabase) {

    // Reads from Device MediaStore (Raw Data)
    suspend fun getDeviceSongs(): List<Song> =
            withContext(Dispatchers.IO) {
                // Reuse existing MusicRepository logic but here it's specifically for "Discovery"
                MusicRepository(context).getAudioFiles()
            }

    // Writes to App Database (Imported Library)
    suspend fun importSongs(songs: List<Song>) =
            withContext(Dispatchers.IO) {
                val entities =
                        songs.map { song ->
                            SongEntity(
                                    id = song.id,
                                    title = song.title,
                                    artist = song.artist,
                                    album = song.album,
                                    contentUri = song.contentUri.toString(),
                                    albumArtUri = song.albumArtUri.toString(),
                                    duration = song.duration,
                                    folderName = song.folderName
                            )
                        }
                database.songDao().insertSongs(entities)
            }
}
