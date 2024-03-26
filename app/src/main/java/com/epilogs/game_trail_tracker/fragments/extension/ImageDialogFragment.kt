package com.epilogs.game_trail_tracker.fragments.extension

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.epilogs.game_trail_tracker.R

class ImageDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_dialog, container, false)
        val imageView = view.findViewById<ImageView>(R.id.imageViewLarge)
        val imageUri = arguments?.getString("imageUri")

        Glide.with(this).load(imageUri).into(imageView)

        return view
    }

    companion object {
        fun newInstance(imageUri: String): ImageDialogFragment {
            val args = Bundle()
            args.putString("imageUri", imageUri)
            val fragment = ImageDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}