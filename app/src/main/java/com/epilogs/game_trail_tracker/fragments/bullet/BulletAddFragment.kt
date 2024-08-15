package com.epilogs.game_trail_tracker.fragments.bullet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.databinding.FragmentBulletAddBinding

class BulletAddFragment : Fragment() {

    private var weaponId: Int? = null
    private lateinit var binding: FragmentBulletAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weaponId = it.getInt("weaponId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bullet_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBulletAddBinding.bind(view)

        setupTabToggleBtn()
    }

    private fun setupTabToggleBtn() {
        binding.tabToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.storeBoughtToggle -> {
                        binding.storeBoughtToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_color))
                        binding.storeBoughtToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                        binding.handLoadedToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
                        binding.handLoadedToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black_disabled))

                        setStoreBought()
                    }
                    R.id.handLoadedToggle -> {
                        binding.handLoadedToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_color))
                        binding.handLoadedToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                        binding.storeBoughtToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
                        binding.storeBoughtToggle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black_disabled))

                        setHandLoaded()
                    }
                }
            }
        }
    }

    private fun setStoreBought() {

    }

    private fun setHandLoaded() {

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BulletAddFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}