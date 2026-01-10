package com.updaown.musicapp.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SettingsRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val settingsDao = db.settingsDao()

    val settingsFlow: Flow<SettingsEntity> = settingsDao.getSettings()

    suspend fun getSettingsSync(): SettingsEntity {
        return withContext(Dispatchers.IO) {
            settingsDao.getSettingsSync() ?: SettingsEntity().also {
                settingsDao.insertSettings(it)
            }
        }
    }

    suspend fun updateShuffleEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            settingsDao.updateShuffleEnabled(enabled)
        }
    }

    suspend fun updateRepeatMode(mode: Int) {
        withContext(Dispatchers.IO) {
            settingsDao.updateRepeatMode(mode)
        }
    }

    suspend fun updateSleepTimer(minutes: Int) {
        withContext(Dispatchers.IO) {
            settingsDao.updateSleepTimer(minutes)
        }
    }

    suspend fun updateEqualizerEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            settingsDao.updateEqualizerEnabled(enabled)
        }
    }

    suspend fun updateEqualizerPreset(preset: String) {
        withContext(Dispatchers.IO) {
            settingsDao.updateEqualizerPreset(preset)
        }
    }

    suspend fun updateBass(value: Int) {
        withContext(Dispatchers.IO) {
            settingsDao.updateBass(value)
        }
    }

    suspend fun updateTreble(value: Int) {
        withContext(Dispatchers.IO) {
            settingsDao.updateTreble(value)
        }
    }

    suspend fun updateMidrange(value: Int) {
        withContext(Dispatchers.IO) {
            settingsDao.updateMidrange(value)
        }
    }

    suspend fun updateSettings(settings: SettingsEntity) {
        withContext(Dispatchers.IO) {
            settingsDao.updateSettings(settings)
        }
    }
}
