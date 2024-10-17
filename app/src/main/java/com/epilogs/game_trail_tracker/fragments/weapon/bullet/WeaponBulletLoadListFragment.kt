package com.epilogs.game_trail_tracker.fragments.weapon.bullet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilogs.game_trail_tracker.R
import com.epilogs.game_trail_tracker.adapters.BulletViewAdapter
import com.epilogs.game_trail_tracker.database.entities.Bullet
import com.epilogs.game_trail_tracker.databinding.FragmentWeaponBulletLoadListBinding
import com.epilogs.game_trail_tracker.fragments.weapon.WeaponViewDetailFragmentDirections
import com.epilogs.game_trail_tracker.interfaces.OnBulletItemClickListener
import com.epilogs.game_trail_tracker.viewmodels.BulletViewModel

class WeaponBulletLoadListFragment : Fragment(R.layout.fragment_weapon_bullet_load_list),
    OnBulletItemClickListener {

    private var weaponId: Int? = null
    private val viewModel: BulletViewModel by viewModels()
    private lateinit var binding: FragmentWeaponBulletLoadListBinding
    private lateinit var adapter: BulletViewAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeaponBulletLoadListBinding.bind(view)

        setupRecyclerView()
        getBullets()
        setButton()
        setupInsertAndUpdateCheck()
        checkDataAndUpdateUI()
    }

    override fun onBulletItemClick(bullet: Bullet) {
        val action = WeaponViewDetailFragmentDirections.actionWeaponViewDetailFragmentToBulletViewDetailFragment(bullet.id!!)
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        with(binding.weaponBulletViewList) {
            layoutManager = LinearLayoutManager(context)
            adapter = BulletViewAdapter(emptyList(), this@WeaponBulletLoadListFragment).also {
                this@WeaponBulletLoadListFragment.adapter = it
            }
        }
    }

    private fun getBullets() {
        viewModel.getBulletsByWeaponId(weaponId!!).observe(viewLifecycleOwner) { bullets ->
            adapter.updateBullets(bullets)
            checkDataAndUpdateUI()
        }
    }

    private fun setButton() {
        binding.addBulletButton.setOnClickListener {
            navigateToAdd()
        }
        binding.addBulletButtonFloat.setOnClickListener {
            navigateToAdd()
        }
    }

    private fun navigateToAdd()  {
        val action =
            WeaponViewDetailFragmentDirections.actionWeaponViewDetailFragmentToBulletAddFragment(
                weaponId!!
            )
        findNavController().navigate(action)
    }

    private fun setupInsertAndUpdateCheck() {
        viewModel.getInsertionSuccess().observe(viewLifecycleOwner) {
            getBullets()
        }
        viewModel.getUpdateSuccess().observe(viewLifecycleOwner) {
            getBullets()
        }
    }

    private fun checkDataAndUpdateUI() {
        val hasData = adapter.itemCount > 0

        binding.addBulletButtonFloat.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.addBulletButton.visibility = if (hasData) View.GONE else View.VISIBLE
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