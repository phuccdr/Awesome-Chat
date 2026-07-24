package com.rikkeisoft.awesome.model

import com.project.core.model.firebase.User

sealed class UserUI {
    data class UserItem(
        val id: String = "", val avatar: String = "", val username: String = ""
    ) : UserUI() {
        constructor(user: User) : this(
            id = user.uid, username = user.username, avatar = user.avatar
        )
    }

    data class AlphabetHeader(val title: String) : UserUI()

    fun toUserItem(user: User): UserItem {
        return UserItem(
            id = user.uid, avatar = user.avatar, username = user.username
        )
    }

}

