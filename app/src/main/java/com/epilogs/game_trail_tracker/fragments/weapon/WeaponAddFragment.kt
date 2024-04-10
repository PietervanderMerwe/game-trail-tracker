package com.epilogs.game_trail_tracker.fragments.weapon

import android.content.Intent
import android.net.Uri
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
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponAddBinding
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponBinding

class WeaponAddFragment : Fragment() {
    private val viewModel: WeaponViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private val selectedImageUris = mutableListOf<String>()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var weaponId: Int? = null
    private lateinit var binding: FragmentWeaponAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                val imagesUris = uris.map(Uri::toString)
                selectedImageUris.clear()
                selectedImageUris.addAll(imagesUris)
                imageAdapter.updateImages(selectedImageUris)
            }

        arguments?.let {
            weaponId = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weapon_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeaponAddBinding.bind(view)

        setupUI()

    }

    private fun setupUI() {


        binding.buttonSaveWeapon.setOnClickListener {
            if (weaponId == 0) {
                saveWeapon(
                    binding.editTextWeaponName.text.toString(),
                    binding.editTextWeaponNotes.text.toString()
                )
            } else {
                updateWeapon(
                    binding.editTextWeaponName.text.toString(),
                    binding.editTextWeaponNotes.text.toString()
                )
            }
        }

        if (weaponId != 0) {
            setupEdit()
        }

        binding.buttonSelectWeaponImages.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        setupRecyclerView(binding.imagesWeaponRecyclerView)
        observeViewModel()
    }

    private fun setupEdit() {
        binding.buttonSaveWeapon.text = "Update"
        binding.addWeaponText.text = "Update weapon"
        viewModel.getWeaponById(weaponId!!).observe(viewLifecycleOwner, Observer { weapon ->
            binding.editTextWeaponName.setText(weapon?.name)
            binding.editTextWeaponNotes.setText(weapon?.notes)

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
        })
    }

    private fun saveWeapon(name: String, notes: String) {
        val weapon = Weapon(name = name, notes = notes, imagePaths = selectedImageUris)
        viewModel.insertWeapon(weapon)
    }

    private fun updateWeapon(name: String, notes: String) {
        val weapon =
            Weapon(id = weaponId, name = name, notes = notes, imagePaths = selectedImageUris)
        viewModel.updateWeapon(weapon)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        imageAdapter = ImagesAdapter(selectedImageUris) { imageUri, position ->
            val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                putStringArrayListExtra("image_urls", ArrayList(selectedImageUris))
                putExtra("image_position", position)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
            context?.startActivity(intent)
        }
        recyclerView.adapter = imageAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun observeViewModel() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                binding.editTextWeaponName.text?.clear()
                binding.editTextWeaponNotes.text?.clear()
                viewModel.resetInsertionSuccess()
                imageAdapter.clearImages()
                showCheckMark()
            }
        })

        viewModel.getUpdateSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                findNavController().navigateUp()
            }
        })
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

    companion object {
        @JvmStatic
        fun newInstance() = WeaponAddFragment()
    }
}
