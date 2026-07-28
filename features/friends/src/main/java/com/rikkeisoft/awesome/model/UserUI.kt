package com.rikkeisoft.awesome.model

import com.project.core.model.firebase.User

sealed class UserUI {
    data class UserItem(
        val id: String = "",
        val avatar: String = "",
        val username: String = "",
        val isFriend: Boolean = false
    ) : UserUI() {
        constructor(user: User, isFriend: Boolean = false) : this(
            id = user.uid, username = user.username, avatar = user.avatar, isFriend = isFriend
        )
    }

    data class AlphabetHeader(val title: String) : UserUI()
}

