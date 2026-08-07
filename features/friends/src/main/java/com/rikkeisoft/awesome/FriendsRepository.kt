package com.rikkeisoft.awesome

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.model.firebase.FriendShip
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.alluser.UserPagingSource
import com.rikkeisoft.awesome.friendslist.FriendShipPagingSource
import com.rikkeisoft.awesome.model.FriendShipUI
import com.rikkeisoft.awesome.model.UserUI
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendsRepository @Inject constructor(
    private val db: FirebaseFirestore, private val auth: FirebaseAuth
) {
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun loadFriendsPaging(onInvalidated: (() -> Unit)? = null): Flow<PagingData<FriendShipUI.FriendUI>> {
        return Pager(
            config = PagingConfig(
                pageSize = FriendShipPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = FriendShipPagingSource.PAGE_SIZE
            ), pagingSourceFactory = {
                FriendShipPagingSource(db, getCurrentUserId(), onInvalidated)
            }).flow
    }

    fun loadAllUserPaging(): Flow<PagingData<UserUI.UserItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = UserPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = UserPagingSource.PAGE_SIZE
            ), pagingSourceFactory = {
                UserPagingSource(db, getCurrentUserId())
            }).flow
    }

    fun search(keyword: String): Flow<List<FriendShipUI>> = flow {
        val currentUserId = getCurrentUserId()
        if (currentUserId == null || keyword.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        try {
            val snapshot = db.collection("users").document(currentUserId).collection("friends")
                .whereEqualTo("status", "ACTIVE").orderBy("friendFirstName").startAt(keyword)
                .endAt(keyword + "\uf8ff").limit(100).get().await()
            val friendUIs = snapshot.documents.map { doc ->
                kotlinx.coroutines.coroutineScope {
                    async {
                        val friendShip = doc.toObject(FriendShip::class.java)
                        val friendId = friendShip?.friendId
                        val friendUser = if (friendId != null) {
                            db.collection("users").document(friendId).get().await()
                                .toObject(User::class.java)
                        } else null

                        FriendShipUI.FriendUI(
                            id = doc.id,
                            createdAt = friendShip?.createdAt,
                            friendId = friendShip?.friendId,
                            friendFirstName = friendShip?.friendFirstName,
                            friend = friendUser,
                            status = friendShip?.status
                                ?: com.project.core.model.firebase.FriendShipStatus.ACTIVE,
                            conversationId = friendShip?.conversationId
                        )
                    }
                }
            }.awaitAll()

            emit(friendUIs.sortedBy {
                (it.friendFirstName ?: it.friend?.username)?.firstOrNull()?.uppercaseChar()
            })
        } catch (_: Exception) {
            emit(emptyList())
        }
    }
}
