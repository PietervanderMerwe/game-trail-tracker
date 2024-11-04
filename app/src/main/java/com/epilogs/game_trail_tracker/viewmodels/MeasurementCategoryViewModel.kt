package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.MeasurementCategoryDao
import com.epilogs.game_trail_tracker.database.entities.MeasurementCategory
import com.epilogs.game_trail_tracker.database.entities.MeasurementType
import kotlinx.coroutines.launch

class MeasurementCategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val measurementCategoryDao: MeasurementCategoryDao = db.measurementCategoryDao()
    private val _insertionId = MutableLiveData<Long?>()
    val insertionId: LiveData<Long?> = _insertionId
    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()

    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertMeasurementCategory(measurementCategory: MeasurementCategory) = viewModelScope.launch {
        val measurementTypeId = measurementCategoryDao.insertMeasurementCategory(measurementCategory)
        _insertionId.postValue(measurementTypeId)
        insertionSuccess.postValue(true)
    }

    fun getAllMeasurementCategories(): LiveData<List<MeasurementCategory>> = liveData {
        val measurementCategories = measurementCategoryDao.getAllMeasurementCategories()
        emit(measurementCategories)
    }

    fun resetInsertionId() {
        _insertionId.value = null
    }

    fun getMeasurementCategoryById(id: Int): LiveData<MeasurementCategory?> = liveData {
        val measurementCategory = measurementCategoryDao.getMeasurementCategoryById(id)
        emit(measurementCategory)
    }

    fun updateMeasurementCategory(measurementCategory: MeasurementCategory) = viewModelScope.launch {
        measurementCategoryDao.updateMeasurementCategory(measurementCategory)
        updateSuccess.postValue(true)
    }

    fun deleteMeasurementCategory(measurementCategory: MeasurementCategory) = viewModelScope.launch {
        measurementCategoryDao.deleteMeasurementCategory(measurementCategory)
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