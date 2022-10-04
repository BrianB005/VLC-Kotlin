package com.brianbett.vlc

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout

class ViewPagerAdapter(fragmentActivity: AudioFragment, private val tabLayout: TabLayout) :
    FragmentStateAdapter(fragmentActivity) {




    override fun getItemCount(): Int {
        return tabLayout.tabCount
    }

    override fun createFragment(position: Int): Fragment {
        val selectedFragment=when (position){
            0->TracksFragment()
            1->ArtistsFragment()
            2->AlbumsFragment()
            3->GenresFragment()
            else->TracksFragment()
        }
        return selectedFragment

    }
}