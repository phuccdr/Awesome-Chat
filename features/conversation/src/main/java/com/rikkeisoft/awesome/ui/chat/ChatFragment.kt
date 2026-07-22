package com.rikkeisoft.awesome.ui.chat

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.DeviceUtil
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

    private val adapterMessage by lazy {
      MessageAdapter(onMessageClick = { itemId ->
            Timber.tag("ChatMessage").d("onMessageClick $itemId")
            viewModel.onClickItemMessage(itemId)
        }, onImageClick = { imageUrl -> })
    }

    private val pickMultipleMedia =
    registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            // Danh sách ảnh đã chọn
        }
    }

    private val galleryAdapter by lazy {
        GalleryAdapter(onClick = { viewModel.toggleSelection(it.uri) })
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.any { it.value }
        if (isGranted) {
            viewModel.togglePanel(true)
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

    @Inject
    lateinit var appNavigator: ConversationNavigation


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.viewModel = viewModel
        binding.apply {
            btnBack.setOnSafeClickListener {
                appNavigator.back()
            }
            layoutInput.btnAddImage.setOnSafeClickListener {
                if(viewModel?.isPanelVisible?.value==true){
                    viewModel?.togglePanel(false)
                }else{
                    handleOpenSelectImage()
                }

            }
            layoutInput.edtInputMessage.doAfterTextChanged {
                this@ChatFragment.viewModel.onInputTextChanged(it?.toString() ?: "")
            }
            layoutInput.btnSendMessage.setOnSafeClickListener {
                this@ChatFragment.viewModel.sendMessage()
            }
            layoutInput.edtInputMessage.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    viewModel?.togglePanel(false)
                }
            }
//            btnSendMes.setOnSafeClickListener {
//                val uris = this@ChatFragment.viewModel.confirmSelection()
//                // OUT OF SCOPE: hand `uris` to the upload/send flow.
//            }
        }
        setupChatRecyclerView()
        setupGalleryRecyclerView()
    }

    private fun setupGalleryRecyclerView() {
        binding.rvGallery.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = galleryAdapter
        }
        galleryAdapter.addLoadStateListener { loadState ->
            binding.pbGallery.isVisible = loadState.source.append is LoadState.Loading
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
                            ivAvatarFriend.loadImage(conversationUI?.friend?.avatar, true)
                            tvFriendName.text = conversationUI?.friend?.username
                        }
                    }
                }
                launch {
                    viewModel.messageItems.collectLatest { messages ->
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

                        }
                    }
                }
                launch {
                    viewModel.galleryImages.collectLatest {
                        galleryAdapter.submitData(it)
                    }
                }
                launch {
                    viewModel.selectedUris.collectLatest {
                        galleryAdapter.submitSelection(it)
                    }
                }
                launch {
                    viewModel.isPanelVisible.collectLatest { isVisible ->
                        if (isVisible) {
                            binding.layoutInput.edtInputMessage.clearFocus()
                            DeviceUtil.hideSoftKeyboard(requireActivity())
                            binding.galleryPanel.isVisible = true
                        }else{
                            binding.galleryPanel.isVisible = false
                        }
                    }
                }
                launch {
                    viewModel.isSendMessageEnable.collectLatest { isEnable->

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
            }
        }
    }

    private fun handleOpenSelectImage() {
        val permissions = getRequiredPermissions()
        val isGranted = permissions.any { requireContext().isPermissionGranted(it) }

        if (isGranted) {
            viewModel.togglePanel(true)
        } else {
            requestPermissions.launch(permissions)
        }
    }

    private fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ->
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

}
