package com.epilogs.game_trail_tracker.fragments.weapon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponAddBinding

class WeaponAddFragment : Fragment() {
    private val viewModel: WeaponViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private val selectedImageUris = mutableListOf<String>()
    private lateinit var documentPickerLauncher: ActivityResultLauncher<Array<String>>
    private var weaponId: Int? = null
    private lateinit var binding: FragmentWeaponAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        documentPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            uris.forEach { uri ->
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                requireActivity().contentResolver.takePersistableUriPermission(uri, takeFlags)
                selectedImageUris.add(uri.toString())
            }
            imageAdapter.updateImages(selectedImageUris)
        }

        weaponId = arguments?.getInt("id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeaponAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.buttonSaveWeapon.setOnClickListener {
            val name = binding.editTextWeaponName.text.toString()
            val notes = binding.editTextWeaponNotes.text.toString()
            if (weaponId == 0) saveWeapon(name, notes) else updateWeapon(name, notes)
        }

        if (weaponId != 0) setupEdit()

        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                uris.forEach { uri ->
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    requireActivity().contentResolver.takePersistableUriPermission(uri, takeFlags)
                    selectedImageUris.add(uri.toString())
                }
                imageAdapter.updateImages(selectedImageUris)
            }

        binding.buttonSelectWeaponImages.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }

        sharedViewModel.selectedImages.observe(viewLifecycleOwner) { images ->
            selectedImageUris.clear()
            selectedImageUris.addAll(images)
            imageAdapter.updateImages(selectedImageUris)
        }

        setupRecyclerView(binding.imagesWeaponRecyclerView)
        observeViewModel()
    }

    private fun setupEdit() {
        binding.buttonSaveWeapon.text = getString(R.string.button_update)
        binding.addWeaponText.text = getString(R.string.update_weapon)
        viewModel.getWeaponById(weaponId!!).observe(viewLifecycleOwner) { weapon ->
            binding.editTextWeaponName.setText(weapon?.name)
            binding.editTextWeaponNotes.setText(weapon?.notes)
            setupImageAdapter(weapon?.imagePaths?.toMutableList() ?: mutableListOf())

            imageAdapter = ImagesAdapter(
                weapon?.imagePaths?.toMutableList() ?: mutableListOf()
            ) { imageUrl, position ->
                val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                    putStringArrayListExtra("image_urls", ArrayList(weapon?.imagePaths))
                    putExtra("image_position", position)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                }
                context?.startActivity(intent)
            }

            binding.imagesWeaponRecyclerView.adapter = imageAdapter
            binding.imagesWeaponRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            selectedImageUris.addAll(weapon?.imagePaths ?: mutableListOf())
        }
    }

    private fun saveWeapon(name: String, notes: String) {
        val weapon = Weapon(name = name, notes = notes, imagePaths = selectedImageUris)
        viewModel.insertWeapon(weapon)
    }

    private fun updateWeapon(name: String, notes: String) {
        val weapon = Weapon(id = weaponId, name = name, notes = notes, imagePaths = selectedImageUris)
        viewModel.updateWeapon(weapon)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        setupImageAdapter(selectedImageUris)
        recyclerView.adapter = imageAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupImageAdapter(imageUris: MutableList<String>) {
        imageAdapter = ImagesAdapter(imageUris) { imageUri, position ->
            val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                putStringArrayListExtra("image_urls", ArrayList(imageUris))
                putExtra("image_position", position)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
            context?.startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner) { success ->
            if (success == true) {
                binding.editTextWeaponName.text?.clear()
                binding.editTextWeaponNotes.text?.clear()
                viewModel.resetInsertionSuccess()
                imageAdapter.clearImages()
                showCheckMark()
            }
        }

        viewModel.getUpdateSuccess().observe(viewLifecycleOwner) { success ->
            if (success == true) findNavController().navigateUp()
        }
    }

    private fun showCheckMark() {
        binding.checkMarkWeaponAdd.let { checkMarkImageView ->
            checkMarkImageView.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                checkMarkImageView.visibility = View.GONE
            }, 3000)
        }
        sharedViewModel.notifyWeaponsUpdated()
    }
}