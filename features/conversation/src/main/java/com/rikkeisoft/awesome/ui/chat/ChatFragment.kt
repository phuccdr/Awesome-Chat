package com.rikkeisoft.awesome.ui.chat

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.loadImage
import com.project.core.utils.prefetcher.bindToLifecycle
import com.project.core.utils.prefetcher.setupWithPrefetchViewPool
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.ConversationNavigation
import com.rikkeisoft.awesome.adapter.message.MessageAdapter
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.FragmentChatBinding
import com.rikkeisoft.awesome.custom.ChatItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding, ChatViewModel>(R.layout.fragment_chat) {
    private val viewModel: ChatViewModel by viewModels()
    override fun getVM() = viewModel

    private var adapterMessage: MessageAdapter? = null

    @Inject
    lateinit var appNavigator: ConversationNavigation

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.apply {
            btnBack.setOnSafeClickListener {
                appNavigator.back()
            }
        }
        setupChatRecyclerView()
    }

    private fun setupChatRecyclerView() {
        adapterMessage = MessageAdapter(onMessageClick = {}, onImageClick = { imageUrl -> })
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = adapterMessage
            addItemDecoration(ChatItemDecoration())
            setupWithPrefetchViewPool {
                setPrefetchBound(viewType = R.layout.item_received_text_message, count = 10)
                setPrefetchBound(viewType = R.layout.item_received_image_message, count = 3)
                setPrefetchBound(viewType = R.layout.item_received_sticker_message, count = 2)
                setPrefetchBound(viewType = R.layout.item_sent_text_message, count = 10)
                setPrefetchBound(viewType = R.layout.item_sent_image_message, count = 3)
                setPrefetchBound(viewType = R.layout.item_sent_sticker_message, count = 2)
                setPrefetchBound(viewType = R.layout.item_header_time_message, count = 3)
            }.bindToLifecycle(viewLifecycleOwner)

            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    binding.rvMessages.postDelayed({
                        val count = binding.rvMessages.adapter?.itemCount ?: 0
                        if (count > 0) {
                            binding.rvMessages.smoothScrollToPosition(count - 1)
                        }
                    }, 100)
                }
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val firstVisible = layoutManager.findFirstVisibleItemPosition()
                    val firstView = layoutManager.findViewByPosition(firstVisible)
                    val offset = firstView?.top ?: 0

//                    layoutManager.scrollToPositionWithOffset(
//                        firstVisible + numberOfNewItems, offset
//                    )

                    if (!viewModel.isLoadingNextPage.value && viewModel.hasMoreData && totalItemCount > 0) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.conversation.collectLatest { conversationUI ->
                        binding.apply {
                            ivAvatarFriend.loadImage(conversationUI?.friend?.avatar, true)
                            tvFriendName.text = conversationUI?.friend?.username
                        }
                    }
                }
                launch {
                    viewModel.messageItems.collectLatest { messages ->
                        adapterMessage?.submitList(messages)
                    }
                }
            }
        }
    }
}
