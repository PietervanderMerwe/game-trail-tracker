package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.HuntDao
import com.epilogs.game_trail_tracker.database.entities.Hunt
import kotlinx.coroutines.launch

class HuntViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val huntDao: HuntDao = db.huntDao()

    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()

    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertHunt(hunt: Hunt) = viewModelScope.launch {
        huntDao.insertHunt(hunt)
        insertionSuccess.postValue(true)
    }

    fun getHuntById(id: Int): LiveData<Hunt?> = liveData {
        val hunt = huntDao.getHuntById(id)
        emit(hunt)
    }

    fun getAllHunts(): LiveData<List<Hunt>> = liveData {
        val hunts = huntDao.getAllHunts()
        emit(hunts)
    }

    fun updateHunt(hunt: Hunt) = viewModelScope.launch {
        huntDao.updateHunt(hunt)
        updateSuccess.postValue(true)
    }

    fun getLatestHunt(): LiveData<Hunt?> = liveData {
        val hunt = huntDao.getLatestHunt()
        emit(hunt)
    }

    fun deleteHunt(hunt: Hunt) = viewModelScope.launch {
        huntDao.deleteHunt(hunt)
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