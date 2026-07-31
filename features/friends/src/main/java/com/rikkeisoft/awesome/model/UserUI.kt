package com.rikkeisoft.awesome.model

import com.project.core.model.firebase.User

sealed class UserUI {
    data class UserItem(
        val id: String = "",
        val avatar: String = "",
        val username: String = "",
        val firstName: String = "",
        val userStatus: UserStatus? = null
    ) : UserUI() {
        constructor(user: User, userStatus: UserStatus) : this(
            id = user.uid,
            avatar = user.avatar,
            username = user.username,
            firstName = user.firstName,
            userStatus = userStatus
        )
    }

    data class AlphabetHeader(val title: String) : UserUI()
}

