package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.LocationDao
import com.epilogs.game_trail_tracker.database.entities.Location
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val locationDao: LocationDao = db.locationDao()

    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()

    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertLocation(location: Location) = viewModelScope.launch {
        locationDao.insertLocation(location)
        insertionSuccess.postValue(true)
    }

    fun getLocationById(id: Int): LiveData<Location?> = liveData {
        val location = locationDao.getLocationById(id)
        emit(location)
    }

    fun getAllLocations(): LiveData<List<Location>> = liveData {
        val locations = locationDao.getAllLocations()
        emit(locations)
    }

    fun updateLocation(location: Location) = viewModelScope.launch {
        locationDao.updateLocation(location)
        updateSuccess.postValue(true)
    }

    fun deleteLocation(location: Location) = viewModelScope.launch {
        locationDao.deleteLocation(location)
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