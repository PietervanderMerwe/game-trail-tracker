package com.epilogs.game_trail_tracker.fragments.weapon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epilogs.game_trail_tracker.R

class WeaponBulletLoadListFragment : Fragment() {
    private var weaponId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weaponId = arguments?.getInt("weaponId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weapon_bullet_load_list, container, false)
    }

    companion object {
        fun newInstance(weaponId: Int?): WeaponBulletLoadListFragment {
            val fragment = WeaponBulletLoadListFragment()
            val args = Bundle()
            args.putInt("weaponId", weaponId!!)
            fragment.arguments = args
            return fragment
        }
    }
}