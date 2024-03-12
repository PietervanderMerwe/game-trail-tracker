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
import com.epilogs.game_trail_tracker.database.entities.Weapon
import kotlinx.coroutines.launch

class WeaponViewModel (application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val weaponDao: WeaponDao = db.weaponDao()

    private val insertionSuccess = MutableLiveData<Boolean?>()

    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertWeapon(weapon: Weapon) = viewModelScope.launch {
        weaponDao.insertWeapon(weapon)
        insertionSuccess.postValue(true)
    }

    fun getAllWeapons(): LiveData<List<Weapon>> = liveData {
        val weapons = weaponDao.getAllWeapons()
        emit(weapons)
    }

    fun resetInsertionSuccess() {
        insertionSuccess.value = null
    }
}