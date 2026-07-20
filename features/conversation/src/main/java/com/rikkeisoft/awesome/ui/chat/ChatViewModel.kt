package com.rikkeisoft.awesome.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.project.core.base.BaseViewModel
import com.project.core.model.firebase.Message
import com.project.core.model.firebase.MessageType
import com.project.core.navigationComponent.BundleKeys.CONVERSATION_ID
import com.project.core.utils.isSameDay
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.toLocalDate
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.ext.copyMessageItem
import com.rikkeisoft.awesome.model.ConversationChat
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition
import com.rikkeisoft.awesome.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
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

    private val _inputMessage = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputMessage.asStateFlow()

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
                repo.updateUnread(it)
            } ?: run {
                messageError.value = ResourceUtils.getString(R.string.conversation_not_found)
            }
        }
    }

    suspend fun firstLoadMessages(conversationId: String) {
            _isLoadingNextPage.value = true
            val fetchedMessages = repo.firstLoadMessages(conversationId)
            Timber.tag("Chat Message").d("firstLoadMessages $fetchedMessages")
            val mappedItems = withContext(Dispatchers.Default){ MessageToMessageItemMapper.mapMessagesToMessageItems(
                messages = fetchedMessages,
                currentUserId = currentUserId,
                friendAvatar = conversation.value?.friend?.avatar ?: ""
            )}
            _messageItems.value = mappedItems
            _isLoadingNextPage.value = false
        }

    private fun observerLastMessage(){
        conversationId?.let {
            viewModelScope.launch {
                repo.observeLatestMessages(it).flowOn(Dispatchers.IO).buffer(5).collect{ message->
                    appendNewMessage(message)
                }
            }
        }

    }

    fun onClickItemMessage(itemId: String){
        var currentMessages = _messageItems.value
        currentMessages = currentMessages.map { messageItem ->
            if (messageItem is MessageItem.Message && messageItem.id == itemId && !(messageItem.messagePosition== MessagePosition.SINGLE  || messageItem.messagePosition== MessagePosition.BOTTOM) ){
                messageItem.copyMessageItem(isSelected = !messageItem.isSelected)
            } else {
                messageItem
            }
        }
        _messageItems.value = currentMessages
    }
    fun onInputTextChanged(text: String) {
        _inputMessage.value = text
    }

    fun sendMessage() {
        val content = _inputMessage.value.trim()
        val cid = conversationId ?: return
        if (content.isEmpty()) return

        viewModelScope.launch {
            try {
                val message = Message(
                    content = content,
                    createdAt = Timestamp.now(),
                    senderId = currentUserId,
                    receiverId = conversation.value?.friend?.uid ?: "",
                    type = MessageType.TEXT,
                    conversationId = cid
                )
                repo.sendMessage(cid, message)
                _inputMessage.value = ""
            } catch (e: Exception) {
                Timber.e(e)
                messageError.value = e.message
            }
        }
    }

    fun loadNextPage() {
        Timber.tag("ChatMessage").d("ChatViewModel: loadNextPage() called")
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


    fun appendNewMessage(message: Message) {
        val lastMessageItem = _messageItems.value.lastOrNull()
        val lastMessage = lastMessageItem as? MessageItem.Message
        val lastSenderId = lastMessage?.senderId ?: ""
        var lastMessagePosition: MessagePosition? = null
        var newMessagePosition: MessagePosition = MessagePosition.SINGLE

        val isSameDay = if (lastMessage != null) {
            message.createdAt?.let { lastMessage.createdAt?.isSameDay(it) } ?: false
        } else false

        var headerTimeMessage: MessageItem.DateHeader? = null
        if (!isSameDay) {
            headerTimeMessage = MessageItem.DateHeader(message.createdAt?.toLocalDate() ?: LocalDate.now())
        }

        if (lastMessage != null && lastSenderId == message.senderId && isSameDay) {
            when (lastMessage.messagePosition) {
                MessagePosition.SINGLE -> {
                    lastMessagePosition = MessagePosition.TOP
                    newMessagePosition = MessagePosition.BOTTOM
                }

                MessagePosition.BOTTOM -> {
                    lastMessagePosition = MessagePosition.MIDDLE
                    newMessagePosition = MessagePosition.BOTTOM
                }

                else -> Unit
            }
        }

        val mapperItem: MessageItem = MessageToMessageItemMapper.mapMessageToMessageItem(
            message = message,
            currentUserId = currentUserId,
            friendAvatar = conversation.value?.friend?.avatar ?: "",
            position = newMessagePosition
        )
        val newMessages: List<MessageItem> =
            if (headerTimeMessage == null) listOf(mapperItem) else listOf(
                headerTimeMessage,
                mapperItem
            )
        Timber.tag("ChatMessage").d("appendNewMessage $newMessages")

        _messageItems.value = _messageItems.value.toMutableList().apply {
            if (lastMessagePosition != null && this.isNotEmpty() && this[lastIndex] is MessageItem.Message) {
                this[lastIndex] =
                    (this[lastIndex] as MessageItem.Message).copyMessageItem(messagePosition = lastMessagePosition)
            }
            this.addAll(newMessages)
        }
    }
}
