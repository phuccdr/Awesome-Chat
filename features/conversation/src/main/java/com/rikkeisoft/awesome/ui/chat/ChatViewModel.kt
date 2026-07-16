package com.rikkeisoft.awesome.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.core.base.BaseViewModel
import com.project.core.navigationComponent.BundleKeys.CONVERSATION_ID
import com.project.core.utils.resource.ResourceUtils
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.model.ConversationChat
import com.rikkeisoft.awesome.model.MessageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: MessageRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    private val _messageItems: MutableStateFlow<List<MessageItem>> = MutableStateFlow(emptyList())
    val messageItems: StateFlow<List<MessageItem>> = _messageItems.asStateFlow()
    val conversation: MutableStateFlow<ConversationChat?> = MutableStateFlow(null)
    private val _isLoadingNextPage = MutableStateFlow(false)
    val isLoadingNextPage: StateFlow<Boolean> = _isLoadingNextPage.asStateFlow()
    var hasMoreData = true
    private var conversationId: String? = null
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    init {
        conversationId = savedStateHandle.get<String>(CONVERSATION_ID)
        Timber.d(conversationId)
        viewModelScope.launch {
            conversationId?.let {
                conversation.value = repo.getConversation(it)
                firstLoadMessages(it)
                observerLastMessage()
            } ?: run {
                messageError.value = ResourceUtils.getString(R.string.conversation_not_found)
            }
        }
    }

    suspend fun firstLoadMessages(conversationId: String) {
            _isLoadingNextPage.value = true
            val fetchedMessages = repo.firstLoadMessages(conversationId)
            Timber.tag("Chat Message").d("firstLoadMessages $fetchedMessages")
            val mappedItems = MessageToMessageItemMapper.mapMessagesToMessageItems(
                messages = fetchedMessages,
                currentUserId = currentUserId,
                friendAvatar = conversation.value?.friend?.avatar ?: ""
            )
            _messageItems.value = mappedItems
            _isLoadingNextPage.value = false
        }

    private fun observerLastMessage(){
        conversationId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repo.observeLatestMessages(it).buffer(10).collect{ message->
                    val mapperItem = MessageToMessageItemMapper.mapMessagesToMessageItems(
                        messages = listOf(message),
                        currentUserId = currentUserId,
                        friendAvatar = conversation.value?.friend?.avatar ?: "")
//                    val item = MessageToMessageItemMapper.ma
                    Timber.tag("Chat Message").d("observerLastMessage $mapperItem")
                    _messageItems.value = _messageItems.value.toMutableList().apply { addAll(mapperItem) }
                }
            }
        }

    }

    fun loadNextPage() {
        if (_isLoadingNextPage.value || !hasMoreData) return
        conversationId?.let {
            viewModelScope.launch {
                _isLoadingNextPage.value = true
                try {
                    val oldMessages = repo.loadNextPage(it)
                    if (oldMessages.isEmpty()) {
                        hasMoreData = false
                    } else {
                        val mappedItems = MessageToMessageItemMapper.mapMessagesToMessageItems(
                            messages = oldMessages,
                            currentUserId = currentUserId,
                            friendAvatar = conversation.value?.friend?.avatar ?: ""
                        )
                        // Nối các tin nhắn cũ vào đầu danh sách
                        _messageItems.value = mappedItems + _messageItems.value
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                } finally {
                    _isLoadingNextPage.value = false
                }
            }
        }
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    private fun startObserveMessages(id: String) {
//        viewModelScope.launch {
//            limitFlow.flatMapLatest { limit ->
//                repo.observeMessages(id, limit)
//            }.flowOn(Dispatchers.IO).collectLatest { fetchedMessages ->
//                val friendAvatar = conversation.value?.friend?.avatar ?: ""
//                val mappedItems = MessageToMessageItemMapper.mapMessagesToMessageItems(
//                    messages = fetchedMessages,
//                    currentUserId = currentUserId,
//                    friendAvatar = friendAvatar
//                )
//                _messageItems.value = mappedItems
//                hasMoreData = fetchedMessages.size.toLong() >= limitFlow.value
//                _isLoadingNextPage.value = false
//            }
//        }
//    }


}
