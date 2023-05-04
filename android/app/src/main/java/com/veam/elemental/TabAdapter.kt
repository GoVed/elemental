package com.veam.elemental

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class TabAdapter(activity:AppCompatActivity): FragmentStateAdapter(activity) {



    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> return LeaderboardFragment()
            1 -> return LogFragment()
            2 -> return CombineFragment()
            3 -> return VoteFragment()
            4 -> return SettingsFragment()
        }
        return CombineFragment()
    }




}