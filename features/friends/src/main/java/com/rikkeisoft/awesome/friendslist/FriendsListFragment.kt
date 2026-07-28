package com.rikkeisoft.awesome.friendslist

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.core.base.fragment.BaseFragmentNotRequireViewModel
import com.project.core.utils.collectLatestFlowOnView
import com.rikkeisoft.awesome.FriendsViewModel
import com.rikkeisoft.awesome.friends.R
import com.rikkeisoft.awesome.friends.databinding.FragmentFriendsListBinding
import timber.log.Timber

class FriendsListFragment :
    BaseFragmentNotRequireViewModel<FragmentFriendsListBinding>(R.layout.fragment_friends_list) {
    private val viewModel: FriendsViewModel by viewModels<FriendsViewModel>(
        ownerProducer = { requireParentFragment() })
    private val friendAdapter by lazy { FriendListAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.rvListFriend.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendAdapter
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.friendShipsPaging.collectLatestFlowOnView(viewLifecycleOwner) {
            friendAdapter.submitData(it)
            Timber.d(it.toString())
        }
    }
}