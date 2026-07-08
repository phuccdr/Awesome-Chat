package com.rikkeisoft.awesome.ui.conversation

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.core.base.BaseViewModel
import com.project.core.model.firebase.User
import com.project.core.utils.TimeUtils
import com.rikkeisoft.awesome.model.ConversationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListConversationViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth, val repo: ConversationRepository
) : BaseViewModel() {
    private val _items: MutableStateFlow<List<ConversationItem>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<ConversationItem>> = _items.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadNextPageConversations() {
        viewModelScope.launch {
            try {
                // Lấy trang dữ liệu tiếp theo từ repository
                val conversations = repo.getNextPage()
                if (conversations.isEmpty()) return@launch

                val fetchedUiItems = mutableListOf<ConversationItem.ConversationUi>()

                // Xử lý song song việc lấy thông tin user cho từng conversation
                conversations.asFlow().flatMapMerge(concurrency = 10) { conversation ->
                    flow {
                        val user = repo.handleGetFriendByMembers(conversation.members) ?: User()
                        val conversationUI = ConversationItem.ConversationUi(
                            id = conversation.id,
                            friendName = user.username,
                            avatarFriend = user.avatar,
                            lastMessage = conversation.lastMessage,
                            lastUpdate = TimeUtils.format(conversation.lastUpdate),
                            lastSender = conversation.lastSenderId,
                            unreadMessageCount = conversation.unreadMessage[firebaseAuth.currentUser?.uid]?.toIntOrNull() ?: 0
                        )
                        emit(conversationUI)
                    }
                }.collect { uiItem ->
                    fetchedUiItems.add(uiItem)
                }

                val sortedUiItems = conversations.mapNotNull { conv ->
                    fetchedUiItems.find { it.id == conv.id }
                }
                _items.value = _items.value + sortedUiItems
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}