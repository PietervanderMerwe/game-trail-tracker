package com.epilogs.game_trail_tracker.fragments.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel

class WeaponViewDetailFragment : Fragment() {
    private var weaponId: Int? = null
    private val viewModel: WeaponViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter

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
        return inflater.inflate(R.layout.fragment_weapon_view_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name: TextView = view.findViewById(R.id.editTextWeaponNameViewDetail)
        val notes : TextView = view.findViewById(R.id.editTextWeaponNotesViewDetail)
        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesWeaponRecyclerViewViewDetail)

        viewModel.getWeaponById(weaponId!!).observe(viewLifecycleOwner, Observer { weapon ->
            name.text = weapon?.name
            notes.text = weapon?.notes

            imageAdapter = ImagesAdapter(mutableListOf())
            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            weapon?.imagePaths?.let { imageUrls ->
                imageAdapter.updateImages(imageUrls)
            }
        })
    }
    companion object {
        @JvmStatic
        fun newInstance(weaponId: Int) =
            WeaponViewDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("weaponId", weaponId)
                }
            }
    }
}