package com.epilogs.game_trail_tracker.fragments.trophy.add

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyAddImagesBinding
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup
import com.epilogs.game_trail_tracker.viewmodels.TrophyAddSharedViewModel

class TrophyAddImagesFragment : Fragment() {

    private lateinit var binding: FragmentTrophyAddImagesBinding
    private lateinit var imagePickerSetup: ImagePickerSetup
    private lateinit var imageAdapterSetup: ImageAdapterSetup
    private val selectedImageUris = mutableSetOf<String>()
    private val viewModel: TrophyAddSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trophy_add_images, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrophyAddImagesBinding.bind(view)
        hideRecyclerView()
        setImagePicker()
        saveTrophySetup()
        imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
        setupObserveInsertion()
    }

    private fun setupRecyclerView() {
        binding.imagesTrophyRecyclerViewViewDetail.visibility = View.VISIBLE
        binding.imagesTrophyRecyclerViewViewDetail.layoutManager =
            GridLayoutManager(requireContext(), 3)
        binding.addTrophyImageButtonFloat.visibility = View.VISIBLE
        binding.addTrophyImageButton.visibility = View.GONE
    }

    private fun hideRecyclerView() {
        binding.imagesTrophyRecyclerViewViewDetail.visibility = View.GONE
        binding.addTrophyImageButton.visibility = View.VISIBLE
        binding.addTrophyImageButtonFloat.visibility = View.GONE
    }

    private fun setImagePicker() {
        imagePickerSetup = ImagePickerSetup(
            fragment = this,
            maxImages = 5,
            onImagesSelected = { images ->
                selectedImageUris.clear()
                selectedImageUris.addAll(images)
                imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
                viewModel.setImages(images.toMutableSet())
                setupRecyclerView()
            }
        )

        setupImageAdapter(selectedImageUris)

        binding.addTrophyImageButtonFloat.setOnClickListener() {
            imagePickerSetup.pickImages(selectedImageUris.toMutableList())
        }
        binding.addTrophyImageButton.setOnClickListener() {
            imagePickerSetup.pickImages(selectedImageUris.toMutableList())
        }
    }

    private fun saveTrophySetup() {
        binding.buttonSaveAnimal.setOnClickListener {
            viewModel.insertTrophy()
        }
    }

    private fun setupObserveInsertion() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner) { success ->
            if (success == true) {
                viewModel.resetInsertionSuccess()
                viewModel.getOriginFragment().observe(viewLifecycleOwner) { originFragment ->
                when (originFragment) {
                    "huntFragment" -> {
                        viewModel.getHuntId().observe(viewLifecycleOwner) { huntId ->
                            val action =
                                TrophyAddImagesFragmentDirections.actionTrophyAddImagesFragmentToHuntViewDetailFragment(
                                    huntId
                                )
                            findNavController().navigate(action)
                        }
                    }

                    "trophyFragment" -> {
                        val action =
                            TrophyAddImagesFragmentDirections.actionTrophyAddImagesFragmentToTrophyFragment()
                        findNavController().navigate(action)
                    }

                    "TrophyDetailFragment" -> {
                        viewModel.getTrophyId().observe(viewLifecycleOwner) { trophyId ->
                            val action =
                                TrophyAddImagesFragmentDirections.actionTrophyAddImagesFragmentToTrophyViewDetailFragment(
                                    trophyId
                                )
                            findNavController().navigate(action)
                        }
                    }

                    else -> findNavController().navigateUp()
                }
            }
        }
    }
}

private fun setupImageAdapter(imageUris: MutableSet<String>) {
    imageAdapterSetup = ImageAdapterSetup(
        recyclerView = binding.imagesTrophyRecyclerViewViewDetail,
        imageUris = imageUris
    )
    imageAdapterSetup.setupAdapter()
}

companion object {
    @JvmStatic
    fun newInstance() =
        TrophyAddImagesFragment().apply {
            arguments = Bundle().apply {

            }
        }
}
}