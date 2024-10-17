package com.epilogs.game_trail_tracker.fragments.arrow

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
import com.epilogs.game_trail_tracker.databinding.FragmentArrowViewDetailBinding
import com.epilogs.game_trail_tracker.databinding.FragmentBulletViewDetailBinding
import com.epilogs.game_trail_tracker.fragments.bullet.BulletViewDetailFragmentDirections
import com.epilogs.game_trail_tracker.viewmodels.ArrowViewModel
import com.epilogs.game_trail_tracker.viewmodels.BulletViewModel

class ArrowViewDetailFragment : Fragment() {

    private var arrowId: Int? = null
    private lateinit var binding: FragmentArrowViewDetailBinding
    private val viewModel: ArrowViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arrowId = it.getInt("arrowId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_arrow_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentArrowViewDetailBinding.bind(view)

        getArrow()
    }

    private fun getArrow() {
        viewModel.getArrowById(arrowId!!).observe(viewLifecycleOwner) { arrow ->

            binding.textViewBrandOrManViewDetail.setText(arrow?.manufacturer)
            binding.textViewShaftMaterialViewDetail.setText(arrow?.shaftMaterial)
            binding.textViewLengthViewDetail.setText(arrow?.length.toString())
            binding.textViewFletchingTypeViewDetail.setText(arrow?.fletchingType)
            binding.textViewPointTypeViewDetail.setText(arrow?.pointType)
            binding.textViewPointWeightViewDetail.setText(arrow?.pointWeight.toString())
            binding.textViewNockTypeViewDetail.setText(arrow?.nockType)
            binding.textViewNotesViewDetail.setText(arrow?.notes)

            if(arrow?.imagePaths?.isEmpty() == true) {
                binding.imagesArrowRecyclerViewViewDetail.visibility = View.GONE
            } else {
                binding.imagesArrowRecyclerViewViewDetail.visibility = View.VISIBLE

                val imagePaths = arrow?.imagePaths?.toMutableList() ?: mutableListOf()
                imageAdapter = ImagesAdapter(imagePaths) { imageUrl, position ->
                    val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                        putStringArrayListExtra("image_urls", ArrayList(arrow?.imagePaths))
                        putExtra("image_position", position)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    }
                    context?.startActivity(intent)
                }

                binding.imagesArrowRecyclerViewViewDetail.adapter = imageAdapter
                binding.imagesArrowRecyclerViewViewDetail.layoutManager =
                    GridLayoutManager(requireContext(), 3)
            }

            binding.fabEditArrow.setOnClickListener {
                val action =
                    BulletViewDetailFragmentDirections.actionBulletViewDetailFragmentToBulletAddFragment(
                        arrow?.weaponId!!,
                        arrowId!!
                    )
                findNavController().navigate(action)
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            ArrowViewDetailFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}