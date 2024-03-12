package com.epilogs.game_trail_tracker.fragments.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.ViewPagerViewAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A simple [Fragment] subclass.
 * Use the [ViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager_view)
        val adapter = ViewPagerViewAdapter(this)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = view.findViewById(R.id.top_view_tabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewFragment().apply {
            }
    }
}