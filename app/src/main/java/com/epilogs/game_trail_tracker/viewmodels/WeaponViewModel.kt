package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.WeaponDao
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Weapon
import kotlinx.coroutines.launch

class WeaponViewModel (application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val weaponDao: WeaponDao = db.weaponDao()

    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()

    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertWeapon(weapon: Weapon) = viewModelScope.launch {
        weaponDao.insertWeapon(weapon)
        insertionSuccess.postValue(true)
    }

    fun getAllWeapons(): LiveData<List<Weapon>> = liveData {
        val weapons = weaponDao.getAllWeapons()
        emit(weapons)
    }

    fun getWeaponById(id: Int): LiveData<Weapon?> = liveData {
        val weapon = weaponDao.getWeaponById(id)
        emit(weapon)
    }

    fun updateWeapon(weapon: Weapon) = viewModelScope.launch {
        weaponDao.updateWeapon(weapon)
        updateSuccess.postValue(true)
    }

    fun deleteWeapon(weapon: Weapon) = viewModelScope.launch {
        weaponDao.deleteWeapon(weapon)
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