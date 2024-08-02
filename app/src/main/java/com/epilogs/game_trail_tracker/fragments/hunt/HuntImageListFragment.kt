package com.epilogs.game_trail_tracker.fragments.hunt

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.databinding.FragmentHuntImageListBinding
import com.epilogs.game_trail_tracker.viewmodels.HuntViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class HuntImageListFragment : Fragment() {

    private var huntId: Int? = null
    private val huntViewModel: HuntViewModel by viewModels()
    private lateinit var binding: FragmentHuntImageListBinding
    private lateinit var imageAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        huntId = arguments?.getInt("huntId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHuntImageListBinding.bind(view)

        getHuntImages()
        checkDataAndUpdateUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hunt_image_list, container, false)
    }

    private fun getHuntImages() {
        huntViewModel.getHuntById(huntId!!).observe(viewLifecycleOwner) { hunt ->
            if (hunt?.imagePaths?.isEmpty() == true) {
                binding.imagesHuntRecyclerViewViewDetail.visibility = View.GONE
            } else {
                binding.imagesHuntRecyclerViewViewDetail.visibility = View.VISIBLE

                val imagePaths = hunt?.imagePaths?.toMutableList() ?: mutableListOf()
                imageAdapter = ImagesAdapter(imagePaths) { imageUrl, position ->
                    val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                        putStringArrayListExtra("image_urls", ArrayList(hunt?.imagePaths))
                        putExtra("image_position", position)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    }
                    context?.startActivity(intent)
                }

                binding.imagesHuntRecyclerViewViewDetail.adapter = imageAdapter
                binding.imagesHuntRecyclerViewViewDetail.layoutManager =
                    GridLayoutManager(requireContext(), 3)
            }
        }
    }

    private fun checkDataAndUpdateUI() {
        val hasData = ::imageAdapter.isInitialized && imageAdapter.itemCount > 0

        binding.addHuntImageButtonFloat.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addHuntImageButton.visibility = if (hasData) View.GONE else View.VISIBLE
    }

    companion object {
        fun newInstance(huntId: Int?): HuntImageListFragment {
            val fragment = HuntImageListFragment()
            val args = Bundle()
            args.putInt("huntId", huntId!!)
            fragment.arguments = args
            return fragment
        }
    }
}