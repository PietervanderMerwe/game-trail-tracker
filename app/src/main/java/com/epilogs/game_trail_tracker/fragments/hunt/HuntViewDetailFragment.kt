package com.epilogs.game_trail_tracker.fragments.hunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.TrophyViewAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.databinding.FragmentHuntViewDetailBinding
import com.epilogs.game_trail_tracker.interfaces.OnTrophyItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class HuntViewDetailFragment : Fragment(), OnTrophyItemClickListener {

    private var huntId: Int? = null
    private val huntViewModel: HuntViewModel by viewModels()
    private val animalViewModel: AnimalViewModel by viewModels()
    private lateinit var binding: FragmentHuntViewDetailBinding
    private lateinit var adapter: TrophyViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            huntId = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hunt_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHuntViewDetailBinding.bind(view)

        getHunt()
        getTrophies()
        setButton()
    }

    private fun getHunt() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        huntViewModel.getHuntById(huntId!!).observe(viewLifecycleOwner) { hunt ->
            binding.locationName.text = hunt?.name
            binding.dateRange.text = (hunt?.startDate?.let { dateFormat.format(it) } ?: "N/A") + " - " + (hunt?.endDate?.let { dateFormat.format(it) } ?: "N/A")
        }
    }

    private fun getTrophies() {
        with(binding.huntTrophyViewList) {
            layoutManager = LinearLayoutManager(context)
            adapter = TrophyViewAdapter(emptyList(), this@HuntViewDetailFragment).also {
                this@HuntViewDetailFragment.adapter = it
            }
        }
        animalViewModel.getAnimalsByHuntId(huntId!!).observe(viewLifecycleOwner) { animals ->
            adapter.updateAnimals(animals)
        }

        val hasData = adapter.itemCount > 0
        binding.fbAddTrophyButton.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addTrophyButton.visibility = if (hasData) View.GONE else View.VISIBLE
    }

    private fun setButton() {
        binding.addTrophyButton.setOnClickListener {
            val action =
                HuntViewDetailFragmentDirections.actionHuntViewDetailFragmentToTrophyAddFragment(huntId!!, 0, 0)
            findNavController().navigate(action)
        }
        binding.fbAddTrophyButton.setOnClickListener {
            val action =
                HuntViewDetailFragmentDirections.actionHuntViewDetailFragmentToTrophyAddFragment(huntId!!, 0, 0)
            findNavController().navigate(action)
        }
    }
    override fun onTrophyItemClick(animal: Animal) {
        val action = HuntViewDetailFragmentDirections.actionHuntViewDetailFragmentToTrophyViewDetailFragment(animal.id!!)
        findNavController().navigate(action)
    }

    companion object {
        @JvmStatic
        fun newInstance(locationId: Int) =
            HuntViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("locationId", locationId)
                }
            }
    }
}