package com.rikkeisoft.awesome

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.collectLatestFlowOnView
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.friends.R
import com.rikkeisoft.awesome.friends.databinding.FragmentFriendsBinding
import com.rikkeisoft.awesome.friendslist.FriendListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsFragment :
    BaseFragment<FragmentFriendsBinding, FriendsViewModel>(R.layout.fragment_friends) {
    private val viewModel: FriendsViewModel by viewModels()
    override fun getVM() = viewModel

    private val searchAdapter by lazy { FriendListAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupViewPager2()
        setupSearch()
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.searchResult.collectLatestFlowOnView(viewLifecycleOwner) {
            searchAdapter.submitList(it)
            binding.tvNoResult.isVisible =
                it.isEmpty() && viewModel.searchQuery.value.isNotEmpty()
            binding.icNoResult.isVisible =
                it.isEmpty() && viewModel.searchQuery.value.isNotEmpty()
        }
        viewModel.isSearchMode.collectLatestFlowOnView(viewLifecycleOwner) { isSearchMode ->
            handleSearchVisibility(isSearchMode)
        }
    }

    private fun setupSearch() {
        binding.apply {
            edtSearch.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    viewModel.setSearchMode(true)
                }
            }
            rvSearchFriend.layoutManager = LinearLayoutManager(requireContext())
            rvSearchFriend.adapter = searchAdapter

            edtSearch.doAfterTextChanged {
                val query = it?.toString().orEmpty()
                viewModel.searchQuery.value = query
            }

            btnCancelSearch.setOnSafeClickListener {
                edtSearch.text?.clear()
                edtSearch.clearFocus()
                viewModel.setSearchMode(false)
            }
        }
    }

    private fun handleSearchVisibility(isSearching: Boolean) {
        binding.apply {
            groupSearchFriend.isVisible = isSearching
            viewPagerFriends.isVisible = !isSearching
            itemFriendTabLayout.root.isVisible = !isSearching
            itemAllUsersTabLayout.root.isVisible = !isSearching
            itemFriendRequestTabLayout.root.isVisible = !isSearching
            divideTabLayout.isVisible = !isSearching
        }
    }

    private fun setupViewPager2() {
        binding.apply {
            val tabs = listOf(
                itemFriendTabLayout, itemAllUsersTabLayout, itemFriendRequestTabLayout
            )

            itemFriendTabLayout.tvTitle.text = ResourceUtils.getString(R.string.friend)
            itemFriendRequestTabLayout.tvTitle.text =
                ResourceUtils.getString(R.string.request_friend)
            itemAllUsersTabLayout.tvTitle.text = ResourceUtils.getString(R.string.all_user)

            viewPagerFriends.adapter = FriendsViewPagerAdapter(this@FriendsFragment)
            viewPagerFriends.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabs.forEachIndexed { index, tab ->
                        val selected = index == position
                        tab.tvTitle.isSelected = selected
                        tab.underscore.visibility = if (selected) View.VISIBLE else View.INVISIBLE
                    }
                }
            })
            itemFriendTabLayout.root.setOnSafeClickListener {
                viewPagerFriends.currentItem = 0
            }
            itemAllUsersTabLayout.root.setOnSafeClickListener {
                viewPagerFriends.currentItem = 1
            }
            itemFriendRequestTabLayout.root.setOnSafeClickListener {
                viewPagerFriends.currentItem = 2
            }
        }
    }

}