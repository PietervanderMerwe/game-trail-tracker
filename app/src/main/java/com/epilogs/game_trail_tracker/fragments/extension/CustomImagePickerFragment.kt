package com.epilogs.game_trail_tracker.fragments.extension

import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts

class CustomImagePickerFragment : Fragment() {
    private lateinit var imageRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_custom_image_picker, container, false)
        imageRecyclerView = view.findViewById(R.id.imagesCustomRecyclerView)
        imageRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val images = loadImagesFromGallery().map { it.toString() }.toMutableList()
        imageRecyclerView.adapter = ImagesAdapter(mutableListOf()) { imagePath, position ->
            Log.d("CustomImagePickerFragment", "Clicked image: $imagePath at position $position")
        }
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    val images = loadImagesFromGallery().map { it.toString() }.toMutableList()
                    (imageRecyclerView.adapter as ImagesAdapter).updateImages(images)
                } else {
                    // Explain to the user that the feature is unavailable because the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to system settings in an attempt to convince the user to change their decision.
                }
            }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("CustomImagePickerFragment", "Requesting READ_EXTERNAL_STORAGE permission")
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            Log.d("CustomImagePickerFragment", "READ_EXTERNAL_STORAGE permission already granted")
            val images = loadImagesFromGallery().map { it.toString() }.toMutableList()
            (imageRecyclerView.adapter as ImagesAdapter).updateImages(images)
        }
    }

    private fun loadImagesFromGallery(): List<Uri> {
        val images = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        try {
            requireActivity().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    images.add(uri)
                }
            }
            Log.d("CustomImagePickerFragment", "Loaded ${images.size} images from gallery")
        } catch (e: Exception) {
            Log.e("CustomImagePickerFragment", "Error loading images from gallery", e)
        }
        return images
    }

    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 100
    }
}