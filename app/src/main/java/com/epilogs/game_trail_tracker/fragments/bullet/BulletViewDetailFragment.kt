package com.epilogs.game_trail_tracker.fragments.bullet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.databinding.FragmentBulletViewDetailBinding
import com.epilogs.game_trail_tracker.viewmodels.BulletViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class BulletViewDetailFragment : Fragment() {

    private var bulletId: Int? = null
    private lateinit var binding: FragmentBulletViewDetailBinding
    private val viewModel: BulletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bulletId = it.getInt("bulletId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bullet_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBulletViewDetailBinding.bind(view)

        getBullet()
    }

    private fun getBullet() {
        viewModel.getBulletById(bulletId!!).observe(viewLifecycleOwner) { bullet ->
            binding.locationName.text = hunt?.name
            val startDateStr = hunt?.startDate?.let { dateFormat.format(it) } ?: "N/A"
            val endDateStr = hunt?.endDate?.let { dateFormat.format(it) } ?: "N/A"
            val dateRange = getString(R.string.date_range, startDateStr, endDateStr)
            binding.dateRange.text = dateRange
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BulletViewDetailFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}