package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.TrophyDao
import com.epilogs.game_trail_tracker.database.daos.TrophyMeasurementDao
import com.epilogs.game_trail_tracker.database.entities.Trophy
import com.epilogs.game_trail_tracker.database.entities.TrophyMeasurement
import kotlinx.coroutines.launch

class TrophyAddSharedViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val trophyDao: TrophyDao = db.animalDao()
    private val trophyMeasurementDao: TrophyMeasurementDao = db.trophyMeasurementDao()
    private val trophyEntity = MutableLiveData<Trophy?>()
    private val measurementCategoryId = MutableLiveData<Int?>()
    private val trophyMeasurementsEntity = MutableLiveData<List<TrophyMeasurement?>>()
    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()
    private var trophyId: Int? = 0
    private var huntId: Int? = 0
    private var originFragment: String? = ""
    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertTrophy() = viewModelScope.launch {
        trophyEntity.value?.let { trophy ->
            val trophyId = trophyDao.insertTrophy(trophy)

            val newTrophyMeasurements = trophyMeasurementsEntity.value
                ?.filterNotNull()
                ?.map { measurement ->
                    TrophyMeasurement(
                        trophyId = trophyId.toInt(),
                        measurement = measurement.measurement,
                        measurementTypeId = measurement.measurementTypeId
                    )
                } ?: emptyList()

            if (newTrophyMeasurements.isNotEmpty()) {
                trophyMeasurementDao.insertTrophyMeasurements(newTrophyMeasurements)
            }
            insertionSuccess.postValue(true)
        }
    }

    fun setBasicTrophyDetails(trophy: Trophy, categoryId: Int?) {
        trophyEntity.value = trophy
        measurementCategoryId.value = categoryId
    }

    fun setArguments(trophyId: Int?, huntId: Int?, originFragment: String?) {
        this.trophyId = trophyId
        this.huntId = huntId
        this.originFragment = originFragment
    }

    fun setTrophyWeight(weight: Double) {
        trophyEntity.value?.weight = weight
    }

    fun setTrophyMeasurements(trophyMeasurements: List<TrophyMeasurement>) {
        trophyMeasurementsEntity.value = trophyMeasurements
    }

    fun getTrophyDetails(): LiveData<Trophy> = liveData {
        emit(trophyEntity.value!!)
    }

    fun getTrophyId(): LiveData<Int> = liveData {
        emit(trophyId!!)
    }

    fun getHuntId(): LiveData<Int> = liveData {
        emit(huntId!!)
    }

    fun getOriginFragment(): LiveData<String> = liveData {
        emit(originFragment!!)
    }

    fun getMeasurementCategoryId(): LiveData<Int> = liveData {
        emit(measurementCategoryId.value!!)
    }

    fun setImages(images: MutableSet<String>) {
        trophyEntity.value?.imagePaths = images.toList()
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