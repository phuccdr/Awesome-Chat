package com.rikkeisoft.awesome

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.project.core.base.BaseViewModel
import com.rikkeisoft.awesome.model.FriendShipUI
import com.rikkeisoft.awesome.model.UserUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
) : BaseViewModel() {
    val searchQuery = MutableStateFlow("")
    val isSearchMode = MutableStateFlow(false)

    @OptIn(FlowPreview::class)
    val searchResult: StateFlow<List<FriendShipUI>> =
        searchQuery.debounce(200.milliseconds).flatMapLatest { keyword ->
            friendsRepository.search(keyword)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val friendShipsPaging: Flow<PagingData<FriendShipUI>> =
        friendsRepository.loadFriendsPaging().map { pagingData ->
                pagingData.map { item: FriendShipUI.FriendUI ->
                    item as FriendShipUI
                }.insertSeparators { before: FriendShipUI?, after: FriendShipUI? ->
                    val bef = ((before as? FriendShipUI.FriendUI)?.friendFirstName
                        ?: (before as? FriendShipUI.FriendUI)?.friend?.username)?.firstOrNull()
                        ?.uppercaseChar()
                    val aft = ((after as? FriendShipUI.FriendUI)?.friendFirstName
                        ?: (after as? FriendShipUI.FriendUI)?.friend?.username)?.firstOrNull()
                        ?.uppercaseChar()
                    if ((bef == null || bef != aft) && aft != null) {
                        FriendShipUI.AlphabetHeader(aft.toString())
                    } else {
                        null
                    }
                }
            }.cachedIn(viewModelScope)
    val users: Flow<PagingData<UserUI>> = friendsRepository.loadAllUserPaging().map { pagingData ->
        pagingData.map { userItem: UserUI.UserItem ->
            userItem as UserUI
        }.insertSeparators { before, after ->
            val bef = (before as? UserUI.UserItem)?.username?.firstOrNull()?.uppercaseChar()
            val aft = (after as? UserUI.UserItem)?.username?.firstOrNull()?.uppercaseChar()
            if ((bef == null || bef != aft) && aft != null) {
                UserUI.AlphabetHeader(aft.toString())
            } else {
                null
            }
        }
    }.cachedIn(viewModelScope)

    fun setSearchMode(mode: Boolean) {
        isSearchMode.value = mode
    }
}