package com.rikkeisoft.awesome.friendrequest

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.core.model.firebase.FriendRequest
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.model.FriendRequestUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class FriendRequestPagingSource(
    private val db: FirebaseFirestore, private val currentUserId: String?
) : PagingSource<DocumentSnapshot, FriendRequestUI>() {
    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, FriendRequestUI> =
        withContext(Dispatchers.IO) {
            try {
                if (currentUserId == null) return@withContext LoadResult.Error(Exception("Current user ID is null"))
                var query =
                    db.collection("friends_request").whereEqualTo("receiverId", currentUserId)
                        .whereEqualTo("status", "PENDING")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .limit(params.loadSize.toLong())

                params.key?.let {
                    query = query.startAfter(it)
                }
                val snapshot = query.get().await()
                val friendRequests =
                    snapshot.documents.mapNotNull { it.toObject(FriendRequest::class.java) }
                val friendRequestUIs = friendRequests.map { friendRequest ->
                    async {
                        val senderUser =
                            db.collection("users").document(friendRequest.senderId).get().await()
                                .toObject(User::class.java)
                        FriendRequestUI(
                            id = friendRequest.id,
                            sender = senderUser,
                            createdAt = friendRequest.createdAt
                        )
                    }
                }.awaitAll()

                Timber.tag("123asd").d("${friendRequestUIs} - size: ${friendRequestUIs.size}")

                LoadResult.Page(
                    data = friendRequestUIs,
                    prevKey = null,
                    nextKey = snapshot.documents.lastOrNull()
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, FriendRequestUI>): DocumentSnapshot? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor)

        return page?.prevKey ?: page?.nextKey
    }

    companion object {
        const val PAGE_SIZE = 32
    }

}