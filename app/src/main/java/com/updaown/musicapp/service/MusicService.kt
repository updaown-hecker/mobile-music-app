package com.updaown.musicapp.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import android.media.audiofx.Equalizer
import android.media.audiofx.DynamicsProcessing
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.updaown.musicapp.ui.MainActivity

class MusicService : MediaLibraryService() {

    private var mediaSession: MediaLibrarySession? = null
    private lateinit var player: ExoPlayer
    private var equalizer: Equalizer? = null
    private var dynamicsProcessing: DynamicsProcessing? = null

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

        setupEqualizer()
        setupDynamicsProcessing()
    }

    private fun setupEqualizer() {
        try {
            equalizer = Equalizer(0, player.audioSessionId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupDynamicsProcessing() {
        try {
            val config = DynamicsProcessing.Config.Builder(
                DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                1, // channels
                true, // preEq
                0, // preEq bands
                true, // mbc
                0, // mbc bands
                true, // postEq
                0, // postEq bands
                true // limiter
            ).build()
            dynamicsProcessing = DynamicsProcessing(0, player.audioSessionId, config)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Define a callback with custom command support
    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {
        
        // 1. Grant permission for the custom command when a controller connects
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val availableCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .add(SessionCommand("SET_SKIP_SILENCE", Bundle.EMPTY))
                .add(SessionCommand("SET_EQUALIZER", Bundle.EMPTY))
                .add(SessionCommand("SET_CROSSFADE", Bundle.EMPTY))
                .add(SessionCommand("SET_VOLUME_NORMALIZATION", Bundle.EMPTY))
                .build()
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(availableCommands)
                .build()
        }

        // 2. Handle the command and update ExoPlayer
        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            when (customCommand.customAction) {
                "SET_SKIP_SILENCE" -> {
                    val enabled = args.getBoolean("enabled", false)
                    player.skipSilenceEnabled = enabled
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                "SET_EQUALIZER" -> {
                    val enabled = args.getBoolean("enabled", false)
                    val bass = args.getInt("bass", 0)
                    val midrange = args.getInt("midrange", 0)
                    val treble = args.getInt("treble", 0)

                    applyEqualizer(enabled, bass, midrange, treble)
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                "SET_CROSSFADE" -> {
                    val duration = args.getInt("duration", 0)
                    // Crossfade implementation would go here
                    // For now we just acknowledge the command
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                "SET_VOLUME_NORMALIZATION" -> {
                    val enabled = args.getBoolean("enabled", false)
                    applyVolumeNormalization(enabled)
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_ERROR_UNKNOWN))
        }
    }

    private fun applyEqualizer(enabled: Boolean, bass: Int, midrange: Int, treble: Int) {
        try {
            if (equalizer == null) setupEqualizer()

            equalizer?.let { eq ->
                eq.enabled = enabled
                if (enabled) {
                    // Map -10..10 to milliBel (usually -1500 to 1500)
                    val numberOfBands = eq.numberOfBands
                    if (numberOfBands >= 3) {
                        eq.setBandLevel(0.toShort(), (bass * 150).toShort())
                        eq.setBandLevel((numberOfBands / 2).toShort(), (midrange * 150).toShort())
                        eq.setBandLevel((numberOfBands - 1).toShort(), (treble * 150).toShort())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyVolumeNormalization(enabled: Boolean) {
        try {
            if (dynamicsProcessing == null) setupDynamicsProcessing()
            dynamicsProcessing?.enabled = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
            equalizer?.release()
            dynamicsProcessing?.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
