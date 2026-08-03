package com.project.core.model.firebase

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val avatar: String = "",
    val email: String = "",
    val language: String = "",
    val username: String = "",
    val firstName: String = "",
    val phoneNumber: String = "",
    val birthOfDay: Timestamp? = null,
)