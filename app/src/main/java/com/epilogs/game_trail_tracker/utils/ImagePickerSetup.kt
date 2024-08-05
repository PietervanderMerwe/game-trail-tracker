package com.epilogs.game_trail_tracker.utils

import android.content.Intent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class ImagePickerSetup(
    private val fragment: Fragment,
    private val maxImages: Int,
    private val onImagesSelected: (List<String>) -> Unit
) {
    private val pickMultipleMedia =
        fragment.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxImages)) { uris ->
            val selectedImageUris = uris.map { uri ->
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                fragment.requireActivity().contentResolver.takePersistableUriPermission(uri, takeFlags)
                uri.toString()
            }
            onImagesSelected(selectedImageUris)
        }

    fun pickImages() {
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }
}