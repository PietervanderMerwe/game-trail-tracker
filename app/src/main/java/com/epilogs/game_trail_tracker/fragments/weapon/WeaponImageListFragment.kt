package com.epilogs.game_trail_tracker.fragments.weapon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponImageListBinding
import com.epilogs.game_trail_tracker.utils.ImageAdapterSetup
import com.epilogs.game_trail_tracker.utils.ImagePickerSetup
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel

class WeaponImageListFragment : Fragment() {
    private var weaponId: Int? = null
    private val viewModel: WeaponViewModel by viewModels()
    private lateinit var binding: FragmentWeaponImageListBinding
    private val selectedImageUris = mutableSetOf<String>()
    private lateinit var imagePickerSetup: ImagePickerSetup
    private lateinit var imageAdapterSetup: ImageAdapterSetup
    private lateinit var currentWeapon: Weapon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weaponId = arguments?.getInt("weaponId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weapon_image_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWeaponImageListBinding.bind(view)

        getWeaponImages()
        setImagePicker()
    }
    private fun getWeaponImages() {
        viewModel.getWeaponById(weaponId!!).observe(viewLifecycleOwner, Observer { weapon ->

            currentWeapon = weapon!!

            if (weapon.imagePaths?.isEmpty() == true) {
                setupImageAdapter(selectedImageUris)
                hideRecyclerView()
            } else {
                selectedImageUris.addAll(weapon.imagePaths?.toMutableSet()!!)
                setupImageAdapter(weapon.imagePaths?.toMutableSet() ?: mutableSetOf())
                setupRecyclerView()
            }
        })
    }

    private fun setupRecyclerView() {
        binding.imagesWeaponRecyclerViewViewDetail.visibility = View.VISIBLE
        binding.imagesWeaponRecyclerViewViewDetail.layoutManager =
            GridLayoutManager(requireContext(), 3)
        binding.addWeaponImageButtonFloat.visibility = View.VISIBLE
        binding.addWeaponImageButton.visibility = View.GONE
    }

    private fun hideRecyclerView() {
        binding.imagesWeaponRecyclerViewViewDetail.visibility = View.GONE
        binding.addWeaponImageButton.visibility = View.VISIBLE
        binding.addWeaponImageButtonFloat.visibility = View.GONE
    }

    private fun setImagePicker() {
        imagePickerSetup = ImagePickerSetup(
            fragment = this,
            maxImages = 5,
            onImagesSelected = { images ->
                selectedImageUris.clear()
                selectedImageUris.addAll(images)
                imageAdapterSetup.updateImages(selectedImageUris.toMutableList())
                currentWeapon.imagePaths = selectedImageUris.toMutableList()
                saveImages()
            }
        )

        binding.addWeaponImageButtonFloat.setOnClickListener()
        {
            imagePickerSetup.pickImages(selectedImageUris.toMutableList())
        }
        binding.addWeaponImageButton.setOnClickListener()
        {
            imagePickerSetup.pickImages(selectedImageUris.toMutableList())
        }
    }

    private fun saveImages() {
        viewModel.updateWeapon(currentWeapon)

        if(currentWeapon.imagePaths?.isEmpty() == true) {
            hideRecyclerView()
        } else {
            setupRecyclerView()
        }
    }

    private fun setupImageAdapter(imageUris: MutableSet<String>) {
        imageAdapterSetup = ImageAdapterSetup(
            recyclerView = binding.imagesWeaponRecyclerViewViewDetail,
            imageUris = imageUris
        )
        imageAdapterSetup.setupAdapter()
    }

    companion object {
        fun newInstance(weaponId: Int?): WeaponImageListFragment {
            val fragment = WeaponImageListFragment()
            val args = Bundle()
            args.putInt("weaponId", weaponId!!)
            fragment.arguments = args
            return fragment
        }
    }
}