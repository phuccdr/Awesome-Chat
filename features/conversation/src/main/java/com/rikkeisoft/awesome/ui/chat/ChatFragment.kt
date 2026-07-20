package com.rikkeisoft.awesome.ui.chat

import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.loadImage2
import com.project.core.utils.prefetcher.bindToLifecycle
import com.project.core.utils.prefetcher.setupWithPrefetchViewPool
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.setOnSafeClickListener
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
            btnBack.setOnSafeClickListener {
                appNavigator.back()
            }
            layoutInput.edtInputMessage.doAfterTextChanged {
                viewModel.onInputTextChanged(it?.toString() ?: "")
            }
            layoutInput.btnSendMessage.setOnSafeClickListener {
                viewModel.sendMessage()
            }
        }
        setupChatRecyclerView()
        binding.root.viewTreeObserver.addOnGlobalFocusChangeListener { oldFocus, newFocus ->
            Timber.d(
                "old=${oldFocus?.javaClass?.simpleName} new=${newFocus?.javaClass?.simpleName}"
            )
        }
        binding.layoutInput.edtInputMessage.setOnFocusChangeListener { _, hasFocus ->
            Timber.d("EditText focus = $hasFocus")
        }
    }

    private fun setupChatRecyclerView() {
        adapterMessage = MessageAdapter(onMessageClick = { itemId ->
            Timber.tag("ChatMessage").d("onMessageClick $itemId")
            viewModel.onClickItemMessage(itemId)
        }, onImageClick = { imageUrl -> })
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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    Timber.tag("ChatMessage")
                        .d("scrolled firstVisiblePosition: $firstVisiblePosition dy: $dy")
                    if (dy <= 0 && firstVisiblePosition <= PRELOAG_MESSAGE && !viewModel.isLoadingNextPage.value && viewModel.hasMoreData) {
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
                                    binding.rvMessages.scrollToPosition(last)
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.inputText.collect { text ->
                        binding.layoutInput.apply {
                            if (edtInputMessage.text.toString() != text) {
                                edtInputMessage.setText(text)
                                edtInputMessage.setSelection(text.length)
                                Timber.d(
                                    "focus=${binding.layoutInput.edtInputMessage.hasFocus()}"
                                )
                            }
                            val isNotBlank = text.isNotBlank()
                            btnSendMessage.isEnabled = isNotBlank
                            val tintColor = if (isNotBlank) {
                                ResourceUtils.getColor(com.project.core.R.color.primary_color)
                            } else {
                                ResourceUtils.getColor(com.project.core.R.color.color_button_disable)
                            }
                            btnSendMessage.imageTintList =
                                android.content.res.ColorStateList.valueOf(tintColor)
                        }
                    }
                }
            }
        }
    }
}
