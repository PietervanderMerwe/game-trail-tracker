package com.epilogs.game_trail_tracker.utils

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

object ImagePickerUtil {
    fun openImagePicker(fragment: Fragment, requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        fragment.startActivityForResult(Intent.createChooser(intent, "Select Pictures"), requestCode)
    }

    fun extractSelectedImages(data: Intent?): List<Uri> {
        return when {
            data?.clipData != null -> {
                // Multiple images selected
                val clipData = data.clipData!!
                (0 until clipData.itemCount).map { index ->
                    clipData.getItemAt(index).uri
                }
            }
            data?.data != null -> {
                // Single image selected
                listOf(data.data!!)
            }
            else -> emptyList()
        }
    }
}