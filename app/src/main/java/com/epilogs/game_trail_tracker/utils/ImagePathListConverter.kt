package com.epilogs.game_trail_tracker.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ImagePathListConverter {
    @TypeConverter
    fun fromImagePathsList(value: List<String>?): String? = Gson().toJson(value)

    @TypeConverter
    fun toImagePathsList(value: String?): List<String>? {
        val type = object : TypeToken<List<String>?>() {}.type
        return Gson().fromJson(value, type)
    }
}