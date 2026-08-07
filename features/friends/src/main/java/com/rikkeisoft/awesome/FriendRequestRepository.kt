package com.rikkeisoft.awesome

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.model.firebase.Conversation
import com.project.core.model.firebase.ConversationStatus
import com.project.core.model.firebase.FriendRequest
import com.project.core.model.firebase.FriendRequestStatus
import com.project.core.model.firebase.FriendShip
import com.project.core.model.firebase.FriendShipStatus
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.friendrequest.ReceivedFriendRequestPagingSource
import com.rikkeisoft.awesome.friendrequest.SentFriendRequestPagingSource
import com.rikkeisoft.awesome.model.FriendRequestUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FriendRequestRepository @Inject constructor(
    private val db: FirebaseFirestore, private val auth: FirebaseAuth
) {
    fun loadReceivedFriendRequest(): Flow<PagingData<FriendRequestUI>> {
        return Pager(
            config = PagingConfig(
                pageSize = ReceivedFriendRequestPagingSource.PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = ReceivedFriendRequestPagingSource.PAGE_SIZE
            ), pagingSourceFactory = {
                ReceivedFriendRequestPagingSource(db, auth.currentUser?.uid)
            }).flow
    }

    fun loadSentFriendRequest(onInvalidated: (() -> Unit)? = null): Flow<PagingData<FriendRequestUI>> {
        return Pager(
            config = PagingConfig(
                pageSize = SentFriendRequestPagingSource.PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = SentFriendRequestPagingSource.PAGE_SIZE
            ), pagingSourceFactory = {
                SentFriendRequestPagingSource(db, auth.currentUser?.uid, onInvalidated)
            }).flow
    }
    suspend fun sendFriendRequest(receiverId: String): String {
        return withContext(Dispatchers.IO) {
            val senderId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val requestId = "${senderId}-$receiverId"
            val requestRef = db.collection("friends_request").document(requestId)
            val friendRequest = FriendRequest(
                id = requestId,
                senderId = senderId,
                receiverId = receiverId,
                status = FriendRequestStatus.PENDING,
                createdAt = Timestamp.now()
            )
            requestRef.set(friendRequest).await()
            Timber.d("sendFriendRequest: requestId: $requestId")
            requestId
        }
    }

    suspend fun acceptFriendRequest(requestId: String) {
        withContext(Dispatchers.IO) {
            db.runTransaction { transaction ->
                val requestRef = db.collection("friends_request").document(requestId)
                val requestDoc = transaction.get(requestRef)
                val friendRequest = requestDoc.toObject(FriendRequest::class.java)
                    ?: throw Exception("Friend request not found")

                if (friendRequest.status != FriendRequestStatus.PENDING) {
                    return@runTransaction
                }
                Timber.d("friendRequest: $friendRequest")
                val senderId = friendRequest.senderId
                val receiverId = friendRequest.receiverId

                val senderRef = db.collection("users").document(senderId)
                val receiverRef = db.collection("users").document(receiverId)

                val senderUser = transaction.get(senderRef).toObject(User::class.java)
                    ?: throw Exception("Sender user not found")
                val receiverUser = transaction.get(receiverRef).toObject(User::class.java)
                    ?: throw Exception("Receiver user not found")

                // Pre-read conversation (Reads must come before writes)
                val members = listOf(senderId, receiverId).sorted()
                val conversationId = members.joinToString("_")
                val conversationRef = db.collection("conversations").document(conversationId)
                val conversationDoc = transaction.get(conversationRef)

                // 1. Update friend request status
                transaction.update(requestRef, "status", FriendRequestStatus.ACCEPTED)
                transaction.update(requestRef, "acceptedAt", Timestamp.now())

                // 2. Create or update conversation
                if (conversationDoc.exists()) {
                    transaction.update(conversationRef, "status", ConversationStatus.ACTIVE)
                    transaction.update(conversationRef, "lastUpdate", Timestamp.now())
                } else {
                    val conversation = Conversation(
                        id = conversationId,
                        members = members,
                        lastUpdate = Timestamp.now(),
                        unreadMessage = members.associateWith { 0 },
                        status = ConversationStatus.ACTIVE
                    )
                    transaction.set(conversationRef, conversation)
                }

                // 3. Create/Overwrite friendship for sender
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

                // 4. Create/Overwrite friendship for receiver
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
    }

    suspend fun cancelFriendRequest(requestId: String) {
        withContext(Dispatchers.IO){
            Timber.d("cancelFriendRequest: $requestId")
        db.collection("friends_request").document(requestId)
            .update("status", FriendRequestStatus.CANCELED)
            .await()}
    }

    suspend fun rejectFriendRequest(requestId: String) {
        withContext(Dispatchers.IO){
        db.collection("friends_request").document(requestId)
            .update("status", FriendRequestStatus.REJECTED)
            .await()}
    }
}
