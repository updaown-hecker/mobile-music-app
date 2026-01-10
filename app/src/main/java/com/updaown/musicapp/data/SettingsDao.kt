package com.updaown.musicapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity>
    
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): SettingsEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)
    
    @Update
    suspend fun updateSettings(settings: SettingsEntity)
    
    @Query("UPDATE settings SET shuffleEnabled = :enabled WHERE id = 1")
    suspend fun updateShuffleEnabled(enabled: Boolean)
    
    @Query("UPDATE settings SET repeatMode = :mode WHERE id = 1")
    suspend fun updateRepeatMode(mode: Int)
    
    @Query("UPDATE settings SET sleepTimerMinutes = :minutes WHERE id = 1")
    suspend fun updateSleepTimer(minutes: Int)
    
    @Query("UPDATE settings SET equalizerEnabled = :enabled WHERE id = 1")
    suspend fun updateEqualizerEnabled(enabled: Boolean)
    
    @Query("UPDATE settings SET equalizerPreset = :preset WHERE id = 1")
    suspend fun updateEqualizerPreset(preset: String)
    
    @Query("UPDATE settings SET bass = :value WHERE id = 1")
    suspend fun updateBass(value: Int)
    
    @Query("UPDATE settings SET treble = :value WHERE id = 1")
    suspend fun updateTreble(value: Int)
    
    @Query("UPDATE settings SET midrange = :value WHERE id = 1")
    suspend fun updateMidrange(value: Int)
}
