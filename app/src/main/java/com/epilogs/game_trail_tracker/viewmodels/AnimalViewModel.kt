package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.AnimalDao
import com.epilogs.game_trail_tracker.database.daos.LocationDao
import com.epilogs.game_trail_tracker.database.entities.Animal
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.epilogs.game_trail_tracker.database.daos.WeaponDao
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.database.entities.Weapon

class AnimalViewModel (application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val animalDao: AnimalDao = db.animalDao()
    private val locationDao: LocationDao = db.locationDao()
    private val weaponDao: WeaponDao = db.weaponDao()

    private val insertionSuccess = MutableLiveData<Boolean?>()

    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertAnimal(animal: Animal) = viewModelScope.launch {
        animalDao.insertAnimal(animal)
        insertionSuccess.postValue(true)
    }

    fun getAllAnimals(): LiveData<List<Animal>> = liveData {
        val animals = animalDao.getAllAnimals()
        emit(animals)
    }

    fun getAllLocations(): LiveData<List<Location>> = liveData {
        val locations = locationDao.getAllLocations()
        emit(locations)
    }

    fun getAllWeapons(): LiveData<List<Weapon>> = liveData {
        val weapons = weaponDao.getAllWeapons()
        emit(weapons)
    }

    fun getAnimalById(id: Int): LiveData<Animal?> = liveData {
        val animal = animalDao.getAnimalById(id)
        emit(animal)
    }

    fun resetInsertionSuccess() {
        insertionSuccess.value = null
    }
}