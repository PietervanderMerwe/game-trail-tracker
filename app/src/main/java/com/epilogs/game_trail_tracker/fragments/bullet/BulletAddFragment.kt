package com.epilogs.game_trail_tracker.fragments.bullet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epilogs.game_trail_tracker.R

class BulletAddFragment : Fragment() {

    private var weaponId: Int? = null

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
        return inflater.inflate(R.layout.fragment_bullet_add, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BulletAddFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}