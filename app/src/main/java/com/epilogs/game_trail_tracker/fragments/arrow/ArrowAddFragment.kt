package com.epilogs.game_trail_tracker.fragments.arrow

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
import com.epilogs.game_trail_tracker.database.entities.Arrow
import com.epilogs.game_trail_tracker.database.entities.Bullet
import com.epilogs.game_trail_tracker.databinding.FragmentArrowAddBinding
import com.epilogs.game_trail_tracker.databinding.FragmentBulletAddBinding
import com.epilogs.game_trail_tracker.fragments.bullet.BulletAddFragmentDirections
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup
import com.epilogs.game_trail_tracker.viewmodels.ArrowViewModel
import com.epilogs.game_trail_tracker.viewmodels.BulletViewModel

class ArrowAddFragment : Fragment() {

    private var weaponId: Int? = null
    private var arrowId: Int? = null
    private val viewModel: ArrowViewModel by viewModels()
    private lateinit var binding: FragmentArrowAddBinding
    private val selectedImageUris = mutableSetOf<String>()
    private lateinit var imagePickerSetup: ImagePickerSetup
    private lateinit var imageAdapterSetup: ImageAdapterSetup
    private var currentArrow: Arrow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weaponId = it.getInt("weaponId")
            arrowId = it.getInt("arrowId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_arrow_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArrowAddBinding.bind(view)

        setupUI()
    }

    private fun setupUI(){
        binding.buttonSaveArrow.setOnClickListener {
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

        binding.buttonSelectArrowImages.setOnClickListener {
            imagePickerSetup.pickImages(selectedImageUris.toMutableList())
        }

        setupImageAdapter(selectedImageUris)
        observeViewModel()

        if (arrowId != 0) {
            setupEditScreen()
        }
    }

    private fun setupImageAdapter(imageUris: MutableSet<String>) {
        imageAdapterSetup = ImageAdapterSetup(
            recyclerView = binding.imagesArrowRecyclerView,
            imageUris = imageUris
        )
        imageAdapterSetup.setupAdapter()
    }

    private fun setupEditScreen() {
        binding.addArrowText.text = getString(R.string.update_arrow)
        binding.buttonSaveArrow.text = getString(R.string.button_update)
        binding.buttonDeleteArrow.visibility = View.VISIBLE

        binding.buttonDeleteArrow.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        viewModel.getArrowById(arrowId!!).observe(viewLifecycleOwner) { arrow ->
            currentArrow = arrow

            binding.manufacturer.setText(arrow?.manufacturer)
            binding.shaftMaterial.setText(arrow?.shaftMaterial)
            binding.length.setText(arrow?.length.toString())
            binding.fletchingType.setText(arrow?.fletchingType)
            binding.pointType.setText(arrow?.pointType)
            binding.pointWeight.setText(arrow?.pointWeight.toString())
            binding.nockType.setText(arrow?.nockType)
            binding.notes.setText(arrow?.notes)

            setupImageAdapter(arrow?.imagePaths?.toMutableSet() ?: mutableSetOf())

            selectedImageUris.addAll(arrow?.imagePaths ?: mutableListOf())
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
                            ArrowAddFragmentDirections.actionArrowAddFragmentToArrowViewDetailFragment(
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
        val arrow = Arrow(
            manufacturer = binding.manufacturer.text.toString(),
            shaftMaterial = binding.shaftMaterial.text.toString(),
            length = binding.length.text.toString().toDoubleOrNull() ?: 0.0,
            fletchingType = binding.fletchingType.text.toString(),
            pointType = binding.pointType.text.toString(),
            pointWeight = binding.pointWeight.text.toString().toDoubleOrNull() ?: 0.0,
            nockType = binding.nockType.text.toString(),
            notes = binding.notes.text.toString(),
            imagePaths = selectedImageUris.toMutableList(),
            weaponId = weaponId)

        if(this.arrowId != 0) {
            arrow.id = this.arrowId!!
            viewModel.updateArrow(arrow)
            binding.buttonDeleteArrow.visibility = View.GONE
        }
        else {
            viewModel.insertArrow(arrow)
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this arrow?")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteArrow(currentArrow!!)
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

    companion object {
        @JvmStatic
        fun newInstance() =
            ArrowAddFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}