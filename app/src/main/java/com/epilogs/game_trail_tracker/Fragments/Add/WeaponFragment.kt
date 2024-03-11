package com.epilogs.game_trail_tracker.Fragments.Add

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.database.entities.Weapon
import com.epilogs.game_trail_tracker.viewmodels.WeaponViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [WeaponFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeaponFragment : Fragment() {
    private val viewModel: WeaponViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weapon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextWeaponName: EditText = view.findViewById(R.id.editTextWeaponName)
        val editTextWeaponNotes : EditText = view.findViewById(R.id.editTextWeaponNotes)
        val buttonSaveWeapon: Button = view.findViewById(R.id.buttonSaveWeapon)

        buttonSaveWeapon.setOnClickListener {
            val name = editTextWeaponName.text.toString()
            val notes = editTextWeaponNotes.text.toString()
            val weapon = Weapon(name = name, notes = notes, imagePaths = null)

            viewModel.insertWeapon(weapon)
        }

        viewModel.getInsertionSuccess().observe(viewLifecycleOwner, Observer { success ->
            if (success == true) {
                editTextWeaponName.text.clear()
                editTextWeaponNotes.text.clear()
                viewModel.resetInsertionSuccess()

                val checkMarkImageView: ImageView = view.findViewById(R.id.checkMarkWeaponAdd)
                checkMarkImageView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    checkMarkImageView.visibility = View.GONE
                }, 3000)
            }
        })
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WeaponFragment().apply {
            }
    }
}