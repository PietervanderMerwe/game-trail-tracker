package com.epilogs.game_trail_tracker.fragments.trophy

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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
    private var huntId: Int? = 0
    private var weaponId: Int? = 0
    private lateinit var binding: FragmentTrophyDetailsTabBinding
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

            if (!animal?.imagePaths.isNullOrEmpty()) {
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels

                val newHeight = (screenHeight * 0.6).toInt()
                binding.imageTrophyView.layoutParams.height = newHeight
                binding.imageTrophyView.requestLayout()

                animal?.let {
                    Glide.with(this)
                        .load(animal.imagePaths?.first())
                        .into(binding.imageTrophyView)
                }
            }

            trophyMeasurementViewModel.getTrophyMeasurementsByTrophyId(animal?.id!!)
                .observe(viewLifecycleOwner) { trophyMeasurements ->
                    for (i in trophyMeasurements.indices) {
                        binding.parentLinearLayout.addView(
                            createMeasurementLayout(
                                trophyMeasurements[i].name,
                                trophyMeasurements[i].measurement.toString()
                            )
                        )
                    }
                }

            if (animal.weaponId != null) {
                binding.textViewWeaponLayout.visibility = View.VISIBLE
                binding.WeaponLayoutViewDetail.visibility = View.VISIBLE
                binding.textViewWeaponTitleViewDetail.visibility = View.VISIBLE
                weaponViewModel.getWeaponById(animal.weaponId!!)
                    .observe(viewLifecycleOwner) { weapon ->
                        binding.textViewWeaponViewDetail.text = weapon?.name
                    }
                weaponId = animal.weaponId
            } else {
                binding.textViewWeaponLayout.visibility = View.GONE
                binding.textViewWeaponTitleViewDetail.visibility = View.GONE
                binding.WeaponLayoutViewDetail.visibility = View.GONE
            }

            if (animal.huntId != null) {
                binding.textViewLocationLayout.visibility = View.VISIBLE
                binding.textViewLocationTitleViewDetail.visibility = View.VISIBLE
                huntViewModel.getHuntById(animal.huntId!!)
                    .observe(viewLifecycleOwner) { hunt ->
                        binding.textViewLocationViewDetail.text = hunt?.name
                    }
                huntId = animal.huntId
            } else {
                binding.textViewLocationLayout.visibility = View.GONE
                binding.textViewLocationTitleViewDetail.visibility = View.GONE
                binding.textViewLocationViewDetail.visibility = View.GONE
            }
        }
    }

    private fun createMeasurementLayout(name: String, description: String): LinearLayout {
        val linearLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weightSum = 3f
            }
        }

        val textViewMeasurement = TextView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
            )
            text = shortenText(name, 4)
            setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1)
            setPadding(0, 0, 0, 8.dpToPx())
        }

        val textInputLayout = TextInputLayout(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            setPadding(0, 0, 0, 8.dpToPx())
        }

        val textViewMeasurementDetail = TextView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.END
            visibility = View.VISIBLE
            setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1)
            text = description

        }

        textInputLayout.addView(textViewMeasurementDetail)

        linearLayout.addView(textViewMeasurement)
        linearLayout.addView(textInputLayout)

        return linearLayout
    }

    fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    fun shortenText(text: String, wordLimit: Int): String {
        val words = text.split(" ")
        return if (words.size > wordLimit) {
            words.take(wordLimit).joinToString(" ") + "..."
        } else {
            text
        }
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