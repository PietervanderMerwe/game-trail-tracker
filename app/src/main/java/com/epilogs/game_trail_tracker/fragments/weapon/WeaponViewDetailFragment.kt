package com.epilogs.game_trail_tracker.fragments.weapon

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponViewDetailBinding
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import com.google.android.material.textfield.TextInputLayout

class WeaponViewDetailFragment : Fragment() {
    private var weaponId: Int? = null
    private val viewModel: WeaponViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentWeapon: Weapon? = null
    private lateinit var binding: FragmentWeaponViewDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weaponId = it.getInt("id")
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
        binding = FragmentWeaponViewDetailBinding.bind(view)

        disableAllText()

        binding.buttonEditWeapon.setOnClickListener {
            enableAllText()
        }

        getWeaponById()

        binding.buttonSaveWeapon.setOnClickListener {
            currentWeapon?.let { weapon ->

                viewModel.updateWeapon(weapon)
            }
            disableAllText()
        }

        binding.buttonCancelWeapon.setOnClickListener {
            disableAllText()
        }

        binding.buttonDeleteWeapon.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun getWeaponById() {
        viewModel.getWeaponById(weaponId!!).observe(viewLifecycleOwner, Observer { weapon ->
            currentWeapon = weapon
            binding.textViewWeaponNameViewDetail.text = weapon?.name
            binding.textViewWeaponNotesViewDetail.text = weapon?.notes

            imageAdapter = ImagesAdapter(weapon?.imagePaths?.toMutableList() ?: mutableListOf()) { imageUrl, position ->
                val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                    putStringArrayListExtra("image_urls", ArrayList(weapon?.imagePaths))
                    putExtra("image_position", position)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                }
                context?.startActivity(intent)
            }

            binding.imagesWeaponRecyclerViewViewDetail.adapter = imageAdapter
            binding.imagesWeaponRecyclerViewViewDetail.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        })
    }
    private fun disableAllText() {
        binding.buttonEditWeapon.visibility = View.VISIBLE
        binding.buttonDeleteWeapon.visibility = View.VISIBLE
        binding.buttonSaveWeapon.visibility = View.GONE
        binding.buttonCancelWeapon.visibility = View.GONE
        binding.textViewWeaponNameViewDetail.visibility = View.VISIBLE
        binding.textViewWeaponNotesViewDetail.visibility = View.VISIBLE
    }

    private fun enableAllText() {
        binding.buttonEditWeapon.visibility = View.GONE
        binding.buttonDeleteWeapon.visibility = View.GONE
        binding.buttonSaveWeapon.visibility = View.VISIBLE
        binding.buttonCancelWeapon.visibility = View.VISIBLE
        binding.textViewWeaponNameViewDetail.visibility = View.GONE
        binding.textViewWeaponNotesViewDetail.visibility = View.GONE
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this weapon?")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteWeapon(currentWeapon!!)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun disableEditText(textInputLayout: TextInputLayout) {
        textInputLayout.isEnabled = false
        textInputLayout.editText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black_disabled))
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_black_disabled))
        textInputLayout.editText?.apply {
            isClickable = false
            isFocusable = false
            isCursorVisible = false
        }
    }

    private fun enableEditText(textInputLayout: TextInputLayout) {
        textInputLayout.isEnabled = true
        textInputLayout.editText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_black))
        textInputLayout.editText?.apply {
            isClickable = true
            isFocusableInTouchMode = true
            isCursorVisible = true
        }
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