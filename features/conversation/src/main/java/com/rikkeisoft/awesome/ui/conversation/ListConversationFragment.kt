package com.rikkeisoft.awesome.ui.conversation

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.prefetcher.bindToLifecycle
import com.project.core.utils.prefetcher.setupWithPrefetchViewPool
import com.rikkeisoft.awesome.ConversationNavigation
import com.rikkeisoft.awesome.adapter.ListConversationAdapter
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.FragmentListConversationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ListConversationFragment :
    BaseFragment<FragmentListConversationBinding, ListConversationViewModel>(R.layout.fragment_list_conversation
    ) {

    private var adapterConversation: ListConversationAdapter? = null
    private val viewModel: ListConversationViewModel by viewModels()
    override fun getVM() = viewModel

    @Inject
    lateinit var appNavigator: ConversationNavigation

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initAdapter()
        viewModel.loadNextPageConversations()
    }

    private fun initAdapter() {
        adapterConversation = ListConversationAdapter()

        binding.rvConversations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterConversation

            setupWithPrefetchViewPool {
                setPrefetchBound(viewType = R.layout.item_conversation, count = 10)
            }.bindToLifecycle(viewLifecycleOwner)

            // Xử lý Pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (totalItemCount <= (lastVisibleItem + 5)) {
                        viewModel.loadNextPageConversations()
                    }
                }
            })
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect {
                    adapterConversation?.submitList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvConversations.adapter = null
        adapterConversation = null
        super.onDestroyView()
    }
}