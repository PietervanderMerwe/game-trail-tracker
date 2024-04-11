package com.epilogs.game_trail_tracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val weaponsUpdateSignal = MutableLiveData<Boolean>()
    private val locationsUpdateSignal = MutableLiveData<Boolean>()
    val selectedImages = MutableLiveData<List<String>>()

    fun notifyWeaponsUpdated() {
        weaponsUpdateSignal.value = true
    }

    fun getWeaponsUpdateSignal(): LiveData<Boolean> = weaponsUpdateSignal

    fun resetWeaponsUpdatedSignal() {
        weaponsUpdateSignal.value = false
    }
    fun notifyLocationsUpdated() {
        locationsUpdateSignal.value = true
    }

    fun getLocationsUpdateSignal(): LiveData<Boolean> = locationsUpdateSignal

    fun resetLocationsUpdatedSignal() {
        locationsUpdateSignal.value = false
    }
}