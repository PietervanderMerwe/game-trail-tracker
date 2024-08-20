package com.epilogs.game_trail_tracker.fragments.bullet

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Bullet
import com.epilogs.game_trail_tracker.databinding.FragmentBulletAddBinding
import com.epilogs.game_trail_tracker.fragments.weapon.WeaponAddFragmentDirections
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup
import com.epilogs.game_trail_tracker.viewmodels.BulletViewModel

class BulletAddFragment : Fragment() {

    private var weaponId: Int? = null
    private var bulletId: Int? = null
    private val viewModel: BulletViewModel by viewModels()
    private lateinit var binding: FragmentBulletAddBinding
    private val selectedImageUris = mutableSetOf<String>()
    private lateinit var imagePickerSetup: ImagePickerSetup
    private lateinit var imageAdapterSetup: ImageAdapterSetup
    private var type: String = ""
    private var currentBullet: Bullet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weaponId = it.getInt("weaponId")
            bulletId = it.getInt("bulletId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bullet_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBulletAddBinding.bind(view)

        setupUI()
        setupTabToggleBtn()
    }

    private fun setupTabToggleBtn() {
        binding.tabToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.storeBoughtToggle -> {
                        binding.storeBoughtToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_color))
                        binding.storeBoughtToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                        binding.handLoadedToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
                        binding.handLoadedToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black_disabled))

                        type = "Store"

                        showInputs()
                        hideHandLoaded()
                    }
                    R.id.handLoadedToggle -> {
                        binding.handLoadedToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_color))
                        binding.handLoadedToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                        binding.storeBoughtToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
                        binding.storeBoughtToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black_disabled))

                        type = "Hand"

                        showInputs()
                        showHandLoaded()
                    }
                }
            }
        }
    }

    private fun setupUI(){
        binding.buttonSaveBullet.setOnClickListener {
            setupSaveListener()
        }

        imagePickerSetup = ImagePickerSetup(
            fragment = this,
            maxImages = 5,
            onImagesSelected = { images ->
                selectedImageUris.clear()
                selectedImageUris.addAll(images)
                imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
            }
        )

        binding.buttonSelectBulletImages.setOnClickListener {
            imagePickerSetup.pickImages()
        }

        setupImageAdapter(selectedImageUris)
        observeViewModel()

        if (bulletId != 0) {
            setupEditScreen()
        }
    }

    private fun setupImageAdapter(imageUris: MutableSet<String>) {
        imageAdapterSetup = ImageAdapterSetup(
            recyclerView = binding.imagesBulletRecyclerView,
            imageUris = imageUris
        )
        imageAdapterSetup.setupAdapter()
    }

    private fun setupEditScreen() {
        binding.addBulletText.text = getString(R.string.update_bullet)
        binding.buttonSaveBullet.text = getString(R.string.button_update)
        binding.buttonDeleteBullet.visibility = View.VISIBLE

        binding.buttonDeleteBullet.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        viewModel.getBulletById(bulletId!!).observe(viewLifecycleOwner) { bullet ->
            currentBullet = bullet
            if(bullet?.type == "Store") {
                binding.manufacturer.setText(bullet.manufacturer)
            }
            else {
                binding.manufacturer.setText(bullet?.bulletBrand)
            }
            binding.bulletType.setText(bullet?.bulletType)
            binding.bulletWeight.setText(bullet?.bulletWeight.toString())
            binding.bulletCaseBrand.setText(bullet?.caseBrand)
            binding.bulletPowderBrand.setText(bullet?.powderName)
            binding.bulletPowderWeight.setText(bullet?.powderWeight.toString())
            binding.bulletNotes.setText(bullet?.notes)

            setupImageAdapter(bullet?.imagePaths?.toMutableSet() ?: mutableSetOf())

            selectedImageUris.addAll(bullet?.imagePaths ?: mutableListOf())
        }
    }

    private fun observeViewModel() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner) { success ->
            if (success == true) {
                viewModel.resetInsertionSuccess()
                imageAdapterSetup.clearImages()

                viewModel.insertionId.observe(viewLifecycleOwner) { id ->
                    id?.let {
                        val action =
                            BulletAddFragmentDirections.actionBulletAddFragmentToBulletViewDetailFragment(
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

    private fun setupSaveListener() {
        val bullet = Bullet(
            type = type,
            manufacturer = binding.manufacturer.text.toString(),
            bulletBrand = binding.manufacturer.text.toString(),
            bulletType = binding.bulletType.text.toString(),
            bulletWeight = binding.bulletWeight.text.toString().toDoubleOrNull() ?: 0.0,
            caseBrand = binding.bulletCaseBrand.text.toString(),
            powderName = binding.bulletPowderBrand.text.toString(),
            weaponId = weaponId,
            powderWeight = binding.bulletPowderWeight.text.toString().toDoubleOrNull() ?: 0.0,
            notes = binding.bulletNotes.text.toString(),
            imagePaths = selectedImageUris.toMutableList())

        if(this.bulletId != 0) {
            bullet.id = this.bulletId!!
            viewModel.updateBullet(bullet)
            binding.buttonDeleteBullet.visibility = View.GONE
        }
        else {
            viewModel.insertBullet(bullet)
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this bullet?")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteBullet(currentBullet!!)
                val action = BulletAddFragmentDirections.actionBulletAddFragmentToWeaponFragment()
                findNavController().navigate(action)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_color))
        }
        dialog.show()
    }

    private fun showInputs() {
        binding.manufacturerContainer.visibility = View.VISIBLE
        binding.bulletWeightContainer.visibility = View.VISIBLE
        binding.bulletTypeContainer.visibility = View.VISIBLE
        binding.bulletNotesContainer.visibility = View.VISIBLE
    }

    private fun showHandLoaded() {
        binding.bulletCaseBrandContainer.visibility = View.VISIBLE
        binding.bulletPowderBrandContainer.visibility = View.VISIBLE
        binding.bulletPowderWeightContainer.visibility = View.VISIBLE
    }

    private fun hideHandLoaded() {
        binding.bulletCaseBrandContainer.visibility = View.GONE
        binding.bulletPowderBrandContainer.visibility = View.GONE
        binding.bulletPowderWeightContainer.visibility = View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BulletAddFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}