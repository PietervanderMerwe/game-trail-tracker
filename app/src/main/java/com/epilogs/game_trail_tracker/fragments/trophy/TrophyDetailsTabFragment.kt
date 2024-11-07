package com.epilogs.game_trail_tracker.fragments.trophy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Trophy
import com.epilogs.game_trail_tracker.databinding.FragmentTrophyDetailsTabBinding
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import com.epilogs.game_trail_tracker.viewmodels.TrophyMeasurementViewModel
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Locale

class TrophyDetailsTabFragment : Fragment() {

    private var trophyId: Int? = null
    private val animalViewModel: AnimalViewModel by viewModels()
    private val weaponViewModel: WeaponViewModel by viewModels()
    private val huntViewModel: HuntViewModel by viewModels()
    private val trophyMeasurementViewModel: TrophyMeasurementViewModel by viewModels()
    private var currentTrophy: Trophy? = null
    private var huntId: Int? = 0;
    private var weaponId: Int? = 0;
    private lateinit var binding: FragmentTrophyDetailsTabBinding
    private lateinit var measurementLayouts: List<LinearLayout>
    private lateinit var measurementDescriptions: List<TextView>
    private lateinit var measurementValues: List<TextView>
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
        return inflater.inflate(R.layout.fragment_trophy_details_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrophyDetailsTabBinding.bind(view)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        setBindings()

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
                TrophyViewDetailFragmentDirections.actionTrophyViewDetailFragmentToTrophyAddBasicDetailsFragment(
                    "TrophyDetailFragment",
                    huntId!!,
                    trophyId!!,
                    weaponId!!
                )
            findNavController().navigate(action)
        }

        animalViewModel.getAnimalById(trophyId!!).observe(viewLifecycleOwner) { animal ->
            currentTrophy = animal
            binding.textViewSpecieNameViewDetail.text = animal?.name
            binding.textViewDateViewDetail.text =
                animal?.harvestDate?.let { dateFormat.format(it) } ?: "N/A"
            val weight = animal?.weight?.toString() ?: ""
            binding.textViewWeightViewDetail.text = weight

            trophyMeasurementViewModel.getTrophyMeasurementsByTrophyId(animal?.id!!).observe(viewLifecycleOwner) { trophyMeasurements ->
                for (i in measurementLayouts.indices) {
                    if (i < trophyMeasurements.size) {
                        measurementLayouts[i].visibility = View.VISIBLE
                        measurementDescriptions[i].text = trophyMeasurements[i].name
                        measurementValues[i].text = trophyMeasurements[i].measurement.toString()
                    } else {
                        measurementLayouts[i].visibility = View.GONE
                    }
                }
            }

            if (animal.weaponId != null) {
                binding.WeaponLayoutViewDetail.visibility = View.VISIBLE
                binding.textViewWeaponTitleViewDetail.visibility = View.VISIBLE
                weaponViewModel.getWeaponById(animal.weaponId!!)
                    .observe(viewLifecycleOwner) { weapon ->
                        binding.textViewWeaponViewDetail.text = weapon?.name
                    }
                weaponId = animal.weaponId
            } else {
                binding.textViewWeaponTitleViewDetail.visibility = View.GONE
                binding.WeaponLayoutViewDetail.visibility = View.GONE
            }

            if (animal.huntId != null) {
                binding.textViewLocationTitleViewDetail.visibility = View.VISIBLE
                huntViewModel.getHuntById(animal.huntId!!)
                    .observe(viewLifecycleOwner) { hunt ->
                        binding.textViewLocationViewDetail.text = hunt?.name
                    }
                huntId = animal.huntId
            } else {
                binding.textViewLocationTitleViewDetail.visibility = View.GONE
                binding.textViewLocationViewDetail.visibility = View.GONE
            }
        }
    }

    private fun setBindings() {
        measurementLayouts = listOf(
            binding.linearLayoutMeasurementOne,
            binding.linearLayoutMeasurementTwo,
            binding.linearLayoutMeasurementThree,
            binding.linearLayoutMeasurementFour,
            binding.linearLayoutMeasurementFive,
            binding.linearLayoutMeasurementSix
        );
        measurementDescriptions = listOf(
            binding.textViewMeasurementOne,
            binding.textViewMeasurementTwo,
            binding.textViewMeasurementThree,
            binding.textViewMeasurementFour,
            binding.textViewMeasurementFive,
            binding.textViewMeasurementSix
        );
        measurementValues = listOf(
            binding.textViewMeasurementViewDetailOne,
            binding.textViewMeasurementViewDetailTwo,
            binding.textViewMeasurementViewDetailThree,
            binding.textViewMeasurementViewDetailFour,
            binding.textViewMeasurementViewDetailFive,
            binding.textViewMeasurementViewDetailSix
        );
    }

    companion object {
        fun newInstance(trophyId: Int?): TrophyDetailsTabFragment {
            val fragment = TrophyDetailsTabFragment()
            val args = Bundle()
            args.putInt("trophyId", trophyId!!)
            fragment.arguments = args
            return fragment
        }
    }
}