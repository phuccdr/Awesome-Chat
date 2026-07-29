package com.rikkeisoft.awesome.friendslist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.model.firebase.FriendShip
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.model.FriendShipUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class FriendShipPagingSource(
    private val db: FirebaseFirestore, private val currentUserId: String?
) : PagingSource<DocumentSnapshot, FriendShipUI.FriendUI>() {
    companion object {
        const val PAGE_SIZE = 32
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, FriendShipUI.FriendUI> =
        withContext(Dispatchers.IO) {
            try {
                if (currentUserId == null) throw Exception("Error currentId=null")
                var query = db.collection("users").document(currentUserId).collection("friends")
                    .whereEqualTo("status", "ACTIVE").orderBy("friendFirstName")
                    .limit(PAGE_SIZE.toLong())

                params.key?.let {
                    query = query.startAfter(it)
                }
                val snapshot = query.get().await()
                val friendShips = snapshot.documents.mapNotNull {
                    it.toObject(FriendShip::class.java)
                }
                Timber.tag("12345").d(friendShips.size.toString())
                val friendUIs = friendShips.map { friendShip ->
                    async {
                        val friendId = friendShip.friendId ?: return@async null
                        val friendUser = db.collection("users").document(friendId).get().await()
                            .toObject(User::class.java)
                        FriendShipUI.FriendUI(
                            id = friendShip.id,
                            createdAt = friendShip.createdAt,
                            friendId = friendShip.friendId,
                            friendFirstName = friendShip.friendFirstName,
                            friend = friendUser,
                            status = friendShip.status,
                            conversationId = friendShip.conversationId
                        )
                    }
                }.awaitAll().filterNotNull()
                Timber.tag("Paging Friends").d(friendUIs.toString())

                LoadResult.Page(
                    data = friendUIs, prevKey = null, nextKey = snapshot.documents.lastOrNull()
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, FriendShipUI.FriendUI>): DocumentSnapshot? {
        return state.pages.lastOrNull()?.nextKey
    }
}