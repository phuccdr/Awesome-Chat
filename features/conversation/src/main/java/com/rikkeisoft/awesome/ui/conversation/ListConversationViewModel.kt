package com.rikkeisoft.awesome.ui.conversation

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.core.base.BaseViewModel
import com.project.core.model.firebase.User
import com.project.core.utils.TimeUtils
import com.rikkeisoft.awesome.model.ConversationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListConversationViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth, val repo: ConversationRepository
) : BaseViewModel() {
    private val CONCURRENCY_COROUTINE: Int = 5
    private val _isLoadingNextConversation: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var hasMoreData = true
    val isLoadingNextConversation: StateFlow<Boolean> = _isLoadingNextConversation.asStateFlow()
    private val querySearch: MutableStateFlow<String> = MutableStateFlow("")

  val isSearching : MutableStateFlow<Boolean> = MutableStateFlow(false)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val resultSearch = isSearching.flatMapLatest { searchMode ->
        if (searchMode) {
            querySearch.debounce(500).distinctUntilChanged().mapLatest { keyword ->
                if (keyword.isBlank()) {
                    emptyList()
                } else {
                    repo.searchConversation(keyword)
                }
            }
        } else {
            querySearch.value = ""
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500),emptyList())


    
    private val _items: MutableStateFlow<List<ConversationItem>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<ConversationItem>> = combine(_items, _isLoadingNextConversation) { data, isLoading ->
        if (isLoading) {
            data + ConversationItem.LoadingFooter
        } else data
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(500),
        listOf()
    )

    private val currentUserUid = firebaseAuth.currentUser?.uid ?: ""
    private val limitFlow = MutableStateFlow(PAGE_SIZE)

    init {
        startObserveConversations()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startObserveConversations() {
        viewModelScope.launch {
            limitFlow.flatMapLatest { limit ->
                repo.observeConversations(limit)
            }.collect { conversations ->
                _isLoadingNextConversation.value = true
                val fetchedUiItems = mutableListOf<ConversationItem.ConversationUi>()

                // Xử lý song song việc lấy thông tin user cho từng conversation
                conversations.asFlow().flatMapMerge(concurrency = CONCURRENCY_COROUTINE) { conversation ->
                    flow {
                        val user = repo.handleGetFriendByMembers(conversation.members) ?: User()
                        val conversationUI = ConversationItem.ConversationUi(
                            id = conversation.id,
                            friendName = user.username,
                            avatarFriend = user.avatar,
                            lastMessage = conversation.lastMessage,
                            lastUpdate = TimeUtils.format(conversation.lastUpdate),
                            isLastMessageSender = conversation.lastSenderId == currentUserUid,
                            unreadMessageCount = conversation.unreadMessage[currentUserUid] ?: 0
                        )
                        emit(conversationUI)
                    }
                }.collect { uiItem ->
                    fetchedUiItems.add(uiItem)
                }

                // Sắp xếp lại danh sách UI theo đúng thứ tự thời gian từ Firestore
                val sortedUiItems = conversations.mapNotNull { conv ->
                    fetchedUiItems.find { it.id == conv.id }
                }
                
                _items.value = sortedUiItems
                hasMoreData = conversations.size.toLong() >= limitFlow.value
                _isLoadingNextConversation.value = false
            }
        }
    }

    fun loadNextPageConversations() {
        if (_isLoadingNextConversation.value || !hasMoreData) return
        limitFlow.value += PAGE_SIZE
    }

    fun onSearch(keyword:String){
        querySearch.value = keyword
        Timber.d(keyword)
    }

    fun setSearching(status:Boolean){
        isSearching.value = status
    }
}
