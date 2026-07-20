package com.project.core.model.firebase

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val uid: String = "",
    val avatar: String = "",
    val email: String = "",
    val language: String = "",
    val username: String = ""
)