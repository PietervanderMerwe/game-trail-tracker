package com.epilogs.game_trail_tracker.utils

import android.content.Intent
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import gun0912.tedimagepicker.builder.TedImagePicker

class ImagePickerSetup(
    private val fragment: Fragment,
    private val maxImages: Int,
    private val onImagesSelected: (List<String>) -> Unit
) {

    fun pickImages(previouslySelectedUris: List<String> = emptyList()) {
        TedImagePicker.with(fragment.requireContext())
            .max(maxImages, "Max images is $maxImages") // Set the maximum number of images to select
            .selectedUri(previouslySelectedUris.map { Uri.parse(it) }) // Pass the previously selected URIs
            .startMultiImage { uriList ->
                val selectedImageUris = uriList.map { uri ->
                    try {
                        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        fragment.requireActivity().contentResolver.takePersistableUriPermission(uri, takeFlags)
                    } catch (e: SecurityException) {
                        // Log or handle the exception if persistable permission is not granted
                        e.printStackTrace()
                    }
                    uri.toString()
                }
                onImagesSelected(selectedImageUris)
            }
    }
}