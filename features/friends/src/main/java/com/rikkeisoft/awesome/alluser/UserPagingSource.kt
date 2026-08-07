package com.rikkeisoft.awesome.alluser

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.model.firebase.FriendRequestStatus
import com.project.core.model.firebase.FriendShipStatus
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.model.UserStatus
import com.rikkeisoft.awesome.model.UserUI
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class UserPagingSource(
    private val db: FirebaseFirestore, private val currentUserId: String?
) : PagingSource<DocumentSnapshot, UserUI.UserItem>() {
    companion object {
        const val PAGE_SIZE = 32
    }

    init {
        Timber.d("UserPagingSource created")
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, UserUI.UserItem> {
        return try {
            var query = db.collection("users").orderBy("firstName").limit(PAGE_SIZE.toLong())
            params.key?.let {
                query = query.startAfter(it)
            }
            val snapshot = query.get().await()
            val users = snapshot.documents.mapNotNull {
                if (it.getString("uid").isNullOrBlank()) {
                    null
                } else {
                    it.toObject(User::class.java)
                }
            }
            val userItems = coroutineScope {
                users.map { user ->
                    async {
                        runCatching {
                            if (currentUserId.isNullOrEmpty()) {
                                error("Current Id is empty")
                            }
                            val status = run {
                                val friendSnapshot = db.collection("users").document(currentUserId)
                                    .collection("friends").whereEqualTo("friendId", user.uid)
                                    .whereEqualTo("status", FriendShipStatus.ACTIVE).limit(1).get()
                                    .await()

                                if (!friendSnapshot.isEmpty) {
                                    val conversationId =
                                        friendSnapshot.documents.first().getString("conversationId")
                                            ?: ""
                                    UserStatus.Friend(conversationId)
                                } else {
                                    val requestSnapshot = db.collection("friends_request")
                                        .whereEqualTo("senderId", currentUserId)
                                        .whereEqualTo("receiverId", user.uid).whereEqualTo(
                                            "status", FriendRequestStatus.PENDING
                                        ).limit(1).get().await()

                                    if (!requestSnapshot.isEmpty) {
                                        UserStatus.RequestSent(
                                            requestSnapshot.documents.first().id, user.uid
                                        )
                                    } else {
                                        UserStatus.NotFriend(user.uid)
                                    }
                                }
                            }
                            UserUI.UserItem(user = user, userStatus = status)
                        }.onFailure { e ->
                            Timber.e(e.toString())
                        }.getOrNull()
                    }
                }.awaitAll().filterNotNull()
            }

            LoadResult.Page(
                data = userItems,
                prevKey = params.key,
                nextKey = if (snapshot.isEmpty) null else snapshot.documents.lastOrNull()
            )
        } catch (e: Exception) {
            Timber.e(e, "UserPagingSource load error")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, UserUI.UserItem>): DocumentSnapshot? {
        return null
    }
}