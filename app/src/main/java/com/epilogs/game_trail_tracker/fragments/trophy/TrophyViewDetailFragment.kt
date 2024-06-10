package com.epilogs.game_trail_tracker.fragments.trophy

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyViewDetailBinding
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class TrophyViewDetailFragment : Fragment() {

    private var trophyId: Int? = null
    private val animalViewModel: AnimalViewModel by viewModels()
    private val weaponViewModel: WeaponViewModel by viewModels()
    private val huntViewModel: HuntViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentAnimal: Animal? = null
    private var huntId: Int? = 0;
    private var weaponId: Int? = 0;
    private lateinit var binding: FragmentTrophyViewDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trophyId = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trophy_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrophyViewDetailBinding.bind(view)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.textInputLayoutLocationViewDetail.setOnClickListener {
            val action =
                TrophyViewDetailFragmentDirections.actionTrophyViewDetailFragmentToHuntViewDetailFragment(
                    huntId!!
                )
            findNavController().navigate(action)
        }

        binding.textInputLayoutWeaponViewDetail.setOnClickListener {
            val action =
                TrophyViewDetailFragmentDirections.actionTrophyViewDetailFragmentToWeaponViewDetailFragment(
                    weaponId!!
                )
            findNavController().navigate(action)
        }

        binding.fabEditTrophy.setOnClickListener {
            val action =
                TrophyViewDetailFragmentDirections.actionTrophyViewDetailFragmentToTrophyAddFragment(
                    huntId!!,
                    trophyId!!,
                    weaponId!!
                )
            findNavController().navigate(action)
        }

        animalViewModel.getAnimalById(trophyId!!).observe(viewLifecycleOwner, Observer { animal ->
            currentAnimal = animal
            binding.textViewSpecieNameViewDetail.text = animal?.name
            binding.textViewDateViewDetail.text =
                animal?.harvestDate?.let { dateFormat.format(it) } ?: "N/A"
            val weight = animal?.weight?.toString() ?: ""
            val weightType = animal?.weightType ?: ""
            binding.textViewWeightViewDetail.text = getString(R.string.weight_detail, weight, weightType)

            val measurement = animal?.measurement?.toString() ?: ""
            val measurementType = animal?.measurementType ?: ""
            binding.textViewMeasurementViewDetail.text = getString(R.string.measurement_detail, measurement, measurementType)

            if(animal?.imagePaths?.isEmpty() == true)
            {
                binding.imagesAnimalRecyclerViewViewDetail.visibility = View.GONE
            }
            else
            {
                binding.imagesAnimalRecyclerViewViewDetail.visibility = View.VISIBLE

                val imagePaths = animal?.imagePaths?.toMutableList() ?: mutableListOf()
                imageAdapter = ImagesAdapter(imagePaths) { imageUrl, position ->
                    val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                        putStringArrayListExtra("image_urls", ArrayList(animal?.imagePaths))
                        putExtra("image_position", position)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    }
                    context?.startActivity(intent)
                }

                binding.imagesAnimalRecyclerViewViewDetail.adapter = imageAdapter
                binding.imagesAnimalRecyclerViewViewDetail.layoutManager =
                    GridLayoutManager(requireContext(), 3)
            }

            if (animal?.weaponId != null) {
                binding.WeaponLayoutViewDetail.visibility = View.VISIBLE
                binding.textViewWeaponTitleViewDetail.visibility = View.VISIBLE
                weaponViewModel.getWeaponById(animal?.weaponId!!)
                    .observe(viewLifecycleOwner, Observer { weapon ->
                        binding.textViewWeaponViewDetail.text = weapon?.name
                    })
                weaponId = animal.weaponId
            } else {
                binding.textViewWeaponTitleViewDetail.visibility = View.GONE
                binding.WeaponLayoutViewDetail.visibility = View.GONE
            }

            if (animal?.huntId != null) {
                binding.textViewLocationTitleViewDetail.visibility = View.VISIBLE
                huntViewModel.getHuntById(animal.huntId!!)
                    .observe(viewLifecycleOwner, Observer { hunt ->
                        binding.textViewLocationViewDetail.text = hunt?.name
                    })
                huntId = animal.huntId
            } else {
                binding.textViewLocationTitleViewDetail.visibility = View.GONE
            }
        })
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this animal?")
            .setPositiveButton("Delete") { dialog, which ->
                animalViewModel.deleteAnimal(currentAnimal!!)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        @JvmStatic
        fun newInstance(animalId: Int) =
            TrophyViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("animalId", animalId)
                }
            }
    }
}