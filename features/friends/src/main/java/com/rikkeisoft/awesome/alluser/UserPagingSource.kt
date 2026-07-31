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

class UserPagingSource(
    private val db: FirebaseFirestore, private val currentUserId: String?
) : PagingSource<DocumentSnapshot, UserUI.UserItem>() {
    companion object {
        const val PAGE_SIZE = 16
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, UserUI.UserItem> {
        return try {
            var query = db.collection("users").orderBy("firstName").limit(PAGE_SIZE.toLong())
            params.key?.let {
                query = query.startAfter(it)
            }
            val snapshot = query.get().await()
            val users = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)
            }
            val userItems = coroutineScope {
                users.map { user ->
                    async {
                        val status = if (currentUserId != null) {
                            val friendSnapshot =
                                db.collection("users").document(currentUserId).collection("friends")
                                    .whereEqualTo("friendId", user.uid)
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
                        } else {
                            UserStatus.NotFriend(user.uid)
                        }

                        UserUI.UserItem(user = user, userStatus = status)
                    }
                }.awaitAll()
            }

            LoadResult.Page(
                data = userItems, prevKey = null, nextKey = snapshot.documents.lastOrNull()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, UserUI.UserItem>): DocumentSnapshot? {
        return state.pages.lastOrNull()?.nextKey
    }
}