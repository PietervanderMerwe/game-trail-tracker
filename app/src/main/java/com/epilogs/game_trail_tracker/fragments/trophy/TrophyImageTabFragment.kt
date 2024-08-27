package com.epilogs.game_trail_tracker.fragments.trophy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyImageTabBinding
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel

class TrophyImageTabFragment : Fragment() {

    private var trophyId: Int? = null
    private lateinit var imageAdapter: ImagesAdapter
    private val viewModel: AnimalViewModel by viewModels()
    private lateinit var binding: FragmentTrophyImageTabBinding
    private lateinit var currentTrophy: Animal
    private val selectedImageUris = mutableSetOf<String>()
    private lateinit var imagePickerSetup: ImagePickerSetup
    private lateinit var imageAdapterSetup: ImageAdapterSetup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trophyId = arguments?.getInt("trophyId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trophy_image_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrophyImageTabBinding.bind(view)

        getTrophyImages()
        setImagePicker()
    }

    private fun getTrophyImages() {
        viewModel.getAnimalById(trophyId!!).observe(viewLifecycleOwner, Observer { animal ->
            currentTrophy = animal!!

            if (animal.imagePaths?.isEmpty() == true) {
                binding.imagesTrophyRecyclerViewViewDetail.visibility = View.GONE
                setupImageAdapter(selectedImageUris)

                binding.addTrophyImageButton.visibility = View.VISIBLE
                binding.addTrophyImageButtonFloat.visibility = View.GONE
            } else {
                binding.imagesTrophyRecyclerViewViewDetail.visibility = View.VISIBLE
                setupImageAdapter(animal.imagePaths?.toMutableSet() ?: mutableSetOf())

                binding.imagesTrophyRecyclerViewViewDetail.layoutManager =
                    GridLayoutManager(requireContext(), 3)
                binding.addTrophyImageButtonFloat.visibility = View.VISIBLE
                binding.addTrophyImageButton.visibility = View.GONE
            }
        })
    }

    private fun setImagePicker() {
        imagePickerSetup = ImagePickerSetup(
            fragment = this,
            maxImages = 5,
            onImagesSelected = { images ->
                selectedImageUris.clear()
                selectedImageUris.addAll(images)
                imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
                currentTrophy.imagePaths = selectedImageUris.toMutableList()
                saveImages()
            }
        )

        binding.addTrophyImageButtonFloat.setOnClickListener()
        {
            imagePickerSetup.pickImages()
        }
        binding.addTrophyImageButton.setOnClickListener()
        {
            imagePickerSetup.pickImages()
        }
    }

    private fun saveImages() {
        viewModel.updateAnimal(currentTrophy)
    }

    private fun setupImageAdapter(imageUris: MutableSet<String>) {
        imageAdapterSetup = ImageAdapterSetup(
            recyclerView = binding.imagesTrophyRecyclerViewViewDetail,
            imageUris = imageUris
        )
        imageAdapterSetup.setupAdapter()
    }

    companion object {
        fun newInstance(trophyId: Int?): TrophyImageTabFragment {
            val fragment = TrophyImageTabFragment()
            val args = Bundle()
            args.putInt("trophyId", trophyId!!)
            fragment.arguments = args
            return fragment
        }
    }
}