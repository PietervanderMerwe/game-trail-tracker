package com.epilogs.game_trail_tracker.fragments.hunt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.TrophyViewAdapter
import com.epilogs.game_trail_tracker.database.entities.Animal
import com.epilogs.game_trail_tracker.databinding.FragmentHuntTrophyListBinding
import com.epilogs.game_trail_tracker.interfaces.OnTrophyItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.AnimalViewModel

class HuntTrophyListFragment : Fragment(), OnTrophyItemClickListener {
    private var huntId: Int? = null
    private val animalViewModel: AnimalViewModel by activityViewModels()
    private lateinit var binding: FragmentHuntTrophyListBinding
    private lateinit var adapter: TrophyViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        huntId = arguments?.getInt("huntId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hunt_trophy_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHuntTrophyListBinding.bind(view)

        getTrophies()
        setButton()
        setupInsertAndUpdateCheck()
    }

    private fun getTrophies() {
        with(binding.huntTrophyViewList) {
            layoutManager = LinearLayoutManager(context)
            adapter = TrophyViewAdapter(emptyList(), this@HuntTrophyListFragment).also {
                this@HuntTrophyListFragment.adapter = it
            }
        }
        animalViewModel.getAnimalsByHuntId(huntId!!).observe(viewLifecycleOwner) { animals ->
            adapter.updateAnimals(animals)
            checkDataAndUpdateUI()
        }
    }

    private fun checkDataAndUpdateUI() {
        val hasData = adapter.itemCount > 0

        binding.fbAddTrophyButton.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addTrophyButton.visibility = if (hasData) View.GONE else View.VISIBLE
    }

    private fun setButton() {
        binding.addTrophyButton.setOnClickListener {
            navigateToAdd()
        }
        binding.fbAddTrophyButton.setOnClickListener {
            navigateToAdd()
        }
    }

    private fun navigateToAdd()  {
        val action =
            HuntViewDetailFragmentDirections.actionHuntViewDetailFragmentToTrophyAddFragment(
                "huntFragment",
                huntId!!,
                0,
                0
            )
        findNavController().navigate(action)
    }

    override fun onTrophyItemClick(animal: Animal) {
        val action =
            HuntTrophyListFragmentDirections.actionHuntTrophyListFragmentToHuntAddFragment(
                animal.id!!
            )
        findNavController().navigate(action)
    }

    private fun setupInsertAndUpdateCheck() {
        animalViewModel.getInsertionSuccess().observe(viewLifecycleOwner) {
            getTrophies()
        }
        animalViewModel.getUpdateSuccess().observe(viewLifecycleOwner) {
            getTrophies()
        }
    }
    companion object {
        fun newInstance(huntId: Int?): HuntTrophyListFragment {
            val fragment = HuntTrophyListFragment()
            val args = Bundle()
            args.putInt("huntId", huntId!!)
            fragment.arguments = args
            return fragment
        }
    }
}