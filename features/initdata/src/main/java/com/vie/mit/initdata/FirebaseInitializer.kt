package com.vie.mit.initdata

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseInitializer {
    fun init() {
        // Đọc file từ classloader (thư mục resources)
        val serviceAccount = this::class.java.classLoader.getResourceAsStream("service-account.json")
            ?: throw Exception("Không tìm thấy file service-account.json trong thư mục resources!")

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
    }
}