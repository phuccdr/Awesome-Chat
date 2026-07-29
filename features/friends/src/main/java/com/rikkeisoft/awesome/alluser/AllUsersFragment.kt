package com.rikkeisoft.awesome.alluser

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.core.base.fragment.BaseFragmentNotRequireViewModel
import com.project.core.utils.collectFlowOnView
import com.rikkeisoft.awesome.FriendsViewModel
import com.rikkeisoft.awesome.friends.R
import com.rikkeisoft.awesome.friends.databinding.FragmentAllFriendsBinding

class AllUsersFragment :
    BaseFragmentNotRequireViewModel<FragmentAllFriendsBinding>(R.layout.fragment_all_friends) {
    private val viewModel by viewModels<FriendsViewModel>(
        ownerProducer = {
            requireParentFragment()
        })
    private val userAdapter by lazy {
        UserAdapter { userId ->
            viewModel.sendFriendRequest(userId)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupAllUsersRecyclerView()

    }

    private fun setupAllUsersRecyclerView() {
        binding.rvAllUser.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    override fun bindingStateView() {
        viewModel.users.collectFlowOnView(viewLifecycleOwner) { pagingData ->
            userAdapter.submitData(pagingData)
        }
        super.bindingStateView()
    }

}