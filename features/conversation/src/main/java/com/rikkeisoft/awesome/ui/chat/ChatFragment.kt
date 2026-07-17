package com.rikkeisoft.awesome.ui.chat

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.loadImage2
import com.project.core.utils.prefetcher.bindToLifecyclexoa123
import com.project.core.utils.prefetcher.setupWithPrefetchViewPoolxoa123
import com.project.core.utils.setOnSafeClickListenerxoa123
import com.rikkeisoft.awesome.ConversationNavigation
import com.rikkeisoft.awesome.adapter.message.MessageAdapter
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.FragmentChatBinding
import com.rikkeisoft.awesome.custom.ChatItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding, ChatViewModel>(R.layout.fragment_chat) {
    val PRELOAG_MESSAGE = 5
    private val viewModel: ChatViewModel by viewModels()
    override fun getVM() = viewModel

    private var adapterMessage: MessageAdapter? = null

    @Inject
    lateinit var appNavigator: ConversationNavigation

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.apply {
            btnBack.setOnSafeClickListenerxoa123 {
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
            setupWithPrefetchViewPoolxoa123 {
                setPrefetchBoundxoa123(viewType = R.layout.item_received_text_message, count = 10)
                setPrefetchBoundxoa123(viewType = R.layout.item_received_image_message, count = 3)
                setPrefetchBoundxoa123(viewType = R.layout.item_received_sticker_message, count = 2)
                setPrefetchBoundxoa123(viewType = R.layout.item_sent_text_message, count = 10)
                setPrefetchBoundxoa123(viewType = R.layout.item_sent_image_message, count = 3)
                setPrefetchBoundxoa123(viewType = R.layout.item_sent_sticker_message, count = 2)
                setPrefetchBoundxoa123(viewType = R.layout.item_header_time_message, count = 3)
            }.bindToLifecyclexoa123(viewLifecycleOwner)

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
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    Timber.tag("ChatMessage").d("scroll: $firstVisiblePosition")
                    if (dy < 0 && firstVisiblePosition <= PRELOAG_MESSAGE && !viewModel.isLoadingNextPage.value && viewModel.hasMoreData) {
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
                    viewModel.conversation.collect { conversationUI ->
                        binding.apply {
                            ivAvatarFriend.loadImage2(conversationUI?.friend?.avatar, true)
                            tvFriendName.text = conversationUI?.friend?.username
                        }
                    }
                }
                launch {
                    viewModel.messageItems.collectLatest { messages ->
                        val isBottom = !binding.rvMessages.canScrollVertically(1)
                        adapterMessage?.submitList(messages) {
                            adapterMessage?.itemCount?.let {
                                val last = it - 1
                                if (last >= 0 && isBottom) {
                                    binding.rvMessages.smoothScrollToPosition(last)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
