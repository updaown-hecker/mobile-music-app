package com.updaown.musicapp.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository(private val context: Context) {

    suspend fun getAudioFiles(): List<Song> =
            withContext(Dispatchers.IO) {
                val songs = mutableListOf<Song>()
                val projection =
                        arrayOf(
                                MediaStore.Audio.Media._ID,
                                MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Media.DURATION,
                                MediaStore.Audio.Media.ALBUM_ID
                        )

                val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
                val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

                context.contentResolver.query(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                projection,
                                selection,
                                null,
                                sortOrder
                        )
                        ?.use { cursor ->
                            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                            val titleColumn =
                                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                            val artistColumn =
                                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                            val albumColumn =
                                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                            val durationColumn =
                                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                            val albumIdColumn =
                                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                            // BUCKET_DISPLAY_NAME might not exist on very old Android version, but
                            // standard on modern
                            val folderColumn =
                                    cursor.getColumnIndex(
                                            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME
                                    )

                            while (cursor.moveToNext()) {
                                val id = cursor.getLong(idColumn)
                                val title = cursor.getString(titleColumn) ?: "Unknown"
                                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                                val album = cursor.getString(albumColumn) ?: "Unknown Album"
                                val duration = cursor.getLong(durationColumn)
                                val albumId = cursor.getLong(albumIdColumn)
                                val folder =
                                        if (folderColumn != -1)
                                                cursor.getString(folderColumn) ?: "Unknown"
                                        else "Unknown"

                                val contentUri: Uri =
                                        ContentUris.withAppendedId(
                                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                id
                                        )

                                val artworkUri =
                                        Uri.parse("content://media/external/audio/albumart")
                                val albumArtUri = ContentUris.withAppendedId(artworkUri, albumId)

                                songs.add(
                                        Song(
                                                id = id,
                                                title = title,
                                                artist = artist,
                                                album = album,
                                                duration = duration,
                                                contentUri = contentUri,
                                                albumArtUri = albumArtUri,
                                                folderName = folder
                                        )
                                )
                            }
                        }
                songs
            }
}
