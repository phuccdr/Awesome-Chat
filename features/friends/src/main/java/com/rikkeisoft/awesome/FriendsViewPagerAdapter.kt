package com.rikkeisoft.awesome

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rikkeisoft.awesome.allfriend.AllUsersFragment
import com.rikkeisoft.awesome.friendrequest.FriendRequestFragment
import com.rikkeisoft.awesome.friendslist.FriendsListFragment

class FriendsViewPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FriendsListFragment()
            1 -> AllUsersFragment()
            else -> FriendRequestFragment()
        }
    }
}