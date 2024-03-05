package com.epilogs.game_trail_tracker

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epilogs.game_trail_tracker.adapters.ImagesAdapter
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnimalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnimalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_animal, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextDate: EditText = view.findViewById(R.id.editTextDate)
        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date and set it as the EditText content
                val selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                editTextDate.setText(selectedDate)
            }, year, month, dayOfMonth).show()
        }

        val imagesRecyclerView: RecyclerView = view.findViewById(R.id.imagesRecyclerView)
        imagesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Example image URIs, replace with your actual image URIs
        val imageUris = listOf(
            Uri.parse("android.resource://your.package.name/drawable/image1"),
            Uri.parse("android.resource://your.package.name/drawable/image2")
        )

        imagesRecyclerView.adapter = ImagesAdapter(imageUris)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        const val ARG_PARAM1 = "param1"
        const val ARG_PARAM2 = "param2"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnimalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}