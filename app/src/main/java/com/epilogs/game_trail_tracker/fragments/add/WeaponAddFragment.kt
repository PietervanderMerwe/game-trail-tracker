package com.epilogs.game_trail_tracker.fragments.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.utils.ImagePickerUtil
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel

class WeaponAddFragment : Fragment() {
    private val viewModel: WeaponViewModel by viewModels()
    private val pickImagesRequestCode = 100
    private val selectedImageUris = mutableListOf<String>()
    private lateinit var imageAdapter: ImagesAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weapon_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextWeaponName: EditText = view.findViewById(R.id.editTextWeaponName)
        val editTextWeaponNotes : EditText = view.findViewById(R.id.editTextWeaponNotes)
        val buttonSaveWeapon: Button = view.findViewById(R.id.buttonSaveWeapon)
        val buttonSelectLocationImages: Button = view.findViewById(R.id.buttonSelectWeaponImages)
        val imagePaths = selectedImageUris

        buttonSaveWeapon.setOnClickListener {
            val name = editTextWeaponName.text.toString()
            val notes = editTextWeaponNotes.text.toString()
            val weapon = Weapon(name = name, notes = notes, imagePaths = imagePaths)

            viewModel.insertWeapon(weapon)
        }

        buttonSelectLocationImages.setOnClickListener {
            showImagePicker()
        }

        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesWeaponRecyclerView)
        imageAdapter = ImagesAdapter(mutableListOf())
        imagesRecyclerView.adapter = imageAdapter
        imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewModel.getInsertionSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                editTextWeaponName.text.clear()
                editTextWeaponNotes.text.clear()
                viewModel.resetInsertionSuccess()
                imageAdapter.clearImages()
                val checkMarkImageView: ImageView = view.findViewById(R.id.checkMarkWeaponAdd)
                checkMarkImageView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    checkMarkImageView.visibility = View.GONE
                }, 3000)
                Log.d("Weapon Add notify","True")
                sharedViewModel.notifyWeaponsUpdated()
            }
        })
    }

    private fun showImagePicker() {
        ImagePickerUtil.openImagePicker(this, pickImagesRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImagesRequestCode && resultCode == Activity.RESULT_OK) {
            val imagesUris = ImagePickerUtil.extractSelectedImages(data)
            selectedImageUris.clear()
            selectedImageUris.addAll(imagesUris.map { it.toString() })
            imageAdapter.updateImages(selectedImageUris)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WeaponAddFragment().apply {
            }
    }
}