package com.rikkeisoft.awesome

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.project.core.base.BaseViewModel
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.model.FriendShipUI
import com.rikkeisoft.awesome.model.UserUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
) : BaseViewModel() {
    val rawFriendShips: MutableStateFlow<List<FriendShipUI.FriendUI>> =
        MutableStateFlow(emptyList())
    val searchQuery = MutableStateFlow("")
    val isSearchMode = MutableStateFlow<Boolean>(false)
    val searchResult: StateFlow<List<FriendShipUI>> = combine(
        rawFriendShips, searchQuery
    ) { friends, query ->
        val filtered = if (query.isEmpty()) {
            friends
        } else {
            friends.filter {
                it.friend?.username?.contains(query, ignoreCase = true) == true
            }
        }
        filtered.sortedBy {
            it.friend?.username?.first()?.uppercaseChar()
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val friendShips: StateFlow<List<FriendShipUI>> = rawFriendShips.map { friends ->
        buildList {
            friends.sortedBy { it.friend?.username?.first()?.uppercaseChar() }.groupBy {
                it.friend?.username?.first()?.uppercaseChar()
            }.forEach { (letter, ships) ->
                add(FriendShipUI.AlphabetHeader(letter.toString()))
                addAll(ships)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val users: Flow<PagingData<UserUI>> =
        friendsRepository.loadAllUserPaging().map { pagingData: PagingData<User> ->
            pagingData.map { user: User ->
                UserUI.UserItem(user)
            }.insertSeparators { before, after ->
                val bef = before?.username?.first()?.uppercaseChar()
                val aft = after?.username?.first()?.uppercaseChar()
                if ((bef == null || bef != aft) && aft != null) {
                    UserUI.AlphabetHeader(after.username.first().uppercaseChar().toString())
                } else if (aft == null) {
                    return@insertSeparators null
                } else {
                    null
                }
            }
        }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            rawFriendShips.value = friendsRepository.loadFriendsShip()
        }
    }

    fun setSearchMode(mode: Boolean) {
        isSearchMode.value = mode
    }
}