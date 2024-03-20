package com.epilogs.game_trail_tracker.fragments.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Location
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.utils.DateConverter
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel

class WeaponViewDetailFragment : Fragment() {
    private var weaponId: Int? = null
    private val viewModel: WeaponViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentWeapon: Weapon? = null
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

        val name: EditText = view.findViewById(R.id.editTextWeaponNameViewDetail)
        val notes : EditText = view.findViewById(R.id.editTextWeaponNotesViewDetail)
        val imagesRecyclerView = view.findViewById<RecyclerView>(R.id.imagesWeaponRecyclerViewViewDetail)
        val deleteButton: Button = view.findViewById(R.id.button_delete_weapon)
        val editButton: Button = view.findViewById(R.id.button_edit_weapon)
        val saveButton: Button = view.findViewById(R.id.button_save_weapon)
        val backButton: ImageView = view.findViewById(R.id.backButtonWeaponViewDetail)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        disableEditText(name)
        disableEditText(notes)

        editButton.setOnClickListener {
            enableEditText(name)
            enableEditText(notes)

            editButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
            saveButton.visibility = View.VISIBLE
        }

        viewModel.getWeaponById(weaponId!!).observe(viewLifecycleOwner, Observer { weapon ->
            currentWeapon = weapon
            name.setText(weapon?.name)
            notes.setText(weapon?.notes)

            imageAdapter = ImagesAdapter(mutableListOf())
            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            weapon?.imagePaths?.let { imageUrls ->
                imageAdapter.updateImages(imageUrls)
            }
        })

        saveButton.setOnClickListener {
            currentWeapon?.let { weapon ->

                weapon.name = name.text.toString()
                weapon.notes = notes.text.toString()

                viewModel.updateWeapon(weapon)
            }

            disableEditText(name)
            disableEditText(notes)

            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
            saveButton.visibility = View.GONE
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
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

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isClickable = false
        editText.isCursorVisible = false
        editText.background = null
    }

    private fun enableEditText(editText: EditText) {
        editText.isFocusableInTouchMode = true
        editText.isClickable = true
        editText.isCursorVisible = true
        editText.setBackgroundResource(android.R.drawable.edit_text)
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