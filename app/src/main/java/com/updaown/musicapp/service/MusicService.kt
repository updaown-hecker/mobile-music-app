package com.updaown.musicapp.service

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.updaown.musicapp.ui.MainActivity

class MusicService : MediaLibraryService() {

    private var mediaSession: MediaLibrarySession? = null
    private lateinit var player: ExoPlayer

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()
            
        mediaSession = MediaLibrarySession.Builder(this, player, LibrarySessionCallback())
            .setSessionActivity(getSingleTopActivity())
            .build()
    }
    
    // Define a callback (basic implementation for now)
    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {
        // Implement browsing logic if needed later, for now default is fine for playback
    }

    private fun getSingleTopActivity(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
