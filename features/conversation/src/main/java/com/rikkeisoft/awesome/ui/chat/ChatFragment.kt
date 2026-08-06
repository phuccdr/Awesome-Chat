package com.rikkeisoft.awesome.ui.chat

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.collectFlowOnView
import com.project.core.utils.collectLatestFlowOnView
import com.project.core.utils.loadImage
import com.project.core.utils.prefetcher.bindToLifecycle
import com.project.core.utils.prefetcher.setupWithPrefetchViewPool
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.setOnSafeClickListener
import com.project.core.utils.toastMessage
import com.project.permission.isPermissionGranted
import com.rikkeisoft.awesome.ConversationNavigation
import com.rikkeisoft.awesome.adapter.gallery.GalleryAdapter
import com.rikkeisoft.awesome.adapter.message.MessageAdapter
import com.rikkeisoft.awesome.adapter.sticker.StickerAdapter
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.FragmentChatBinding
import com.rikkeisoft.awesome.custom.ChatItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding, ChatViewModel>(R.layout.fragment_chat) {
    val PRELOAD_MESSAGE = 5
    private val viewModel: ChatViewModel by viewModels()
    override fun getVM() = viewModel

    private val adapterMessage by lazy {
        MessageAdapter(onMessageClick = { itemId ->
            Timber.tag("ChatMessage").d("onMessageClick $itemId")
            viewModel.onClickItemMessage(itemId)
        }, onImageClick = { imageUrl -> })
    }
    private val pickMultipleMedia = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.sendImageMessage()
        }
    }
    private val galleryAdapter by lazy {
        GalleryAdapter(onClick = { viewModel.toggleSelection(it.uri) })
    }
    private val stickerAdapter by lazy {
        StickerAdapter(onClick = { sticker ->
            viewModel.sendStickerMessage(sticker)
        })
    }
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.any { it.value }
        if (isGranted) {
            viewModel.setInputMode(ChatInputMode.GALLERY)
            galleryAdapter.refresh()
        } else {
            val requiredPermissions = getRequiredPermissions()
            val isPermanentlyDenied = requiredPermissions.all {
                !shouldShowRequestPermissionRationale(it)
            }
            if (isPermanentlyDenied) {
                pickMultipleMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                toastMessage(ResourceUtils.getString(R.string.gallery_permission_needed))
            }
        }
    }

    override fun onKeyboardVisibilityChanged(isKeyboardVisible: Boolean, keyboardHeight: Int) {
        super.onKeyboardVisibilityChanged(isKeyboardVisible, keyboardHeight)
        with(binding) {
            constraintLayout.updatePadding(0, 0, 0, keyboardHeight)
        }
    }

    @Inject
    lateinit var appNavigator: ConversationNavigation

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.viewModel = viewModel
        binding.apply {
            btnBack.setOnSafeClickListener {
                appNavigator.back()
            }
            layoutInput.btnAddImage.setOnSafeClickListener {
                if (viewModel?.inputMode?.value == ChatInputMode.GALLERY) {
                    viewModel?.setInputMode(ChatInputMode.NONE)
                } else {
                    handleOpenSelectImage()
                }
            }
            layoutInput.edtInputMessage.doAfterTextChanged {
                this@ChatFragment.viewModel.onInputTextChanged(it?.toString() ?: "")
            }
            layoutInput.btnSendMessage.setOnSafeClickListener {
                this@ChatFragment.viewModel.sendTextMessage()
            }
            layoutInput.edtInputMessage.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    viewModel?.setInputMode(ChatInputMode.KEYBOARD)
                }
            }
            layoutInput.btnAddSticker.setOnSafeClickListener {
                val currentMode = viewModel?.inputMode?.value
                if (currentMode == ChatInputMode.STICKER) {
                    viewModel?.setInputMode(ChatInputMode.NONE)
                } else {
                    viewModel?.setInputMode(ChatInputMode.STICKER)
                }
            }
        }
        setupChatRecyclerView()
        setupGalleryRecyclerView()
        setupStickerRecyclerView()
    }

    private fun setupStickerRecyclerView() {
        binding.rvStickers.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = stickerAdapter
        }
    }

    private fun setupGalleryRecyclerView() {
        binding.rvGallery.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = galleryAdapter
        }
        galleryAdapter.addLoadStateListener { loadState ->
            binding.pbPanel.isVisible = loadState.source.append is LoadState.Loading
        }
    }

    private fun setupChatRecyclerView() {
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
                    val firstVisiblePosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    Timber.tag("ChatMessage")
                        .d("scrolled firstVisiblePosition: $firstVisiblePosition dy: $dy")
                    if (dy <= 0 && firstVisiblePosition <= PRELOAD_MESSAGE && !viewModel.isLoadingNextPage.value && viewModel.hasMoreData) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.conversation.collectFlowOnView(viewLifecycleOwner) { conversationUI ->
            binding.apply {
                ivAvatarFriend.loadImage(conversationUI?.friend?.avatar, true)
                tvFriendName.text = conversationUI?.friend?.username
            }
        }
        viewModel.messageItems.collectLatestFlowOnView(viewLifecycleOwner) { messages ->
            val isBottom = !binding.rvMessages.canScrollVertically(1)
            adapterMessage.submitList(messages) {
                adapterMessage.itemCount.let {
                    val last = it - 1
                    if (last >= 0 && isBottom) {
                        binding.rvMessages.scrollToPosition(last)
                    }
                }
            }
        }
        viewModel.inputText.collectLatestFlowOnView(viewLifecycleOwner) { text ->
            binding.layoutInput.apply {
                if (edtInputMessage.text.toString() != text) {
                    edtInputMessage.setText(text)
                    edtInputMessage.setSelection(text.length)
                }
            }
        }
        viewModel.galleryImages.collectLatestFlowOnView(viewLifecycleOwner) {
            galleryAdapter.submitData(it)
        }
        viewModel.selectedUris.collectLatestFlowOnView(viewLifecycleOwner) {
            galleryAdapter.submitSelection(it)
        }
        viewModel.inputMode.collectLatestFlowOnView(viewLifecycleOwner) { mode ->
            handleInputModeChange(mode)
        }
        viewModel.stickers.collectLatestFlowOnView(viewLifecycleOwner) {
            stickerAdapter.submitList(it)
        }
        viewModel.isSendMessageEnable.collectLatestFlowOnView(viewLifecycleOwner) { isEnable ->
            binding.layoutInput.btnSendMessage.isEnabled = isEnable
            val tintColor = if (isEnable) {
                ResourceUtils.getColor(com.project.core.R.color.primary_color)
            } else {
                ResourceUtils.getColor(com.project.core.R.color.color_button_disable)
            }
            binding.layoutInput.btnSendMessage.imageTintList =
                android.content.res.ColorStateList.valueOf(tintColor)
        }
    }

    private fun handleInputModeChange(mode: ChatInputMode) {
        when (mode) {
            ChatInputMode.KEYBOARD -> {
                binding.panelLayout.isVisible = false
            }

            ChatInputMode.GALLERY -> {
                binding.panelLayout.isVisible = true
                binding.rvGallery.isVisible = true
                binding.rvStickers.isVisible = false
            }

            ChatInputMode.STICKER -> {
                binding.panelLayout.isVisible = true
                binding.rvStickers.isVisible = true
                binding.rvGallery.isVisible = false
            }

            ChatInputMode.NONE -> {
                binding.pbPanel.isVisible = false
            }
        }
    }

    private fun handleOpenSelectImage() {
        val permissions = getRequiredPermissions()
        val isGranted = permissions.any { requireContext().isPermissionGranted(it) }

        if (isGranted) {
            viewModel.setInputMode(ChatInputMode.GALLERY)
        } else {
            requestPermissions.launch(permissions)
        }
    }

    private fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

}
