package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.ArrowDao
import com.epilogs.game_trail_tracker.database.entities.Arrow
import kotlinx.coroutines.launch

class ArrowViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val arrowDao: ArrowDao = db.arrowDao()
    private val _insertionId = MutableLiveData<Long?>()
    val insertionId: LiveData<Long?> = _insertionId
    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()

    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertArrow(arrow: Arrow) = viewModelScope.launch {
        val arrowId = arrowDao.insertArrow(arrow)
        _insertionId.postValue(arrowId)
        insertionSuccess.postValue(true)
    }

    fun getAllArrows(): LiveData<List<Arrow>> = liveData {
        val arrows = arrowDao.getAllArrows()
        emit(arrows)
    }

    fun resetInsertionId() {
        _insertionId.value = null
    }

    fun getArrowById(id: Int): LiveData<Arrow?> = liveData {
        val arrow = arrowDao.getArrowsById(id)
        emit(arrow)
    }

    fun updateArrow(arrow: Arrow) = viewModelScope.launch {
        arrowDao.updateArrow(arrow)
        updateSuccess.postValue(true)
    }

    fun deleteArrow(arrow: Arrow) = viewModelScope.launch {
        arrowDao.deleteArrow(arrow)
        deleteSuccess.postValue(true)
    }

    fun getArrowByWeaponId(weaponId: Int): LiveData<List<Arrow>> = liveData {
        val arrows = arrowDao.getArrowsByWeaponId(weaponId)
        emit(arrows)
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