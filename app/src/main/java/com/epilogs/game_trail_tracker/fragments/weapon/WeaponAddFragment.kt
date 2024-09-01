package com.epilogs.game_trail_tracker.fragments.weapon

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.viewmodels.SharedViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponAddBinding
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup

class WeaponAddFragment : Fragment() {
    private val viewModel: WeaponViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val selectedImageUris = mutableSetOf<String>()
    private var weaponId: Int? = null
    private lateinit var binding: FragmentWeaponAddBinding
    private var currentWeapon: Weapon? = null
    private lateinit var imagePickerSetup: ImagePickerSetup
    private lateinit var imageAdapterSetup: ImageAdapterSetup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            setupSaveListener()
        }

        if (weaponId != 0) setupEdit()

        imagePickerSetup = ImagePickerSetup(
            fragment = this,
            maxImages = 5,
            onImagesSelected = { images ->
                selectedImageUris.clear()
                selectedImageUris.addAll(images)
                imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
            }
        )

        binding.buttonSelectWeaponImages.setOnClickListener {
            imagePickerSetup.pickImages(selectedImageUris.toMutableList())
        }

        sharedViewModel.selectedImages.observe(viewLifecycleOwner) { images ->
            selectedImageUris.clear()
            selectedImageUris.addAll(images)
            imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
        }

        if (weaponId == 0) setupImageAdapter(selectedImageUris)
        observeViewModel()
    }

    private fun setupEdit() {
        binding.buttonSaveWeapon.text = getString(R.string.button_update)
        binding.addWeaponText.text = getString(R.string.update_weapon)
        binding.buttonDeleteWeapon.visibility = View.VISIBLE

        binding.buttonDeleteWeapon.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        viewModel.getWeaponById(weaponId!!).observe(viewLifecycleOwner) { weapon ->
            currentWeapon = weapon
            binding.editTextWeaponName.setText(weapon?.name)
            binding.editTextWeaponNotes.setText(weapon?.notes)
            setupImageAdapter(weapon?.imagePaths?.toMutableSet() ?: mutableSetOf())

            selectedImageUris.addAll(weapon?.imagePaths ?: mutableListOf())
        }
    }

    private fun setupSaveListener() {
        val weapon = Weapon(
            name = binding.editTextWeaponName.text.toString(),
            notes = binding.editTextWeaponNotes.text.toString(),
            imagePaths = selectedImageUris.toMutableList())

        if(this.weaponId != 0) {
            weapon.id = this.weaponId!!
            viewModel.updateWeapon(weapon)
            binding.buttonDeleteWeapon.visibility = View.GONE
        }
        else {
            viewModel.insertWeapon(weapon)
        }

    }

    private fun setupImageAdapter(imageUris: MutableSet<String>) {
        imageAdapterSetup = ImageAdapterSetup(
            recyclerView = binding.imagesWeaponRecyclerView,
            imageUris = imageUris
        )
        imageAdapterSetup.setupAdapter()
    }

    private fun observeViewModel() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner) { success ->
            if (success == true) {
                binding.editTextWeaponName.text?.clear()
                binding.editTextWeaponNotes.text?.clear()
                viewModel.resetInsertionSuccess()
                imageAdapterSetup.clearImages()
                showCheckMark()

                viewModel.insertionId.observe(viewLifecycleOwner) { id ->
                    id?.let {
                        val action =
                            WeaponAddFragmentDirections.actionWeaponAddFragmentToWeaponViewDetailFragment(
                                id.toInt()
                            )
                        findNavController().navigate(action)
                        viewModel.resetInsertionId()
                    }
                }
            }
        }

        viewModel.getUpdateSuccess().observe(viewLifecycleOwner) { success ->
            if (success == true) findNavController().navigateUp()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this weapon?")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteWeapon(currentWeapon!!)
                val action = WeaponAddFragmentDirections.actionWeaponAddFragmentToWeaponFragment()
                findNavController().navigate(action)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton.setTextColor(getColor(requireContext(), R.color.red))
            negativeButton.setTextColor(getColor(requireContext(), R.color.secondary_color))
        }

        dialog.show()
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