package com.rikkeisoft.awesome.alluser

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.core.base.fragment.BaseFragmentNotRequireViewModel
import com.project.core.utils.collectFlowOnView
import com.rikkeisoft.awesome.FriendsViewModel
import com.rikkeisoft.awesome.friends.R
import com.rikkeisoft.awesome.friends.databinding.FragmentAllFriendsBinding
import timber.log.Timber

class AllUsersFragment :
    BaseFragmentNotRequireViewModel<FragmentAllFriendsBinding>(R.layout.fragment_all_friends) {
    private val viewModel by viewModels<FriendsViewModel>(
        ownerProducer = {
            requireParentFragment()
        })
    private val userAdapter by lazy {
        UserAdapter { userStatus ->
            Timber.d(userStatus.toString())
            viewModel.handleClickItem(userStatus)
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
        userAdapter.addLoadStateListener { loadState ->
            val refreshState = loadState.source.refresh
//            binding.rvAllUser.isVisible = refreshState is LoadState.NotLoading
            if (refreshState is LoadState.Error) {
                Timber.e(refreshState.error, "AllUsersFragment Load Error")
            }
        }
    }

    override fun bindingStateView() {
        viewModel.users.collectFlowOnView(viewLifecycleOwner) { pagingData ->
            Timber.d(pagingData.toString())
            userAdapter.submitData(pagingData)
        }
        super.bindingStateView()
    }

}