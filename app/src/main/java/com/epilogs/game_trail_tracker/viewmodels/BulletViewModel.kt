package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.epilogs.game_trail_tracker.database.AppDatabase
import com.epilogs.game_trail_tracker.database.DatabaseProvider
import com.epilogs.game_trail_tracker.database.daos.BulletDao
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Bullet
import kotlinx.coroutines.launch

class BulletViewModel (application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = DatabaseProvider.getDatabase(application)
    private val bulletDao: BulletDao = db.bulletDao()
    private val _insertionId = MutableLiveData<Long?>()
    val insertionId: LiveData<Long?> = _insertionId
    private val insertionSuccess = MutableLiveData<Boolean?>()
    private val updateSuccess = MutableLiveData<Boolean?>()
    private val deleteSuccess = MutableLiveData<Boolean?>()

    fun getUpdateSuccess(): LiveData<Boolean?> = updateSuccess
    fun getDeleteSuccess(): LiveData<Boolean?> = deleteSuccess
    fun getInsertionSuccess(): MutableLiveData<Boolean?> = insertionSuccess

    fun insertBullet(bullet: Bullet) = viewModelScope.launch {
        val bulletId = bulletDao.insertBullet(bullet)
        _insertionId.postValue(bulletId)
        insertionSuccess.postValue(true)
    }

    fun getAllBullets(): LiveData<List<Bullet>> = liveData {
        val weapons = bulletDao.getAllBullets()
        emit(weapons)
    }

    fun resetInsertionId() {
        _insertionId.value = null
    }

    fun getBulletById(id: Int): LiveData<Bullet?> = liveData {
        val weapon = bulletDao.getBulletById(id)
        emit(weapon)
    }

    fun updateBullet(bullet: Bullet) = viewModelScope.launch {
        bulletDao.updateBullet(bullet)
        updateSuccess.postValue(true)
    }

    fun deleteBullet(bullet: Bullet) = viewModelScope.launch {
        bulletDao.deleteBullet(bullet)
        deleteSuccess.postValue(true)
    }

    fun getBulletsByWeaponId(weaponId: Int): LiveData<List<Bullet>> = liveData {
        val bullets = bulletDao.getBulletsByWeaponId(weaponId)
        emit(bullets)
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