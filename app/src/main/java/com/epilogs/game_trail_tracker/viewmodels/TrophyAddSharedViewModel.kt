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
import com.epilogs.game_trail_tracker.database.daos.TrophyDao
import com.epilogs.game_trail_tracker.database.daos.WeaponDao
import com.epilogs.game_trail_tracker.database.entities.Trophy
import com.epilogs.game_trail_tracker.database.entities.TrophyMeasurement
import kotlinx.coroutines.launch

class TrophyAddSharedViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val trophyDao: TrophyDao = db.animalDao()
    private val trophyEntity = MutableLiveData<Trophy?>()
    private val measurementCategoryId = MutableLiveData<Int?>()
    private val trophyMeasurementsEntity = MutableLiveData<List<TrophyMeasurement?>>()
    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()
    private var imageUris = mutableSetOf<String>()

    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertTrophy() = viewModelScope.launch {
        trophyEntity.value?.let {
            trophyDao.insertTrophy(it)
            insertionSuccess.postValue(true)
        }
    }

    fun setBasicTrophyDetails(trophy: Trophy, categoryId: Int?) {
        trophyEntity.value = trophy
        measurementCategoryId.value = categoryId
    }

    fun setTrophyWeight(weight: Double, weightUnit: String) {
        trophyEntity.value?.weight = weight
        trophyEntity.value?.weightUnit = weightUnit
    }

    fun setTrophyMeasurements(trophyMeasurements: List<TrophyMeasurement>) {
        trophyMeasurementsEntity.value = trophyMeasurements
    }

    fun getTrophyDetails(): LiveData<Trophy> = liveData {
        emit(trophyEntity.value!!)
    }
    fun getMeasurementCategoryId(): LiveData<Int> = liveData {
        emit(measurementCategoryId.value!!)
    }

    fun setImages(images: MutableSet<String>) {
        imageUris = images
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