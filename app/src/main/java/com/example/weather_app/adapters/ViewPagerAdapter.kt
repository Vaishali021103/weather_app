package com.example.weather_app.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weather_app.LastFragment
import com.example.weather_app.TodayFragment
import com.example.weather_app.TomFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
                0->{
                    TodayFragment()
                }
                1->{
                    TomFragment()
                }
                2->{
                    LastFragment()
                }
            else->{
                Fragment()
            }
        }
    }

}