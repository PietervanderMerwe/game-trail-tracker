package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.TrophyDao
import com.epilogs.game_trail_tracker.database.daos.HuntDao
import com.epilogs.game_trail_tracker.database.entities.Trophy
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.epilogs.game_trail_tracker.database.daos.WeaponDao
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.database.entities.Weapon

class AnimalViewModel (application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val trophyDao: TrophyDao = db.animalDao()
    private val huntDao: HuntDao = db.huntDao()
    private val weaponDao: WeaponDao = db.weaponDao()

    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()

    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertAnimal(trophy: Trophy) = viewModelScope.launch {
        trophyDao.insertTrophy(trophy)
        insertionSuccess.postValue(true)
    }

    fun getAllAnimals(): LiveData<List<Trophy>> = liveData {
        val animals = trophyDao.getAllTrophies()
        emit(animals)
    }

    fun getAllLocations(): LiveData<List<Hunt>> = liveData {
        val locations = huntDao.getAllHunts()
        emit(locations)
    }

    fun getAllWeapons(): LiveData<List<Weapon>> = liveData {
        val weapons = weaponDao.getAllWeapons()
        emit(weapons)
    }

    fun getAnimalById(id: Int): LiveData<Trophy?> = liveData {
        val animal = trophyDao.getTrophyById(id)
        emit(animal)
    }
    fun updateAnimal(trophy: Trophy) = viewModelScope.launch {
        trophyDao.updateTrophy(trophy)
        updateSuccess.postValue(true)
    }

    fun deleteAnimal(trophy: Trophy) = viewModelScope.launch {
        trophyDao.deleteTrophy(trophy)
        deleteSuccess.postValue(true)
    }

    fun getLatestAnimal(): LiveData<Trophy?> = liveData {
        val animal = trophyDao.getLatestTrophy()
        emit(animal)
    }

    fun getAnimalsByHuntId(huntId: Int): LiveData<List<Trophy>> = liveData {
        val trophies = trophyDao.getTrophiesByHuntId(huntId)
        emit(trophies)
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