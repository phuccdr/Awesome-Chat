package com.rikkeisoft.awesome

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.model.firebase.FriendShip
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.model.FriendShipUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FriendsRepository @Inject constructor(
    private val db: FirebaseFirestore, private val auth: FirebaseAuth
) {
    suspend fun loadFriendsShip(): List<FriendShipUI.FriendUI> = withContext(Dispatchers.IO) {
        val currentUserId = auth.currentUser?.uid ?: return@withContext emptyList()
        val q1 = db.collection("friend_ships").whereEqualTo("status", "ACTIVE")
            .whereEqualTo("userId1", currentUserId)
        val q2 = db.collection("friend_ships").whereEqualTo("status", "ACTIVE")
            .whereEqualTo("userId2", currentUserId)
        val docs = (q1.get().await().documents + q2.get().await().documents).distinctBy { it.id }
            .mapNotNull { it.toObject(FriendShip::class.java) }
        docs.map { friendShip ->
            async {
                val friendId =
                    if (currentUserId == friendShip.userId1) friendShip.userId2 else friendShip.userId1
                if (friendId == null) return@async null
                val friend = db.collection("users").document(friendId).get().await()
                    .toObject(User::class.java)
                FriendShipUI.FriendUI(
                    id = friendShip.id,
                    createdAt = friendShip.createdAt,
                    userId1 = friendShip.userId1,
                    userId2 = friendShip.userId2,
                    status = friendShip.status,
                    friend = friend,
                    conversationId = friendShip.conversationId
                )
            }
        }.awaitAll().filterNotNull().toList()
    }

    fun loadAllUserPaging(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = UserPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = UserPagingSource.PAGE_SIZE
            ), pagingSourceFactory = {
                UserPagingSource(db)
            }).flow
    }
}