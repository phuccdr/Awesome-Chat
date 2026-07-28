package com.rikkeisoft.awesome.ui.conversation

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.core.base.BaseViewModel
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.model.ConversationItem
import com.rikkeisoft.awesome.model.SearchMessage
import com.rikkeisoft.awesome.repository.ConversationRepository
import com.rikkeisoft.awesome.repository.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

data class SearchUIState(
    val results: List<SearchMessage> = emptyList(),
    val isSearching: Boolean = false,
    val lastUpdate: Long = System.currentTimeMillis()
)

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth, val repo: ConversationRepository
) : BaseViewModel() {
    private val _isLoadingNextConversation: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var hasMoreData = true
    val isLoadingNextConversation: StateFlow<Boolean> = _isLoadingNextConversation.asStateFlow()
    private val querySearch: MutableStateFlow<String> = MutableStateFlow("")
    val isSearching: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val currentUserUid = firebaseAuth.currentUser?.uid ?: ""
    private val limitFlow = MutableStateFlow(PAGE_SIZE)
    private val _items: MutableStateFlow<List<ConversationItem>> = MutableStateFlow(emptyList())

    init {
        startObserveConversations()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val resultSearch = isSearching.flatMapLatest { searchMode ->
        if (searchMode) {
            querySearch.debounce(300.milliseconds).mapLatest { keyword ->
                if (keyword.isBlank()) {
                    SearchUIState(results = emptyList(), isSearching = true)
                } else {
                    val result = repo.searchConversation(keyword)
                    SearchUIState(results = result, isSearching = true)
                }
            }.flowOn(Dispatchers.IO)
        } else {
            querySearch.value = ""
            flowOf(SearchUIState(isSearching = false))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), SearchUIState())
    val items: StateFlow<List<ConversationItem>> =
        combine(_items, _isLoadingNextConversation) { data, isLoading ->
            if (isLoading) {
                data + List(8) { ConversationItem.LoadingFooter }
            } else data
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(500), listOf()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startObserveConversations() {
        viewModelScope.launch {
            limitFlow.flatMapLatest { limit ->
                repo.observeConversations(limit)
            }.collectLatest { conversations ->
                _isLoadingNextConversation.value = true
                val fetchedUiItems = coroutineScope {
                    conversations.map { conversation ->
                        async {
                            val user = repo.handleGetFriendByMembers(conversation.members) ?: User()

                            ConversationItem.ConversationUi(
                                id = conversation.id,
                                friendName = user.username,
                                avatarFriend = user.avatar,
                                lastMessage = conversation.lastMessage,
                                lastUpdate = conversation.lastUpdate,
                                isLastMessageSender = conversation.lastSenderId == currentUserUid,
                                unreadMessageCount = conversation.unreadMessage[currentUserUid] ?: 0
                            )
                        }
                    }.awaitAll()
                }
                _items.value = fetchedUiItems
                hasMoreData = conversations.size >= limitFlow.value
                _isLoadingNextConversation.value = false
            }
        }
    }

    fun loadNextPageConversations() {
        if (_isLoadingNextConversation.value || !hasMoreData) return
        limitFlow.value += PAGE_SIZE
    }

    fun onSearch(keyword: String) {
        querySearch.value = keyword
        Timber.tag("SearchConversation").d("Update querySearch: ${keyword}")
    }

    fun setSearching(status: Boolean) {
        isSearching.value = status
    }
}
