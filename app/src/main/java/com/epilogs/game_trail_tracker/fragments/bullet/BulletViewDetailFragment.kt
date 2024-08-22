package com.epilogs.game_trail_tracker.fragments.bullet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Bullet
import com.epilogs.game_trail_tracker.databinding.FragmentBulletViewDetailBinding
import com.epilogs.game_trail_tracker.viewmodels.BulletViewModel

class BulletViewDetailFragment : Fragment() {

    private var bulletId: Int? = null
    private lateinit var binding: FragmentBulletViewDetailBinding
    private val viewModel: BulletViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter

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
            binding.textViewTypeViewDetail.setText(bullet.type)
            binding.textViewBulletWeightViewDetail.setText(bullet.bulletWeight.toString())
            binding.textViewBulletTypeViewDetail.setText(bullet.bulletType)
            binding.textViewBulletNotesViewDetail.setText(bullet.notes)

            if(bullet.imagePaths?.isEmpty() == true) {
                binding.imagesBulletRecyclerViewViewDetail.visibility = View.GONE
            } else {
                binding.imagesBulletRecyclerViewViewDetail.visibility = View.VISIBLE

                val imagePaths = bullet.imagePaths?.toMutableList() ?: mutableListOf()
                imageAdapter = ImagesAdapter(imagePaths) { imageUrl, position ->
                    val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                        putStringArrayListExtra("image_urls", ArrayList(bullet.imagePaths))
                        putExtra("image_position", position)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    }
                    context?.startActivity(intent)
                }

                binding.imagesBulletRecyclerViewViewDetail.adapter = imageAdapter
                binding.imagesBulletRecyclerViewViewDetail.layoutManager =
                    GridLayoutManager(requireContext(), 3)
            }

            binding.fabEditTrophy.setOnClickListener {
                val action =
                    BulletViewDetailFragmentDirections.actionBulletViewDetailFragmentToBulletAddFragment(
                        bullet.weaponId!!,
                        bulletId!!
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun handLoad(bullet : Bullet) {
        binding.textViewBrandOrManViewDetail.setText(bullet.bulletBrand)
        binding.textViewBulletCaseBrandViewDetail.setText(bullet.caseBrand)
        binding.textViewBulletPowderBrandViewDetail.setText(bullet.powderName)
        binding.textViewBulletPowderWeightViewDetail.setText(bullet.powderWeight.toString())
    }

    private fun selfLoad(bullet : Bullet) {
        hideHandLoaded()
        binding.textViewBrandOrManViewDetail.setText(bullet.manufacturer)
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