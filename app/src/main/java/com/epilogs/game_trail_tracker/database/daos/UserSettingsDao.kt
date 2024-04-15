package com.epilogs.game_trail_tracker.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epilogs.game_trail_tracker.database.entities.UserSettings

@Dao
interface UserSettingsDao {
    @Insert
    suspend fun insertUserSettings(userSettings: UserSettings)
    @Update
    suspend fun updateUserSettings(userSettings: UserSettings)
    @Delete
    suspend fun deleteUserSettings(userSettings: UserSettings)
    @Query("SELECT * FROM UserSettings")
    suspend fun getAllUserSettings(): List<UserSettings>

    @Query("SELECT * FROM UserSettings WHERE id = :id")
    suspend fun getUserSettingsById(id: Int): UserSettings
}