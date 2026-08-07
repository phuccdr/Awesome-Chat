package com.rikkeisoft.awesome

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rikkeisoft.awesome.alluser.AllUsersFragment
import com.rikkeisoft.awesome.friendrequest.FriendRequestFragment
import com.rikkeisoft.awesome.friendslist.FriendsListFragment

class FriendsViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FriendsListFragment()
            1 -> AllUsersFragment()
            else -> FriendRequestFragment()
        }
    }
}