package com.rikkeisoft.awesome.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.project.core.base.BaseViewModel
import com.project.core.model.firebase.Message
import com.project.core.navigationComponent.BundleKeys.CONVERSATION_ID
import com.project.core.utils.resource.ResourceUtils
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.model.ConversationChat
import com.rikkeisoft.awesome.ui.conversation.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: MessageRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    val conversation: MutableStateFlow<ConversationChat?> = MutableStateFlow(null)

    private val _isLoadingNextPage = MutableStateFlow(false)
    val isLoadingNextPage: StateFlow<Boolean> = _isLoadingNextPage.asStateFlow()

    private val limitFlow = MutableStateFlow(PAGE_SIZE)
    var hasMoreData = true
    private var conversationId: String? = null

    init {
        conversationId = savedStateHandle.get<String>(CONVERSATION_ID)
        viewModelScope.launch {
            conversationId?.let { id ->
                conversation.value = repo.getConversation(id)
                startObserveMessages(id)
            } ?: run {
                messageError.value = ResourceUtils.getString(R.string.conversation_not_found)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startObserveMessages(id: String) {
        viewModelScope.launch {
            limitFlow.flatMapLatest { limit ->
                repo.observeMessages(id, limit)
            }.flowOn(Dispatchers.IO).collectLatest { fetchedMessages ->
                _messages.value = fetchedMessages
                hasMoreData = fetchedMessages.size.toLong() >= limitFlow.value
                _isLoadingNextPage.value = false
            }
        }
    }

    fun loadNextPage() {
        if (_isLoadingNextPage.value || !hasMoreData) return
        _isLoadingNextPage.value = true
        limitFlow.value += PAGE_SIZE
    }
}
