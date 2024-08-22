package com.epilogs.game_trail_tracker.fragments.hunt

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.databinding.FragmentHuntImageListBinding
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class HuntImageListFragment : Fragment() {

    private var huntId: Int? = null
    private val huntViewModel: HuntViewModel by viewModels()
    private lateinit var binding: FragmentHuntImageListBinding
    private val selectedImageUris = mutableSetOf<String>()
    private lateinit var imagePickerSetup: ImagePickerSetup
    private lateinit var imageAdapterSetup: ImageAdapterSetup
    private lateinit var currentHunt: Hunt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        huntId = arguments?.getInt("huntId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHuntImageListBinding.bind(view)

        getHuntImages()
        setImagePicker()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hunt_image_list, container, false)
    }

    private fun getHuntImages() {
        huntViewModel.getHuntById(huntId!!).observe(viewLifecycleOwner) { hunt ->

            currentHunt = hunt!!

            if (hunt.imagePaths?.isEmpty() == true) {
                binding.imagesHuntRecyclerViewViewDetail.visibility = View.GONE
                setupImageAdapter(selectedImageUris)

                binding.addHuntImageButton.visibility = View.VISIBLE
                binding.addHuntImageButtonFloat.visibility = View.GONE
            } else {
                binding.imagesHuntRecyclerViewViewDetail.visibility = View.VISIBLE
                setupImageAdapter(hunt.imagePaths?.toMutableSet() ?: mutableSetOf())

                binding.imagesHuntRecyclerViewViewDetail.layoutManager =
                    GridLayoutManager(requireContext(), 3)
                binding.addHuntImageButtonFloat.visibility = View.VISIBLE
                binding.addHuntImageButton.visibility = View.GONE
            }
        }
    }

    private fun setImagePicker() {
        imagePickerSetup = ImagePickerSetup(
            fragment = this,
            maxImages = 5,
            onImagesSelected = { images ->
                selectedImageUris.clear()
                selectedImageUris.addAll(images)
                imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
                currentHunt.imagePaths = selectedImageUris.toMutableList()
                saveImages()
            }
        )

        binding.addHuntImageButtonFloat.setOnClickListener()
        {
            imagePickerSetup.pickImages()
        }
        binding.addHuntImageButton.setOnClickListener()
        {
            imagePickerSetup.pickImages()
        }
    }

    private fun saveImages() {
        huntViewModel.updateHunt(currentHunt)
    }
    private fun setupImageAdapter(imageUris: MutableSet<String>) {
        imageAdapterSetup = ImageAdapterSetup(
            recyclerView = binding.imagesHuntRecyclerViewViewDetail,
            imageUris = imageUris
        )
        imageAdapterSetup.setupAdapter()
    }

    companion object {
        fun newInstance(huntId: Int?): HuntImageListFragment {
            val fragment = HuntImageListFragment()
            val args = Bundle()
            args.putInt("huntId", huntId!!)
            fragment.arguments = args
            return fragment
        }
    }
}