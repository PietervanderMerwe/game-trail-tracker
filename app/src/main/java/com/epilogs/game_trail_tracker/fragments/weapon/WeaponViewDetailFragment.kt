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
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.FullScreenImageActivity
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel
import com.google.android.material.textfield.TextInputLayout

class WeaponViewDetailFragment : Fragment() {
    private var weaponId: Int? = null
    private val viewModel: WeaponViewModel by viewModels()
    private lateinit var imageAdapter: ImagesAdapter
    private var currentWeapon: Weapon? = null
    private lateinit var nameLayout: TextInputLayout
    private lateinit var noteLayout: TextInputLayout
    private lateinit var deleteButton: Button
    private lateinit var editButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
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
        nameLayout = view.findViewById(R.id.textInputLayoutWeaponNameViewDetail)
        noteLayout = view.findViewById(R.id.textInputLayoutWeaponNotesViewDetail)
        val name: EditText = view.findViewById(R.id.editTextWeaponNameViewDetail)
        val notes: EditText = view.findViewById(R.id.editTextWeaponNotesViewDetail)
        val imagesRecyclerView =
            view.findViewById<RecyclerView>(R.id.imagesWeaponRecyclerViewViewDetail)
        deleteButton = view.findViewById(R.id.button_delete_weapon)
        editButton = view.findViewById(R.id.button_edit_weapon)
        saveButton = view.findViewById(R.id.button_save_weapon)
        cancelButton = view.findViewById(R.id.button_cancel_weapon)
        val backButton: ImageView = view.findViewById(R.id.backButtonWeaponViewDetail)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        disableAllText()

        editButton.setOnClickListener {
            enableAllText()
        }

        viewModel.getWeaponById(weaponId!!).observe(viewLifecycleOwner, Observer { weapon ->
            currentWeapon = weapon
            name.setText(weapon?.name)
            notes.setText(weapon?.notes)

            imageAdapter = ImagesAdapter(weapon?.imagePaths?.toMutableList() ?: mutableListOf()) { imageUrl, position ->
                val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                    putStringArrayListExtra("image_urls", ArrayList(weapon?.imagePaths))
                    putExtra("image_position", position)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                }
                context?.startActivity(intent)
            }

            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        })

        saveButton.setOnClickListener {
            currentWeapon?.let { weapon ->

                weapon.name = name.text.toString()
                weapon.notes = notes.text.toString()

                viewModel.updateWeapon(weapon)
            }

            disableAllText()
        }

        cancelButton.setOnClickListener {
            disableAllText()
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun disableAllText() {
        disableEditText(nameLayout)
        disableEditText(noteLayout)

        editButton.visibility = View.VISIBLE
        deleteButton.visibility = View.VISIBLE
        saveButton.visibility = View.GONE
        cancelButton.visibility = View.GONE
    }

    private fun enableAllText() {
        enableEditText(nameLayout)
        enableEditText(noteLayout)

        editButton.visibility = View.GONE
        deleteButton.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
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