package com.rikkeisoft.awesome

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.model.firebase.Conversation
import com.project.core.model.firebase.FriendRequest
import com.project.core.model.firebase.FriendRequestStatus
import com.project.core.model.firebase.FriendShip
import com.project.core.model.firebase.FriendShipStatus
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.friendrequest.FriendRequestPagingSource
import com.rikkeisoft.awesome.friendrequest.SentFriendRequestPagingSource
import com.rikkeisoft.awesome.model.FriendRequestUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendRequestRepository @Inject constructor(
    private val db: FirebaseFirestore, private val auth: FirebaseAuth
) {
    fun loadReceivedFriendRequest(): Flow<PagingData<FriendRequestUI>> {
        return Pager(
            config = PagingConfig(
                pageSize = FriendRequestPagingSource.PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = FriendRequestPagingSource.PAGE_SIZE
            ), pagingSourceFactory = {
                FriendRequestPagingSource(db, auth.currentUser?.uid)
            }).flow
    }

    fun loadSentFriendRequest(): Flow<PagingData<FriendRequestUI>> {
        return Pager(
            config = PagingConfig(
                pageSize = SentFriendRequestPagingSource.PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = SentFriendRequestPagingSource.PAGE_SIZE
            ), pagingSourceFactory = {
                SentFriendRequestPagingSource(db, auth.currentUser?.uid)
            }).flow
    }

    suspend fun acceptFriendRequest(requestId: String) {
        db.runTransaction { transaction ->
            val requestRef = db.collection("friends_request").document(requestId)
            val requestDoc = transaction.get(requestRef)
            val friendRequest = requestDoc.toObject(FriendRequest::class.java)
                ?: throw Exception("Friend request not found")

            if (friendRequest.status != FriendRequestStatus.PENDING) {
                return@runTransaction
            }

            val senderId = friendRequest.senderId
            val receiverId = friendRequest.receiverId

            val senderRef = db.collection("users").document(senderId)
            val receiverRef = db.collection("users").document(receiverId)

            val senderUser = transaction.get(senderRef).toObject(User::class.java)
                ?: throw Exception("Sender user not found")
            val receiverUser = transaction.get(receiverRef).toObject(User::class.java)
                ?: throw Exception("Receiver user not found")

            // 1. Update friend request status
            transaction.update(requestRef, "status", FriendRequestStatus.ACCEPTED)
            transaction.update(requestRef, "acceptedAt", Timestamp.now())

            // 2. Create conversation
            val conversationRef = db.collection("conversations").document()
            val conversationId = conversationRef.id
            val conversation = Conversation(
                id = conversationId,
                members = listOf(senderId, receiverId),
                lastUpdate = Timestamp.now(),
                unreadMessage = mapOf(senderId to 0,requestId to 0),
            )
            transaction.set(conversationRef, conversation)

            // 3. Create friendship for sender
            val senderFriendShipRef = senderRef.collection("friends").document(receiverId)
            val senderFriendShip = FriendShip(
                id = receiverId,
                friendId = receiverId,
                friendFirstName = receiverUser.username,
                createdAt = Timestamp.now(),
                conversationId = conversationId,
                status = FriendShipStatus.ACTIVE
            )
            transaction.set(senderFriendShipRef, senderFriendShip)

            // 4. Create friendship for receiver
            val receiverFriendShipRef = receiverRef.collection("friends").document(senderId)
            val receiverFriendShip = FriendShip(
                id = senderId,
                friendId = senderId,
                friendFirstName = senderUser.username,
                createdAt = Timestamp.now(),
                conversationId = conversationId,
                status = FriendShipStatus.ACTIVE
            )
            transaction.set(receiverFriendShipRef, receiverFriendShip)
        }.await()
    }

    suspend fun cancelFriendRequest(requestId: String) {
        db.collection("friends_request").document(requestId)
            .update("status", FriendRequestStatus.CANCELED)
            .await()
    }

    suspend fun rejectFriendRequest(requestId: String) {
        db.collection("friends_request").document(requestId)
            .update("status", FriendRequestStatus.REJECTED)
            .await()
    }
}
