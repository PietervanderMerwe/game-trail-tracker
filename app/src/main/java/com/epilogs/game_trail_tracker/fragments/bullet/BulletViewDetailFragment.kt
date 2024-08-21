package com.epilogs.game_trail_tracker.fragments.bullet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Bullet
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
            if(bullet?.type == "Hand") {
                handLoad(bullet)
            } else {
                selfLoad(bullet!!)
            }
        }
    }

    private fun handLoad(bullet : Bullet) {
        showHandLoaded()
        binding.textViewBrandOrManViewDetail.setText(bullet.bulletBrand)
        binding.textViewTypeViewDetail.setText(bullet.type)
    }

    private fun selfLoad(bullet : Bullet) {
        hideHandLoaded()
    }

    private fun showHandLoaded() {
        binding.CaseBrandLinearLayout.visibility = View.VISIBLE
        binding.PowderBrandLinearLayout.visibility = View.VISIBLE
        binding.PowderWeightLinearLayout.visibility = View.VISIBLE
    }

    private fun hideHandLoaded() {
        binding.CaseBrandLinearLayout.visibility = View.GONE
        binding.PowderBrandLinearLayout.visibility = View.GONE
        binding.PowderWeightLinearLayout.visibility = View.GONE
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