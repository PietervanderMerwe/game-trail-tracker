package com.epilogs.game_trail_tracker.fragments.trophy

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.adapters.LocationAdapter
import com.epilogs.game_trail_tracker.adapters.WeaponAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.database.entities.Hunt
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyViewDetailBinding
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponViewDetailBinding
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.utils.showDatePickerDialog
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import com.google.android.material.textfield.TextInputLayout
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

        binding.backButtonAnimalViewDetail.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonViewLocation.setOnClickListener {
//            val action =
//                TrophyViewDetailFragmentDirections.actionAnimalViewDetailFragment2ToLocationViewDetailFragment2(
//                    locationId!!
//                )
//            findNavController().navigate(action)
        }

        binding.buttonViewWeapon.setOnClickListener {
//            val action =
//                TrophyViewDetailFragmentDirections.actionAnimalViewDetailFragment2ToWeaponViewDetailFragment2(
//                    weaponId!!
//                )
//            findNavController().navigate(action)
        }

        binding.buttonEditAnimal.setOnClickListener {
            val action = TrophyViewDetailFragmentDirections.actionTrophyViewDetailFragmentToTrophyAddFragment(trophyId!!)
            findNavController().navigate(action)
        }

        animalViewModel.getAnimalById(trophyId!!).observe(viewLifecycleOwner, Observer { animal ->
            currentAnimal = animal
            binding.textViewSpecieNameViewDetail.text = animal?.name
            binding.textViewDateViewDetail.text = animal?.harvestDate?.let { dateFormat.format(it) } ?: "N/A"
            binding.textViewWeightViewDetail.text = animal?.weight?.toString()
            binding.textViewMeasurementViewDetail.text = animal?.measurement?.toString()

            imageAdapter = ImagesAdapter(animal?.imagePaths?.toMutableList() ?: mutableListOf()) { imageUrl, position ->
                val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                    putStringArrayListExtra("image_urls", ArrayList(animal?.imagePaths))
                    putExtra("image_position", position)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                }
                context?.startActivity(intent)
            }

            binding.imagesAnimalRecyclerViewViewDetail.adapter = imageAdapter
            binding.imagesAnimalRecyclerViewViewDetail.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            huntId = animal?.huntId!!
            weaponId = animal.huntId!!

            weaponViewModel.getWeaponById(animal.weaponId!!).observe(viewLifecycleOwner, Observer { weapon ->
                binding.textViewWeaponViewDetail.text = weapon?.name
            })

            huntViewModel.getHuntById(animal.huntId!!).observe(viewLifecycleOwner, Observer { hunt ->
                binding.textViewLocationViewDetail.text = hunt?.name
            })
        })


        binding.buttonDeleteAnimal.setOnClickListener {
            showDeleteConfirmationDialog()
        }
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