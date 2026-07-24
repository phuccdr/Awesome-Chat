package com.rikkeisoft.awesome

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.model.firebase.User
import kotlinx.coroutines.tasks.await

class UserPagingSource(
    private val db: FirebaseFirestore
) : PagingSource<DocumentSnapshot, User>() {
    companion object {
        val PAGE_SIZE = 32
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, User> {
        return try {
            var query = db.collection("users").orderBy("username").limit(PAGE_SIZE.toLong())
            params.key?.let {
                query = query.startAfter(it)
            }
            val snapshot = query.get().await()
            val users = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)
            }
            LoadResult.Page(
                data = users, prevKey = null, nextKey = snapshot.documents.lastOrNull()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, User>): DocumentSnapshot? {
        return state.pages.last().nextKey
    }

}