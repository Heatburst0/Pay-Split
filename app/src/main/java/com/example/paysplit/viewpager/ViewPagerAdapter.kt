package com.example.paysplit.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.paysplit.fragments.HistoryFragment
import com.example.paysplit.fragments.HomeFragment
import com.example.paysplit.fragments.ProfileFragment

class ViewPagerAdapter(fragmentActivity : FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0-> return HomeFragment()
            1-> return ProfileFragment()
            2-> return HistoryFragment()
        }
        return HomeFragment()
    }


}