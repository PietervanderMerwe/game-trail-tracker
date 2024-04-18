package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.UserSettingsDao
import com.epilogs.game_trail_tracker.database.entities.UserSettings
import kotlinx.coroutines.launch

class UserSettingsViewModel (application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val userSettingsDao: UserSettingsDao = db.userSettingsDao()
    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()
    private val _insertionId = MutableLiveData<Long?>()
    val insertionId: LiveData<Long?> = _insertionId
    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertUserSettings(userSettings: UserSettings) = viewModelScope.launch {
        val insertedId = userSettingsDao.insertUserSettings(userSettings)
        _insertionId.postValue(insertedId)
        insertionSuccess.postValue(true)
    }

    fun getUserSettingsById(id: Int): LiveData<UserSettings?> = liveData {
        val userSettings = userSettingsDao.getUserSettingsById(id)
        emit(userSettings)
    }

    fun getAllUserSettings(): LiveData<List<UserSettings>> = liveData {
        val userSettings = userSettingsDao.getAllUserSettings()
        emit(userSettings)
    }

    fun resetInsertionId() {
        _insertionId.value = null
    }

    fun updateUserSettings(userSettings: UserSettings) = viewModelScope.launch {
        userSettingsDao.updateUserSettings(userSettings)
        updateSuccess.postValue(true)
    }

    fun deleteUserSettings(userSettings: UserSettings) = viewModelScope.launch {
        userSettingsDao.deleteUserSettings(userSettings)
        deleteSuccess.postValue(true)
    }

    fun resetInsertionSuccess() {
        insertionSuccess.value = null
    }

    fun resetUpdateSuccess() {
        updateSuccess.value = null
    }

    fun resetDeleteSuccess() {
        deleteSuccess.value = null
    }
}