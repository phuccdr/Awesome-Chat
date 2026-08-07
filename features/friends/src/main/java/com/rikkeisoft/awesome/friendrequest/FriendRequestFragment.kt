package com.rikkeisoft.awesome.friendrequest

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.core.base.fragment.BaseFragmentNotRequireViewModel
import com.project.core.utils.collectLatestFlowOnView
import com.rikkeisoft.awesome.FriendsViewModel
import com.rikkeisoft.awesome.friends.R
import com.rikkeisoft.awesome.friends.databinding.FragmentFriendRequestBinding
import timber.log.Timber

class FriendRequestFragment :
    BaseFragmentNotRequireViewModel<FragmentFriendRequestBinding>(R.layout.fragment_friend_request) {
    private val viewModel by viewModels<FriendsViewModel>(
        ownerProducer = { requireParentFragment() })
    private val receivedAdapter by lazy {
        ReceivedFriendRequestAdapter(onAcceptFriendRequest = { friendRequestId ->
            friendRequestId?.let {
                viewModel.acceptFriendRequest(friendRequestId)
            }
        }, onRejectFriendRequest = { friendRequestId ->
            friendRequestId?.let {
                viewModel.rejectFriendRequest(friendRequestId)
            }
        })
    }
    private val sentAdapter by lazy {
        SentFriendRequestAdapter(onCancelClick = { friendRequest ->
            friendRequest.id?.let {
                viewModel.cancelFriendRequest(it)
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupFriendRequestRecyclerView()
    }

    private fun setupFriendRequestRecyclerView() {
        with(binding) {
            rvReceivedFriendRequest.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = receivedAdapter
                ItemTouchHelper(SwipeItemTouchHelper()).attachToRecyclerView(this)
            }
            rvSentFriendRequest.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = sentAdapter
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.sentFriendRequests.collectLatestFlowOnView(viewLifecycleOwner) {
            sentAdapter.submitData(it)
            Timber.d("sentFriendRequestsData: $it")
        }

        viewModel.receivedFriendRequests.collectLatestFlowOnView(viewLifecycleOwner) {
            Timber.d("receivedFriendRequest: $it")
            receivedAdapter.submitData(it)
        }
    }
}
