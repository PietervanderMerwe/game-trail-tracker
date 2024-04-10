package com.epilogs.game_trail_tracker.utils

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

class ImagePickerUtil(activity: Activity) {

    private var weakActivityRef = WeakReference<Activity>(activity)
    private var imagePickCallback: ((List<Uri>) -> Unit)? = null
    private var imageCaptureCallback: ((Uri) -> Unit)? = null
    private var latestTmpUri: Uri? = null

    private val pickImagesLauncher = weakActivityRef.get()?.let { safeActivity ->
        if (safeActivity is ComponentActivity) {
            safeActivity.registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                imagePickCallback?.invoke(uris)
            }
        } else {
            null
        }
    }

    fun pickImageFromGallery(callback: (List<Uri>) -> Unit) {
        imagePickCallback = callback
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            pickImagesLauncher?.launch("image/*")
        }
    }

    private val captureImageLauncher = weakActivityRef.get()?.let { safeActivity ->
        if (safeActivity is ComponentActivity) {
            safeActivity.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    latestTmpUri?.let { uri ->
                        imageCaptureCallback?.invoke(uri)
                    }
                }
            }
        } else {
            null
        }
    }

    fun captureImageFromCamera(callback: (Uri) -> Unit) {
        imageCaptureCallback = callback
        requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            latestTmpUri = generateFileUri()
            captureImageLauncher?.launch(latestTmpUri)
        }
    }

    private val permissionLauncher = weakActivityRef.get()?.let { safeActivity ->
        if (safeActivity is ComponentActivity) {
            safeActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val allPermissionsGranted = permissions.entries.all { it.value }
                if (allPermissionsGranted) {
                    imageCaptureCallback?.invoke(latestTmpUri!!)
                } else {
                    // Handle permission denial gracefully
                }
            }
        } else {
            null
        }
    }

    private fun requestPermissions(permissions: Array<String>, onGranted: () -> Unit) {
        permissionLauncher?.launch(permissions)
    }

    private fun generateFileUri(): Uri? {
        val activity = weakActivityRef.get()
        if (activity != null) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString() + "_picture.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
            }
            return try {
                activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            } catch (e: Exception) {
                Log.e("ImagePickerUtil", "Failed to create file URI", e)
                null
            }
        } else {
            Log.e("ImagePickerUtil", "Activity reference is null")
            return null
        }
    }
}