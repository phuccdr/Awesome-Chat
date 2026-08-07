package com.rikkeisoft.awesome

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.project.core.base.BaseViewModel
import com.rikkeisoft.awesome.model.FriendRequestUI
import com.rikkeisoft.awesome.model.FriendShipUI
import com.rikkeisoft.awesome.model.UserStatus
import com.rikkeisoft.awesome.model.UserUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val friendRequestRepository: FriendRequestRepository
) : BaseViewModel() {
    val searchQuery = MutableStateFlow("")
    val isSearchMode = MutableStateFlow(false)
    private val _userRefreshTrigger = MutableStateFlow(0L)

    fun invalidateUserPaging() {
        _userRefreshTrigger.value = System.currentTimeMillis()
    }

    private val _actionState: MutableSharedFlow<FriendActionState> =
        MutableSharedFlow<FriendActionState>()
    val actionState: SharedFlow<FriendActionState> = _actionState.asSharedFlow()
    val receivedFriendRequests: Flow<PagingData<FriendRequestUI>> =
        friendRequestRepository.loadReceivedFriendRequest().cachedIn(viewModelScope)
    val sentFriendRequests: Flow<PagingData<FriendRequestUI>> =
        friendRequestRepository.loadSentFriendRequest(onInvalidated = ::invalidateUserPaging)
            .cachedIn(viewModelScope)

    @OptIn(FlowPreview::class)
    val searchResult: StateFlow<List<FriendShipUI>> =
        searchQuery.debounce(200.milliseconds).flatMapLatest { keyword ->
            friendsRepository.search(keyword)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val friendShipsPaging: Flow<PagingData<FriendShipUI>> =
        friendsRepository.loadFriendsPaging(onInvalidated = ::invalidateUserPaging)
            .map { pagingData ->
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
    val users: Flow<PagingData<UserUI>> = _userRefreshTrigger.flatMapLatest {
        friendsRepository.loadAllUserPaging()
    }.map { pagingData ->
        pagingData.map { userItem: UserUI.UserItem ->
            userItem as UserUI
        }.insertSeparators { before, after ->
            val bef = (before as? UserUI.UserItem)?.firstName?.firstOrNull()?.uppercaseChar()
            val aft = (after as? UserUI.UserItem)?.firstName?.firstOrNull()?.uppercaseChar()
            if ((bef == null || bef != aft) && aft != null) {
                UserUI.AlphabetHeader(aft.toString())
            } else {
                null
            }
        }
    }.cachedIn(viewModelScope)
    private var pendingCancelFriendRequest: UserStatus? = null

    fun setSearchMode(mode: Boolean) {
        isSearchMode.value = mode
    }

    fun acceptFriendRequest(friendRequestId: String) {
        viewModelScope.launch {
            try {
                Timber.d("friendRequestId: $friendRequestId")
                friendRequestRepository.acceptFriendRequest(friendRequestId)
            } catch (e: Exception) {
                Timber.e("acceptFriendRequest: $e")
                handleError(e, null)
            } finally {
            }
        }
    }

    fun cancelFriendRequest(friendRequestId: String) {
        viewModelScope.launch {
            try {
                friendRequestRepository.cancelFriendRequest(friendRequestId)
                Timber.tag("friendRequestId").d(friendRequestId)
            } catch (e: Exception) {
                handleError(e, null)
            } finally {
            }
        }
    }

    fun rejectFriendRequest(friendRequestId: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                friendRequestRepository.rejectFriendRequest(friendRequestId)
            } catch (e: Exception) {
                handleError(e, null)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun handleClickItem(userStatus: UserStatus) {
        Timber.d("handleClickItem: userStatus $userStatus")
        viewModelScope.launch {
            try {
                when (userStatus) {
                    is UserStatus.RequestSent -> {
                        val friendReq = userStatus.friendRequestId
                        Timber.tag("friendRequestId").d(friendReq)
                        pendingCancelFriendRequest = userStatus
                        _actionState.emit(
                            FriendActionState.ShowConfirmCancelFriendRequestDialog(
                                friendReq,
                            )
                        )
                    }

                    is UserStatus.NotFriend -> {
                        val receiverId = userStatus.receiverId
                        friendRequestRepository.sendFriendRequest(receiverId)
                    }

                    else -> return@launch
                }
            } catch (e: Exception) {
                Timber.e(e.toString())
                messageError.value = e.message
            }
        }
    }

    fun onClickOkCancelFriendRequest() {
        viewModelScope.launch {
            try {
                pendingCancelFriendRequest?.let { userStatus ->
                    if (userStatus is UserStatus.RequestSent) {
                        friendRequestRepository.cancelFriendRequest(userStatus.friendRequestId)
                    }
                }
            } catch (e: Exception) {
                messageError.value = e.message
                Timber.e(e.toString())
            }
        }
    }

    fun onClickItemFriend(conversationId: String) {
        viewModelScope.launch {
            _actionState.emit(FriendActionState.NavToChatScreen(conversationId))
        }

    }
}

sealed class FriendActionState {
    data class NavToChatScreen(val conversationId: String) : FriendActionState()
    data class ShowConfirmCancelFriendRequestDialog(val friendRequestId: String) :
        FriendActionState()

}